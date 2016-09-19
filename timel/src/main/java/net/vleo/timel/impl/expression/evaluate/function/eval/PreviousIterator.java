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
package net.vleo.timel.impl.expression.evaluate.function.eval;

import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.executor.variable.Variable;
import net.vleo.timel.iterator.*;
import net.vleo.timel.time.Interval;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A TimeIterator that will retrieve the previous sample given a variable and a time node.
 *
 * @author Andrea Leofreddi
 */
class PreviousIterator<V> extends BufferedTimeIterator<Sample<V>> {
    private TimeIterator<Sample<V>> delegate;

    public PreviousIterator(final Variable<V> variable, final ValueNode<Long> timeNode, final Interval interval, final ExecutorContext context) {
        delegate = // (1) For each before node  value
                new SplitAdapterTimeIterator<Long, Sample<V>>(
                        timeNode.evaluate(interval, context)
                ) {
                    @Override
                    protected List<Sample<Sample<V>>> adapt(Sample<Long> sample) {
                        // (2) Evaluate the valueNode at that moment in time and get the last value
                        long time = sample.getValue();

                        TimeIterator<V> iterator = variable.readBackward(
                                Interval.of(
                                        Long.MIN_VALUE,
                                        time
                                ),
                                context
                        );

                        // No value
                        if(!iterator.hasNext())
                            return Collections.emptyList();

                        Sample<V> next = iterator.next();

                        return Arrays.asList(sample.copyWithValue(next));
                    }
                };
    }

    @Override
    protected Sample<Sample<V>> concreteNext() {
        if(!delegate.hasNext())
            return null;

        return delegate.next();
    }
}
