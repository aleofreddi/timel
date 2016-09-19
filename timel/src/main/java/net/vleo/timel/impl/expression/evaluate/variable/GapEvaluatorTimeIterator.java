/*
 * Copyright 2014-2016 Andrea Leofreddi
 *
 * This file is part of TimEL.
 *
 * TimEL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TimEL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TimEL.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.vleo.timel.impl.expression.evaluate.variable;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.executor.variable.Variable;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.iterator.*;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.IntervalMaps;
import net.vleo.timel.impl.upscaler.SameUpscaler;

/**
 * A time iterator used to fill the gaps between the backend variable and the
 * requested interval.
 *
 * @author Andrea Leofreddi
 */
class GapEvaluatorTimeIterator<V> extends BufferedTimeIterator<VariablePayload<V>> {
    private final Variable<V> variable;

    private final UpscalableIterator<V> backend;

    private final TimeIterator<VariablePayload<V>> delegate;

    private final ExecutorContext context;

    private CoalesceIterator<VariablePayload<V>> evalItor;

    private long time;

    public GapEvaluatorTimeIterator(
            Variable<V> variable,
            UpscalableIterator<V> backend,
            Interval interval,
            ExecutorContext context
    ) {
        this.variable = variable;
        this.backend = backend;
        this.context = context;

        // Get the read iterator as Coalesce[Variable, TO_EVALUATE]
        this.delegate = new CoalesceIterator<VariablePayload<V>>(
                new AdapterTimeIterator<V, VariablePayload<V>>(
                        variable.readForward(interval, context)
                ) {
                    @Override
                    protected Sample<VariablePayload<V>> adapt(Sample<V> sample) {
                        return sample.copyWithValue(
                                new VariablePayload<V>(
                                        sample.getValue(),
                                        VariablePayload.Status.DEFINED
                                )
                        );
                    }
                },
                new UpscalerWrapperIterator<VariablePayload<V>>(
                        SameUpscaler.<VariablePayload<V>>get(),
                        IntervalMaps.iterator(
                                IntervalMaps.of(
                                        Sample.of(
                                                interval,
                                                new VariablePayload<V>(null, VariablePayload.Status.TO_EVALUTE)

                                        )
                                )
                        )
                )
        );

        time = interval.getStart();
    }

    @Override
    protected Sample<VariablePayload<V>> concreteNext() {
        if(evalItor != null) {
            // We are fillig a gap!
            if(evalItor.hasNext()) {
                Sample<VariablePayload<V>> sample = evalItor.next();

                // Store value
                variable.write(
                        sample.copyWithValue(sample.getValue().getPayload()),
                        context
                );

                return sample;
            } else
                // We have nothing more to evaluate, go back to normal mode
                evalItor = null;
        }

        if(!delegate.hasNext())
            return null;

        Sample<VariablePayload<V>> value = delegate.next();

        if(value != null && value.getValue().getStatus() == VariablePayload.Status.TO_EVALUTE) {
            Interval evalInterval = value.getInterval();

            // We've got a gap to evaluate

            // Try to fill it using coalesce(chop(backend), null)
            evalItor = new CoalesceIterator<VariablePayload<V>>(
                    new AdapterTimeIterator<V, VariablePayload<V>>(
                            new ChopUpscalableIterator<V>(
                                    backend,
                                    evalInterval
                            )
                    ) {
                        @Override
                        protected Sample<VariablePayload<V>> adapt(Sample<V> sample) {
                            return Sample.of(
                                    sample.getInterval(),
                                    new VariablePayload<V>(sample.getValue(), VariablePayload.Status.DEFINED)
                            );
                        }
                    },
                    new UpscalerWrapperIterator<VariablePayload<V>>(
                            SameUpscaler.<VariablePayload<V>>get(),
                            IntervalMaps.iterator(
                                    IntervalMaps.of(
                                            Sample.of(
                                                    evalInterval,
                                                    new VariablePayload<V>(null, VariablePayload.Status.DEFINED)

                                            )
                                    )
                            )
                    )
            );

            // Due to the coalesce, we're sure there's always at least one value
            value = evalItor.next();

            // Store value
            variable.write(
                    value.copyWithValue(value.getValue().getPayload()),
                    context
            );

            return value;
        }

        return value;
    }
}
