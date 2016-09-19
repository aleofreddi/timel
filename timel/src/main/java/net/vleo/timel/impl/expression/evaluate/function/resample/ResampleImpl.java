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
package net.vleo.timel.impl.expression.evaluate.function.resample;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.impl.expression.compile.DebugFactory;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.ValueType;

/**
 * Resample a value accordingly to the context interval.
 *
 * @author Andrea Leofreddi
 */
public class ResampleImpl<V> extends AbstractValueFunction<V> {
    private final ValueNode<V> valueNode;

    private final ValueType<V> innerType, outerType;

    public ResampleImpl(String token, ValueNode<V> valueNode, ValueType<V> innerType, ValueType<V> outerType) {
        super(token, outerType, valueNode);

        this.valueNode = valueNode;
        this.innerType = innerType;
        this.outerType = outerType;
    }

    @Override
    public UpscalableIterator<V> evaluate(Interval interval, ExecutorContext context) {
        DebugFactory.DebugBuilder debug = DebugFactory.of(this, context);

        return new UpscalerWrapperIterator<V>(
                outerType.getUpscaler(),
                ResampleFactory.getDownscaler(
                        innerType,
                        valueNode.evaluate(
                                interval,
                                context
                        ),
                        interval
                )
        );
    }
}
