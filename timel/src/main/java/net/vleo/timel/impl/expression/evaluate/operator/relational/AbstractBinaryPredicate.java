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
package net.vleo.timel.impl.expression.evaluate.operator.relational;


import net.vleo.timel.executor.Sample;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.iterator.IntersectIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.NumericDoubleType;
import net.vleo.timel.type.NumericType;
import net.vleo.timel.type.Types;
import net.vleo.timel.type.ValueType;
import net.vleo.timel.iterator.UpscalableIterator;

/**
 * Common superclass for binary predicates.
 *
 * @author Andrea Leofreddi
 */
public abstract class AbstractBinaryPredicate<T, U> extends AbstractValueFunction<Double> {
    private ValueNode<T> tNode;

    private ValueNode<U> uNode;

    private BinaryBooleanFunction<T, U> predicate;

    public interface BinaryBooleanFunction<T, U> {
        boolean apply(T t, U u);
    }

    protected AbstractBinaryPredicate(String token, ValueNode<T> tNode, ValueNode<U> uNode, BinaryBooleanFunction<T, U> predicate) {
        super(token, Types.getNumericDoubleType(), tNode, uNode);

        this.tNode = tNode;
        this.uNode = uNode;

        this.predicate = predicate;
    }

    @Override
    public UpscalableIterator<Double> evaluate(Interval interval, ExecutorContext context) {
        return new UpscalerWrapperIterator<Double>(
                getType().getUpscaler(),
                new AdapterTimeIterator<Object[], Double>(
                        new IntersectIterator(
                                tNode.evaluate(interval, context),
                                uNode.evaluate(interval, context)
                        )
                ) {
                    @Override
                    protected Sample<Double> adapt(Sample<Object[]> sample) {
                        Object[] values = sample.getValue();

                        T t = (T)values[0];
                        U u = (U)values[1];

                        return sample.copyWithValue(predicate.apply(t, u) ? 1.0 : 0);
                    }
                }
        );
    }
}

