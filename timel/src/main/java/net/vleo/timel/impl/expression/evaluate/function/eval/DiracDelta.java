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

import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.impl.expression.evaluate.function.resample.ResampleFactory;
import net.vleo.timel.impl.expression.evaluate.function.resample.SimpleDownscalerImpl;
import net.vleo.timel.iterator.SplitAdapterTimeIterator;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.Types;

import java.util.Arrays;
import java.util.List;

/**
 * Implementation of the Dirac delta function, with a scale factor.
 *
 * @author Andrea Leofreddi
 */
public class DiracDelta extends SimpleDownscalerImpl<Double> {
    public static final String TOKEN = "DiracDelta";

    private final ValueNode<Double> valueNode;

    public DiracDelta(ValueNode<Double> valueNode, ValueNode<Interval> periodicityNode) throws ParseException {
        super(TOKEN, Types.getIntegralDoubleType(1), valueNode, periodicityNode);

        this.valueNode = valueNode;
    }

    public DiracDelta(ValueNode<Double> valueNode) throws ParseException {
        super(TOKEN, valueNode.getType(), valueNode);

        this.valueNode = valueNode;
    }

    @Override
    protected TimeIterator<Double> getDownscaler(Interval interval, ExecutorContext context) {
        return new SplitAdapterTimeIterator<Double, Double>(
                // (1) Scalarize value in the given interval
                ResampleFactory.getDownscaler(
                        valueNode.getType(),
                        valueNode.evaluate(interval, context),
                        interval
                )
        ) {
            @Override
            protected List<Sample<Double>> adapt(Sample<Double> sample) {
                if(sample.getInterval().toDurationMillis() == 1L)
                    // We already have an impulse
                    return Arrays.asList(sample);

                Interval i = sample.getInterval();

                long s = i.getStart(), e = i.getEnd();

                return Arrays.asList(
                        Sample.of(Interval.of(s, s + 1), sample.getValue()),
                        Sample.of(Interval.of(s + 1, e), 0.0)
                );
            }
        };
    }
}
