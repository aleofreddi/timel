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
package net.vleo.timel.impl.expression.evaluate.constant;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.IntervalMaps;
import net.vleo.timel.type.TimeType;
import net.vleo.timel.type.Types;

import java.util.SortedMap;

/**
 * Stop function implementation. This function returns the stop date of the evaluation period.
 *
 * @author Andrea Leofreddi
 */
public class EvalStopImpl extends AbstractValueFunction<Long> implements ValueNode<Long> {
    public static final String TOKEN = "EvalStop";

    public EvalStopImpl() {
        super(TOKEN, Types.getTimeType());
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public UpscalableIterator<Long> evaluate(Interval interval, ExecutorContext context) {
        long t = context.getInterval().getEnd();

        SortedMap<Interval, Long> map;

        // If interval is crossing the eval date, split the interval so we are sure
        // that any comparison operation will behave consistently
        if(interval.contains(t) && interval.getStart() != t) {
            map = IntervalMaps.of(
                    Sample.of(
                            Interval.of(
                                    interval.getStart(),
                                    t
                            ),
                            t
                    ),

                    Sample.of(
                            Interval.of(
                                    t,
                                    interval.getEnd()
                            ),
                            t
                    )
            );
        } else
            map = IntervalMaps.of(
                    Sample.of(
                            interval,
                            t
                    )
            );

        return new UpscalerWrapperIterator<Long>(
                getType().getUpscaler(),
                IntervalMaps.iterator(map)
        );
    }
}
