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
package net.vleo.timel.impl.iterator;

import net.vleo.timel.executor.Sample;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;

import java.util.concurrent.Callable;

/**
 * A passthrough UpscalableIterator which is printing values to stderr.
 *
 * @author Andrea Leofreddi
 */
public class DebugUpscalableTimeIterator<V> extends DebugTimeIterator<V> implements UpscalableIterator<V> {
    public DebugUpscalableTimeIterator(ValueNode<?> valueNode, String id, Interval interval, UpscalableIterator<V> delegate) {
        super(valueNode, id, interval, delegate);
    }

    @Override
    public Sample<V> peekUpscaleNext(final Interval interval) {
        return DebugContexts.get().apply(
                valueNode,
                id,
                this.interval,
                "peekInterpolatedNext",
                new Callable<Sample<V>>() {
                    @Override
                    public Sample<V> call() throws Exception {
                        return ((UpscalableIterator<V>)delegate).peekUpscaleNext(interval);
                    }
                }
        );
    }
}
