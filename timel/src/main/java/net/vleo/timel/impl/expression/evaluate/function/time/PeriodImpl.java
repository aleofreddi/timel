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
import net.vleo.timel.executor.ExecutionException;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.impl.time.PeriodFields;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.PeriodField;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.iterator.IntersectIterator;
import net.vleo.timel.type.PeriodType;
import net.vleo.timel.type.Types;
import org.joda.time.Period;

/**
 * Period function implementation.
 *
 * @author Andrea Leofreddi
 */
public class PeriodImpl extends AbstractValueFunction<String> {
    public static final String TOKEN = "Period";

    private final ValueNode<String> fieldNode;

    private final ValueNode<Double> sizeNode;

    public PeriodImpl(ValueNode<Double> sizeNode, ValueNode<String> fieldNode) {
        super(TOKEN, Types.getPeriodType(), sizeNode, fieldNode);

        this.sizeNode = sizeNode;
        this.fieldNode = fieldNode;
    }

    @Override
    public UpscalableIterator<String> evaluate(Interval interval, ExecutorContext context) {
        return new UpscalerWrapperIterator<String>(
                getType().getUpscaler(),
                new AdapterTimeIterator<Object[], String>(
                        new IntersectIterator(
                                sizeNode.evaluate(interval, context),
                                fieldNode.evaluate(interval, context)
                        )
                ) {
                    @Override
                    protected Sample<String> adapt(Sample<Object[]> sample) {
                        double size = (Double) sample.getValue()[0];

                        String field = (String) sample.getValue()[1];

                        PeriodField periodField;

                        if(Math.floor(size) != size)
                            throw new ExecutionException("Period size must be an integer");

                        try {
                            periodField = PeriodField.valueOf(field);
                        } catch(IllegalArgumentException e) {
                            throw new ExecutionException("Invalid period field " + field);
                        }

                        return sample.copyWithValue(
                                PeriodFields.periodOf(
                                        (int) size,
                                        periodField
                                ).toString()
                        );
                    }
                }
        );
    }
}
