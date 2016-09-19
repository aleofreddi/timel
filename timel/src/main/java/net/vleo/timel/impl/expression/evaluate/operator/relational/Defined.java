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

import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.impl.upscaler.SameUpscaler;
import net.vleo.timel.iterator.*;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.Types;

import java.util.Collection;

/**
 * @author Andrea Leofreddi
 */
public class Defined extends AbstractValueFunction<Double> {
    public static final String TOKEN = "Defined";

    private final ValueNode<?> valueNode;

    protected Defined(ValueNode<?> valueNode) {
        super(TOKEN, Types.getNumericDoubleType(), valueNode);

        this.valueNode = valueNode;
    }

    @Override
    public UpscalableIterator<Double> evaluate(final Interval interval, ExecutorContext context) {
        final TimeIterator<Double> definedIter = new AdapterTimeIterator<Object, Double>(
                (TimeIterator<Object>) valueNode.evaluate(interval, context)
        ) {
            @Override
            protected Sample<Double> adapt(Sample<Object> sample) {
                return sample.copyWithValue(1.0);
            }
        };

        final UpscalableIterator<Double> undefinedIter = new UpscalerWrapperIterator<Double>(
                SameUpscaler.<Double>get(),
                new SingletonTimeIterator<Double>(
                        Sample.of(interval, 0.0)
                )
        );

        return new UpscalerWrapperIterator<Double>(
                getType().getUpscaler(),
                new CoalesceIterator<Double>(
                        definedIter,
                        undefinedIter
                )
        );
    }
}
