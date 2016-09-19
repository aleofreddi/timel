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
package net.vleo.timel.impl.expression.evaluate.function.math;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.executor.ExecutionException;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.iterator.IntersectIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.NumericDoubleType;
import net.vleo.timel.type.NumericType;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.type.Types;

/**
 * Modulus implementation.
 *
 * @author Andrea Leofreddi
 */
public class ModImpl extends AbstractValueFunction<Double> {
    public static final String TOKEN = "Mod";

    private ValueNode<Double> dividendNode, divisorNode;

    public ModImpl(ValueNode<Double> dividendNode, ValueNode<Double> divisorNode) {
        super(TOKEN, Types.getNumericDoubleType(), dividendNode, divisorNode);

        this.dividendNode = dividendNode;
        this.divisorNode = divisorNode;
    }

    @Override
    public UpscalableIterator<Double> evaluate(Interval interval, ExecutorContext context) {
        return new UpscalerWrapperIterator<Double>(
                getType().getUpscaler(),
                new AdapterTimeIterator<Object[], Double>(
                        new IntersectIterator(
                                dividendNode.evaluate(interval, context),
                                divisorNode.evaluate(interval, context)
                        )
                ) {
                    @Override
                    protected Sample<Double> adapt(Sample<Object[]> sample) {
                        Object[] values = sample.getValue();

                        double dividend = (Double)values[0], divisor = (Double)values[1];

                        if(Math.floor(dividend) != dividend)
                            throw new ExecutionException("Dividend argument must be an integer value");

                        if(Math.floor(divisor) != divisor)
                            throw new ExecutionException("Divisor argument must be an integer value");

                        return sample.copyWithValue((double)((long)dividend % (long)divisor));
                    }
                }
        );
    }
}
