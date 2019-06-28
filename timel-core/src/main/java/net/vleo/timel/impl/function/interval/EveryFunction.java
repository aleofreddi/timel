package net.vleo.timel.impl.function.interval;

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

import net.vleo.timel.annotation.Parameter;
import net.vleo.timel.annotation.FunctionPrototype;
import net.vleo.timel.annotation.Returns;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.function.Function;
import net.vleo.timel.impl.downscaler.Downscaler;
import net.vleo.timel.impl.target.Evaluable;
import net.vleo.timel.impl.time.periodicity.*;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.*;
import net.vleo.timel.time.CalendarField;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.Sample;
import net.vleo.timel.type.IntegerType;
import net.vleo.timel.type.IntervalType;
import net.vleo.timel.type.StringType;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;

/**
 * Implementation of the every function.
 *
 * @author Andrea Leofreddi
 */
@FunctionPrototype(
        name = "every",
        returns = @Returns(type = IntervalType.class),
        parameters = {
                @Parameter(type = IntegerType.class),
                @Parameter(type = StringType.class),
                @Parameter(type = StringType.class),
                //@Parameter(type = StringType.class), // FIXME - optional phase here, defaults t PT0S
        }
)
public class EveryFunction implements Function<Interval> {
    @Override
    public UpscalableIterator<Interval> evaluate(Interval interval, ExecutorContext context, Upscaler<Interval> upscaler, Downscaler<Interval> downscaler, Evaluable<?>... arguments) {
        return new UpscalerIterator<>(
                upscaler,
                // (1) For every values in size, field an tz
                new NestedLoopTimeIterator<Object[], Interval>(
                        new IntersectIterator(
                                arguments[0].evaluate(interval, context),
                                arguments[1].evaluate(interval, context),
                                arguments[2].evaluate(interval, context)
                                //                                arguments[3].evaluate(interval, context)
                        )
                ) {
                    @Override
                    protected TimeIterator<Interval> nestedIterator(Sample<Object[]> value) {
                        final Interval interval = value.getInterval();

                        Object[] values = value.getValue();

                        int size = (int) values[0];
                        String field = (String) values[1];
                        String timeZoneId = (String) values[2];
                        //                        String phaseStr = (String) values[2];
                        Period phase = new Period("PT0S");

                        CalendarField calendarField;

                        try {
                            calendarField = CalendarField.valueOf(field);
                        } catch(IllegalArgumentException e) {
                            throw new RuntimeException("Invalid field specified: " + field);
                        }

                        DateTimeZone dateTimeZone = DateTimeZone.forID(timeZoneId);

                        if(Math.floor(size) != size || size < 1)
                            throw new RuntimeException("Size argument of periodicity should be a positive integer");

                        int sizeInt = (int) size;

                        // (2) Retrieve the relative periodicity
                        IntervalPeriodicity periodicity = new IntervalPeriodicityWrapper(
                                new TranslatedPeriodicity(
                                        new TimeZoneWrappedPeriodicity(
                                                new CyclicPeriodicity(calendarField, sizeInt),
                                                dateTimeZone
                                        ),
                                        phase
                                )
                        );

                        // (3) Adapt Sample<Void> to Sample<Interval> setting interval as value
                        return new AdapterTimeIterator<Void, Interval>(periodicity.supremumIter(interval)) {
                            @Override
                            protected Sample<Interval> adapt(Sample<Void> sample) {
                                Interval subInterval = sample.getInterval();

                                return Sample.of(
                                        subInterval.overlap(interval),
                                        subInterval
                                );
                            }
                        };
                    }
                }
        );
    }
}
