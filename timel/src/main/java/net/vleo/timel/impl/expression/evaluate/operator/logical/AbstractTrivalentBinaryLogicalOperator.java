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
package net.vleo.timel.impl.expression.evaluate.operator.logical;

import net.vleo.timel.executor.Sample;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.*;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.NumericDoubleType;
import net.vleo.timel.type.NumericType;
import net.vleo.timel.type.Types;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Base class for logical operators.
 *
 * Note that all the implementations of this class must implement a trinary logic.
 *
 * @author Andrea Leofreddi
 */
public abstract class AbstractTrivalentBinaryLogicalOperator extends AbstractValueFunction<Double> {
    private final ValueNode<Double> tNode, uNode;

    protected Double encode(boolean value) {
        return value ? 1.0 : 0.0;
    }

    protected Boolean decode(Double value) {
        if(value == null)
            return null;

        return !value.equals(0.0);
    }

    protected abstract Boolean apply(Boolean t, Boolean u);

    protected AbstractTrivalentBinaryLogicalOperator(String token, ValueNode<Double> tNode, ValueNode<Double> uNode) {
        super(token, Types.getNumericDoubleType(), tNode, uNode);

        this.tNode = tNode;
        this.uNode = uNode;
    }

    @Override
    public UpscalableIterator<Double> evaluate(Interval interval, ExecutorContext context) {
        return new UpscalerWrapperIterator<Double>(
                getType().getUpscaler(),
                new SplitAdapterTimeIterator<Object[], Double>(
                        new UnionIterator(
                                tNode.evaluate(interval, context),
                                uNode.evaluate(interval, context)
                        )
                ) {
                    @Override
                    protected List<Sample<Double>> adapt(Sample<Object[]> sample) {
                        Object[] values = sample.getValue();

                        Boolean t = decode((Double) values[0]), u = decode((Double) values[1]);

                        Boolean r = apply(t, u);

                        if(r == null)
                            // Suppress unknown
                            return Collections.emptyList();

                        return Arrays.asList(sample.copyWithValue(encode(r)));
                    }
                }
        );
    }
}
