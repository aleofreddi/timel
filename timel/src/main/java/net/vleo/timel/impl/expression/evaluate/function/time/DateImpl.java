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

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.iterator.NestedLoopTimeIterator;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.IntervalMaps;
import net.vleo.timel.type.TimeType;
import net.vleo.timel.type.Types;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.SortedMap;

/**
 * Date function implementation.
 *
 * @author Andrea Leofreddi
 */
public class DateImpl extends AbstractValueFunction<Long> {
    public static final String TOKEN = "Date";

    private static final DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

    private ValueNode<String> value;

    public DateImpl(ValueNode<String> value) {
        super(TOKEN, Types.getTimeType(), value);

        this.value = value;
    }

    @Override
    public UpscalableIterator<Long> evaluate(Interval interval, ExecutorContext context) {
        return new UpscalerWrapperIterator<Long>(
                getType().getUpscaler(),
                new NestedLoopTimeIterator<String, Long>(value.evaluate(interval, context)) {
                    @Override
                    protected TimeIterator<Long> nestedIterator(Sample<String> s) {
                        Interval interval = s.getInterval();

                        long t = fmt.parseDateTime(s.getValue()).getMillis();

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

                        return IntervalMaps.iterator(map);
                    }
                }
        );
    }
}
