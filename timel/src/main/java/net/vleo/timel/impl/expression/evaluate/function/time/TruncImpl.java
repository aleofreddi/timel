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

import net.vleo.timel.executor.Sample;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.impl.expression.compile.DebugFactory;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.impl.time.CalendarFields;
import net.vleo.timel.impl.time.periodicity.CyclicPeriodicity;
import net.vleo.timel.impl.time.periodicity.IntervalPeriodicity;
import net.vleo.timel.impl.time.periodicity.IntervalPeriodicityWrapper;
import net.vleo.timel.impl.time.periodicity.TimeZoneWrappedPeriodicity;
import net.vleo.timel.iterator.*;
import net.vleo.timel.type.TimeType;
import net.vleo.timel.time.*;
import net.vleo.timel.type.Types;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import net.vleo.timel.executor.ExecutionException;

/**
 * DateTime day truncate function implementation.
 *
 * @author Andrea Leofreddi
 */
public class TruncImpl extends AbstractValueFunction<Long> {
    public static final String TOKEN = "Trunc";

    private final ValueNode<Long> valueNode;

    private final ValueNode<String> fieldNode;

    private final ValueNode<String> timeZoneIdNode;

    public TruncImpl(ValueNode<Long> valueNode, ValueNode<String> fieldNode, ValueNode<String> timeZoneNode) {
        super(TOKEN, Types.getTimeType(), valueNode, fieldNode, timeZoneNode);

        this.valueNode = valueNode;
        this.fieldNode = fieldNode;
        this.timeZoneIdNode = timeZoneNode;
    }

    @Override
    public UpscalableIterator<Long> evaluate(Interval interval, final ExecutorContext context) {
        final DebugFactory.DebugBuilder debug = DebugFactory.of(this, context);

        return new UpscalerWrapperIterator<Long>(
                getType().getUpscaler(),

                // (1) For each field/timezone
                debug.build("forEach(field,tz)", interval, new NestedLoopTimeIterator<Object[], Long>(
                        new IntersectIterator(
                                fieldNode.evaluate(interval, context),
                                timeZoneIdNode.evaluate(interval, context)
                        )
                ) {
                    @Override
                    protected TimeIterator<Long> nestedIterator(Sample<Object[]> value) {
                        Interval interval = value.getInterval();

                        Object[] values = value.getValue();

                        String field = (String)values[0], timeZoneId = (String)values[1];

                        final CalendarField calendarField;

                        try {
                            calendarField = CalendarField.valueOf(field);
                        } catch(IllegalArgumentException e) {
                            throw new ExecutionException("Invalid field specified: " + field);
                        }

                        final DateTimeZone dateTimeZone = DateTimeZone.forID(timeZoneId);

                        IntervalPeriodicity periodicity = new IntervalPeriodicityWrapper(
                                new TimeZoneWrappedPeriodicity(
                                        new CyclicPeriodicity(calendarField, 1),
                                        dateTimeZone
                                )
                        );

                        return debug.build("forEach", interval, new NestedLoopTimeIterator<Void, Long>(
                                periodicity.supremumIter(interval)
                        ) {
                            @Override
                            protected TimeIterator<Long> nestedIterator(Sample<Void> value) {
                                return debug.build("trunc", value.getInterval(), new AdapterTimeIterator<Long, Long>(
                                        valueNode.evaluate(value.getInterval(), context)
                                ) {
                                    @Override
                                    protected Sample<Long> adapt(Sample<Long> sample) {
                                        DateTime value = new DateTime(sample.getValue()).withZone(dateTimeZone);

                                        long result = value.property(CalendarFields.getFieldType(calendarField)).roundFloorCopy().getMillis();

                                        return sample.copyWithValue(result);
                                    }
                                });
                            }
                        });

                    }
                })
        );
    }
}
