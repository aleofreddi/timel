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
package net.vleo.timel.impl.expression.evaluate.function.cast;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.IntegralDoubleType;
import net.vleo.timel.type.IntegralType;
import net.vleo.timel.type.Types;

/**
 * Convert a number to an integral
 *
 * @author Andrea Leofreddi
 */
public class AsIntegralImpl extends AbstractValueFunction<Double> {
    public static final String TOKEN = "AsIntegral";

    private ValueNode<Double> valueNode;

    public AsIntegralImpl(ValueNode<Double> valueNode) {
        super(TOKEN, Types.getIntegralDoubleType(1), valueNode);

        this.valueNode = valueNode;
    }

    @Override
    public UpscalableIterator<Double> evaluate(Interval interval, ExecutorContext context) {
        return new UpscalerWrapperIterator<Double>(
                getType().getUpscaler(),
                valueNode.evaluate(interval, context)
        );
    }

    @Override
    public boolean isConstant() {
        return valueNode.isConstant();
    }
}
