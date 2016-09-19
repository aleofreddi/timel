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
package net.vleo.timel.impl.expression.evaluate.function.eval;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.ExecutionException;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.iterator.NestedLoopTimeIterator;
import net.vleo.timel.time.Interval;
import org.joda.time.Duration;
import org.joda.time.Period;

/**
 * Max function for DateTime expressions
 *
 * @author Andrea Leofreddi
 */
public class ShiftImpl<V> extends AbstractValueFunction<V> {
    public static final String TOKEN = "Shift";

    private final ValueNode<V> valueNode;

    private final ValueNode<String> periodNode;

    private static Interval translate(Interval interval, Duration duration) {
        return Interval.of(
                interval.getStart() + duration.getMillis(),
                interval.getEnd() + duration.getMillis()
        );
    }

    public ShiftImpl(ValueNode<V> valueNode, ValueNode<String> periodNode) {
        super(ShiftImpl.TOKEN, valueNode.getType(), valueNode, periodNode);

        this.valueNode = valueNode;
        this.periodNode = periodNode;
    }

    @Override
    public UpscalableIterator<V> evaluate(final Interval interval, final ExecutorContext context) {
        return new UpscalerWrapperIterator<V>(
                getType().getUpscaler(),
                new NestedLoopTimeIterator<String, V>(
                        periodNode.evaluate(interval, context)
                ) {
                    @Override
                    protected TimeIterator<V> nestedIterator(Sample<String> s) {
                        // (1) For each period vale
                        Period period = new Period(s.getValue());

                        Duration duration;

                        try {
                            duration = period.toStandardDuration();
                        } catch(UnsupportedOperationException e) {
                            throw new ExecutionException("Unable to apply shift: non constant duration period provided");
                        }

                        final Duration negDuration = duration.negated();

                        return new AdapterTimeIterator<V, V>(
                                // (2) Evaluate value node using the shifted interval
                                valueNode.evaluate(
                                        translate(interval, duration),
                                        context
                                )
                        ) {
                            @Override
                            protected Sample<V> adapt(Sample<V> sample) {
                                // (3) Shift back the results
                                return sample.copyWithInterval(
                                        translate(sample.getInterval(), negDuration)
                                );
                            }
                        };
                    }
                }
        );
    }
}
