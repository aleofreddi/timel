package net.vleo.timel.impl.function.time;

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

import net.vleo.timel.EvaluationException;
import net.vleo.timel.annotation.FunctionPrototype;
import net.vleo.timel.annotation.FunctionPrototypes;
import net.vleo.timel.annotation.Parameter;
import net.vleo.timel.annotation.Returns;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.function.Function;
import net.vleo.timel.impl.downscaler.Downscaler;
import net.vleo.timel.impl.target.Evaluable;
import net.vleo.timel.impl.time.CalendarFields;
import net.vleo.timel.impl.time.periodicity.CyclicPeriodicity;
import net.vleo.timel.impl.time.periodicity.IntervalPeriodicity;
import net.vleo.timel.impl.time.periodicity.IntervalPeriodicityWrapper;
import net.vleo.timel.impl.time.periodicity.TimeZoneWrappedPeriodicity;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.*;
import net.vleo.timel.time.CalendarField;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.Sample;
import net.vleo.timel.type.IntegerType;
import net.vleo.timel.type.StringType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Extract a timestamp field.
 *
 * @author Andrea Leofreddi
 */
@FunctionPrototypes({
        @FunctionPrototype(
                name = "extract",
                returns = @Returns(type = IntegerType.class),
                parameters = {
                        @Parameter(type = StringType.class),
                        @Parameter(type = StringType.class)
                }
        )
})
public class ExtractFunction implements Function<Integer> {
    @Override
    public UpscalableIterator<Integer> evaluate(Interval interval, ExecutorContext context, Upscaler<Integer> upscaler, Downscaler<Integer> downscaler, Evaluable<?>... arguments) {
        return new UpscalerIterator<>(
                upscaler,
                new NestedLoopTimeIterator<Object[], Integer>(
                        new IntersectIterator(
                                Function.evaluateAll(interval, context, arguments)
                        )
                ) {
                    @Override
                    protected TimeIterator<Integer> nestedIterator(Sample<Object[]> value) {
                        Interval interval = value.getInterval();

                        Object[] values = value.getValue();
                        String field = (String) values[0], timeZoneId = (String) values[1];

                        CalendarField calendarField;
                        try {
                            calendarField = CalendarField.valueOf(field);
                        } catch(IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid calendar field '" + field + "'");
                        }

                        final DateTimeZone dateTimeZone;
                        try {
                            dateTimeZone = DateTimeZone.forID(timeZoneId);
                        } catch(IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid time zone '" + timeZoneId + "'");
                        }

                        IntervalPeriodicity periodicity = new IntervalPeriodicityWrapper(
                                new TimeZoneWrappedPeriodicity(
                                        new CyclicPeriodicity(calendarField, 1),
                                        dateTimeZone
                                )
                        );

                        return new SampleAdapterTimeIterator<>(
                                periodicity.supremumIter(interval),
                                period -> {
                                    DateTime begin = new DateTime(period.getInterval().getStart()).withZone(dateTimeZone);
                                    return period.copyWithValue(begin.get(CalendarFields.getFieldType(calendarField)));
                                }
                        );
                    }
                }
        );
    }
}
