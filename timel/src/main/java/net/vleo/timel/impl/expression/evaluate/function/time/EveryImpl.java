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
import net.vleo.timel.executor.ExecutionException;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.evaluate.constant.SimpleConstantNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.impl.time.periodicity.*;
import net.vleo.timel.iterator.*;
import net.vleo.timel.time.*;
import net.vleo.timel.type.Types;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;

/**
 * Implementation of the Every function.
 *
 * @author Andrea Leofreddi
 */
public class EveryImpl extends AbstractValueFunction<Interval> {
    public static final String TOKEN = "Every";

    private final ValueNode<String> fieldNode, timeZoneIdNode;

    private final ValueNode<Double> sizeNode;

    private final ValueNode<String> phaseNode;

    public EveryImpl(ValueNode<Double> sizeNode, ValueNode<String> fieldNode, ValueNode<String> phaseNode, ValueNode<String> timeZoneIdNode) {
        super(TOKEN, Types.getIntervalType(), sizeNode, fieldNode, phaseNode, timeZoneIdNode);

        this.sizeNode = sizeNode;
        this.fieldNode = fieldNode;
        this.phaseNode = phaseNode;
        this.timeZoneIdNode = timeZoneIdNode;
    }

    public EveryImpl(ValueNode<Double> sizeNode, ValueNode<String> fieldNode, ValueNode<String> timeZoneIdNode) {
        super(TOKEN, Types.getIntervalType(), sizeNode, fieldNode, timeZoneIdNode);

        this.sizeNode = sizeNode;
        this.fieldNode = fieldNode;
        this.phaseNode = new SimpleConstantNode<String>(Types.getPeriodType(), "PT0S") { };
        this.timeZoneIdNode = timeZoneIdNode;
    }

    @Override
    public UpscalableIterator<Interval> evaluate(Interval interval, ExecutorContext context) {
        return new UpscalerWrapperIterator<Interval>(
                getType().getUpscaler(),
                // (1) For every values in size, field an tz
                new NestedLoopTimeIterator<Object[], Interval>(
                        new IntersectIterator(
                                sizeNode.evaluate(interval, context),
                                fieldNode.evaluate(interval, context),
                                phaseNode.evaluate(interval, context),
                                timeZoneIdNode.evaluate(interval, context)
                        )
                ) {
                    @Override
                    protected TimeIterator<Interval> nestedIterator(Sample<Object[]> value) {
                        final Interval interval = value.getInterval();

                        Object[] values = value.getValue();

                        double size = (Double) values[0];

                        String field = (String) values[1];

                        String phaseStr = (String) values[2];

                        String timeZoneId = (String) values[3];

                        Period phase = new Period(phaseStr);

                        CalendarField calendarField;

                        try {
                            calendarField = CalendarField.valueOf(field);
                        } catch(IllegalArgumentException e) {
                            throw new ExecutionException("Invalid field specified: " + field);
                        }

                        DateTimeZone dateTimeZone = DateTimeZone.forID(timeZoneId);

                        if(Math.floor(size) != size || size < 1)
                            throw new ExecutionException("Size argument of periodicity should be a positive integer");

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
