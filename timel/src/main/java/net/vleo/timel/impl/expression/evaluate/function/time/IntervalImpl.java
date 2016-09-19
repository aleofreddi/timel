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
package net.vleo.timel.impl.expression.evaluate.function.time;

import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.iterator.IntersectIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.Types;

/**
 * Implementation of the Interval function.
 *
 * @author Andrea Leofreddi
 */
public class IntervalImpl extends AbstractValueFunction<Interval> {
    public static final String TOKEN = "Interval";

    private final ValueNode<Long> startNode, stopNode;

    public IntervalImpl(ValueNode<Long> startNode, ValueNode<Long> stopNode) {
        super(TOKEN, Types.getIntervalType(), startNode, stopNode);

        this.startNode = startNode;
        this.stopNode = stopNode;
    }

    @Override
    public UpscalableIterator<Interval> evaluate(Interval interval, ExecutorContext context) {
        return new UpscalerWrapperIterator<Interval>(
                getType().getUpscaler(),
                new AdapterTimeIterator<Object[], Interval>(
                        new IntersectIterator(
                                startNode.evaluate(interval, context),
                                stopNode.evaluate(interval, context)
                        )
                ) {
                    @Override
                    protected Sample<Interval> adapt(Sample<Object[]> sample) {
                        Object[] values = sample.getValue();

                        Long start = (Long)values[0], stop = (Long)values[1];

                        return sample.copyWithValue(Interval.of(start, stop));
                    }
                }
        );
    }
}
