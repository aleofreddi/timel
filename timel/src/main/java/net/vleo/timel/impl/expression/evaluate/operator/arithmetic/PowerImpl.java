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
package net.vleo.timel.impl.expression.evaluate.operator.arithmetic;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.iterator.IntersectIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.ValueType;

import net.vleo.timel.impl.expression.compile.PowerFactory;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;

/**
 * Implementation of exponentiation operator.
 *
 * @author Andrea Leofreddi
 */
public class PowerImpl extends AbstractValueFunction<Double> {
    private ValueNode<Double> baseNode, exponentNode;

    private static final String FORMAT = "(%s %s %s)";

    public PowerImpl(ValueType<Double> returnType, ValueNode<Double> baseNode, ValueNode<Double> exponentNode) {
        super(PowerFactory.TOKEN, returnType, baseNode, exponentNode);

        this.baseNode = baseNode;
        this.exponentNode = exponentNode;
    }

    @Override
    public UpscalableIterator<Double> evaluate(Interval interval, ExecutorContext context) {
        return new UpscalerWrapperIterator<Double>(
                getType().getUpscaler(),
                new AdapterTimeIterator<Object[], Double>(
                        new IntersectIterator(
                                baseNode.evaluate(interval, context),
                                exponentNode.evaluate(interval, context)
                        )
                ) {
                    @Override
                    protected Sample<Double> adapt(Sample<Object[]> sample) {
                        Object[] values = sample.getValue();

                        Double base = (Double) values[0], exponent = (Double) values[1];

                        return sample.copyWithValue(Math.pow(base, exponent));
                    }
                }
        );
    }

    /**
     * Assemble the (infixed) representation of the expression node for times operator.
     */
    @Override
    public String toCanonicalExpression() {
        return String.format(
            FORMAT,
            baseNode.toCanonicalExpression(),
            PowerFactory.SYMBOL,
            exponentNode.toCanonicalExpression()
        );
    }
}
