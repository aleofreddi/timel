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
package net.vleo.timel.impl.expression.evaluate.function.array;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.ExecutionException;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.ArrayType;
import net.vleo.timel.type.ValueType;

import java.util.Date;

/**
 * At function implementation for array types.
 *
 * @author Andrea Leofreddi
 */
public class AtImpl<V> extends AbstractValueFunction<V> {
    public static final String TOKEN = "Part";

    private final ValueNode<Object[]> arrayNode;

    private final ValueNode<Double> indexNode;

    private final int indexValue;

    private static int evaluateIndex(ValueNode<Double> indexNode) throws ParseException {
        long now = new Date().getTime();

        double v;

        try {
            v = indexNode.evaluate(
                    Interval.of(now, now),
                    new ExecutorContext()
            ).next().getValue();
        } catch(ExecutionException e) {
            throw new ParseException("Unable to evaluate At index", e);
        }

        if(v != Math.floor(v) || v < 0)
            throw new ParseException("The At function should be used only with integer, positive indexes");

        return (int) v;
    }

    public AtImpl(ArrayType type, ValueNode<Object[]> arrayNode, ValueNode<Double> indexNode) throws ParseException {
        super(TOKEN, (ValueType<V>) type.getInnerTypes().get(evaluateIndex(indexNode)), arrayNode, indexNode);

        this.arrayNode = arrayNode;
        this.indexNode = indexNode;

        if(!indexNode.isConstant())
            throw new ParseException("The At operator should be used with a constant index");

        indexValue = evaluateIndex(indexNode);
    }

    @Override
    public UpscalableIterator<V> evaluate(Interval interval, ExecutorContext context) {
        return
                // (2) Wrap the i-th value with the right upscaler (from the array type)
                new UpscalerWrapperIterator<V>(
                        getType().getUpscaler(),

                        // (1) Extract the i-th value
                        new AdapterTimeIterator<Object[], V>(
                                arrayNode.evaluate(interval, context)
                        ) {
                            @Override
                            protected Sample<V> adapt(Sample<Object[]> sample) {
                                return sample.copyWithValue(
                                        (V) sample.getValue()[indexValue]
                                );
                            }
                        }
                );
    }

    @Override
    public String toCanonicalExpression() {
        return String.format("%s[[%s]]", arrayNode.toCanonicalExpression(), indexNode.toCanonicalExpression());
    }
}
