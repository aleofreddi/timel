package net.vleo.timel.iterator;

/*-
 * #%L
 * TimEL core
 * %%
 * Copyright (C) 2015 - 2019 Andrea Leofreddi
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.impl.upscaler.SameUpscaler;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.IntervalMaps;
import net.vleo.timel.time.Sample;
import net.vleo.timel.variable.Variable;

import java.util.Optional;

/**
 * A time iterator used to fill the gaps between the backend variable and the
 * requested interval.
 *
 * @author Andrea Leofreddi
 */
public class GapEvaluatorTimeIterator<V> extends BufferedTimeIterator<Optional<V>> {
    private final Variable<V> variable;
    private final UpscalableIterator<V> backend;
    private final TimeIterator<Optional<V>> delegate;
    private final ExecutorContext context;

    private CoalesceIterator<Optional<V>> evalItor;

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
        this.delegate = new CoalesceIterator<Optional<V>>(
                new AdapterTimeIterator<V, Optional<V>>(
                        variable.readForward(interval, context)
                ) {
                    @Override
                    protected Sample<Optional<V>> adapt(Sample<V> sample) {
                        return sample.copyWithValue(
                                Optional.ofNullable(sample.getValue())
                        );
                    }
                },
                new UpscalerIterator<Optional<V>>(
                        SameUpscaler.<Optional<V>>get(),
                        IntervalMaps.iterator(
                                IntervalMaps.of(
                                        Sample.of(
                                                interval,
                                                Optional.empty()
                                        )
                                )
                        )
                )
        );

        time = interval.getStart();
    }

    @Override
    protected Sample<Optional<V>> concreteNext() {
        if(evalItor != null) {
            // We are fillig a gap!
            if(evalItor.hasNext()) {
                Sample<Optional<V>> sample = evalItor.next();

                // Store value
                variable.write(
                        sample.copyWithValue(sample.getValue().orElse(null)),
                        context
                );

                return sample;
            } else
                // We have nothing more to evaluate, go back to normal mode
                evalItor = null;
        }

        if(!delegate.hasNext())
            return null;

        Sample<Optional<V>> value = delegate.next();

        if(value != null && !value.getValue().isPresent()) {
            Interval evalInterval = value.getInterval();

            // We've got a gap to evaluate

            // Try to fill it using coalesce(chop(backend), null)
            evalItor = new CoalesceIterator<Optional<V>>(
                    new AdapterTimeIterator<V, Optional<V>>(
                            new ChopUpscalableIterator<V>(
                                    backend,
                                    evalInterval
                            )
                    ) {
                        @Override
                        protected Sample<Optional<V>> adapt(Sample<V> sample) {
                            return Sample.of(
                                    sample.getInterval(),
                                    Optional.ofNullable(sample.getValue())
                            );
                        }
                    },
                    new UpscalerIterator<Optional<V>>(
                            SameUpscaler.<Optional<V>>get(),
                            IntervalMaps.iterator(
                                    IntervalMaps.of(
                                            Sample.of(
                                                    evalInterval,
                                                    Optional.empty()
                                            )
                                    )
                            )
                    )
            );

            // Due to the coalesce, we're sure there's always at least one value
            value = evalItor.next();

            // Store value
            variable.write(
                    value.copyWithValue(value.getValue().orElse(null)),
                    context
            );

            return value;
        }

        return value;
    }
}
