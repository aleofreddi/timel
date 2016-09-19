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

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the IntervalOf function.
 *
 * @author Andrea Leofreddi
 */
public class IntervalOfImpl extends AbstractValueFunction<Interval> {
    public static final String TOKEN = "IntervalOf";

    private final List<ValueNode<?>> valueNodes;

    public IntervalOfImpl(List<ValueNode<?>> valueNodes) {
        super(TOKEN, Types.getIntervalType(), valueNodes);

        this.valueNodes = valueNodes;
    }

    @Override
    public UpscalableIterator<Interval> evaluate(Interval interval, ExecutorContext context) {
        List<UpscalableIterator<?>> iterators = new ArrayList<UpscalableIterator<?>>(valueNodes.size());

        for(ValueNode<?> valueNode : valueNodes)
            iterators.add(valueNode.evaluate(interval, context));

        return new UpscalerWrapperIterator<Interval>(
                getType().getUpscaler(),
                // (1) Intersect all arguments
                new AdapterTimeIterator<Object[], Interval>(
                        new IntersectIterator(iterators)
                ) {
                    // (2) Return the intersection interval
                    @Override
                    protected Sample<Interval> adapt(Sample<Object[]> sample) {
                        return Sample.of(
                                sample.getInterval(),
                                sample.getInterval()
                        );
                    }
                }
        );
    }
}
