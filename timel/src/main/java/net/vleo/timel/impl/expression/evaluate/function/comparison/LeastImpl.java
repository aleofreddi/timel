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
package net.vleo.timel.impl.expression.evaluate.function.comparison;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.iterator.IntersectIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.ValueType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Least function.
 *
 * @author Andrea Leoreddi
 */
public class LeastImpl<T extends Comparable<T>> extends AbstractValueFunction<T> {
    public static final String TOKEN = "Least";

    private List<ValueNode<T>> expressions;

    public LeastImpl(ValueType<T> type, List<ValueNode<T>> arguments) {
        super(LeastImpl.TOKEN, type, arguments);

        this.expressions = arguments;
    }

    @Override
    public UpscalableIterator<T> evaluate(Interval interval, ExecutorContext context) {
        List<UpscalableIterator<?>> iterators = new ArrayList<UpscalableIterator<?>>(expressions.size());

        for(ValueNode<T> expression : expressions)
            iterators.add(expression.evaluate(interval, context));

        return new UpscalerWrapperIterator<T>(
                getType().getUpscaler(),
                new AdapterTimeIterator<Object[], T>(
                        new IntersectIterator(iterators)
                ) {
                    @Override
                    protected Sample<T> adapt(Sample<Object[]> sample) {
                        List<T> args = new ArrayList<T>(sample.getValue().length);

                        for(Object v : sample.getValue())
                            args.add((T)v);

                        return sample.copyWithValue(Collections.min(args));
                    }
                }
        );
    }
}

