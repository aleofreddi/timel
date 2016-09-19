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
package net.vleo.timel.impl.expression.evaluate.function.round;

import net.vleo.timel.executor.Sample;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.NumericDoubleType;
import net.vleo.timel.type.NumericType;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.type.Types;

/**
 * Double round implementation.
 *
 * @author Andrea Leofreddi
 */
public class RoundImpl extends AbstractValueFunction<Double> {
    public static final String TOKEN = "Round";

    private final ValueNode<Double> valueNode;

    public RoundImpl(ValueNode<Double> valueNode) {
        super(TOKEN, Types.getNumericDoubleType(), valueNode);

        this.valueNode = valueNode;
    }

    @Override
    public UpscalableIterator<Double> evaluate(Interval interval, ExecutorContext context) {
        return new UpscalerWrapperIterator<Double>(
                getType().getUpscaler(),
                new AdapterTimeIterator<Double, Double>(valueNode.evaluate(interval, context)) {
                    @Override
                    protected Sample<Double> adapt(Sample<Double> sample) {
                        return sample.copyWithValue((double) Math.round(sample.getValue()));
                    }
                }
        );
    }
}
