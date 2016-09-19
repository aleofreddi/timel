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
package net.vleo.timel.impl.expression.evaluate.function.conditional;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.*;
import net.vleo.timel.time.Interval;

/**
 * If operator implementation.
 *
 * @author Andrea Leofreddi
 */
public class IfImpl<V> extends AbstractValueFunction<V> {
    public static final String TOKEN = "If";

    private ValueNode<Double> conditionNode;

    private ValueNode<V> tNode, uNode;

    public IfImpl(ValueNode<Double> conditionNode, ValueNode<V> tNode, ValueNode<V> uNode) {
        super(IfImpl.TOKEN, tNode.getType(), conditionNode, tNode, uNode);

        this.conditionNode = conditionNode;
        this.tNode = tNode;
        this.uNode = uNode;
    }

    @Override
    public UpscalableIterator<V> evaluate(final Interval interval, final ExecutorContext context) {
        return new UpscalerWrapperIterator<V>(
                getType().getUpscaler(),
                new NestedLoopTimeIterator<Double, V>(
                        conditionNode.evaluate(interval, context)
                ) {
                    @Override
                    protected TimeIterator<V> nestedIterator(Sample<Double> value) {
                        Interval interval = value.getInterval();

                        boolean condition = value.getValue() != 0.0;

                        if(condition)
                            return tNode.evaluate(interval, context);

                        return uNode.evaluate(interval, context);
                    }
                }
        );

//                new AdapterTimeIterator<Object[], V>(
//                        new IntersectIterator(
//                                conditionNode.evaluate(interval, context),
//                                tNode.evaluate(interval, context),
//                                uNode.evaluate(interval, context)
//                        )
//                ) {
//                    @Override
//                    protected Sample<V> adapt(Sample<Object[]> sample) {
//                        Object[] values = sample.getValue();
//
//                        Double condition = (Double) values[0];
//
//                        V first = (V) values[1], second = (V)values[2];
//
//                        return sample.copyWithValue(condition != 0.0 ? first : second);
//                    }
//                }
//        );
    }
}

