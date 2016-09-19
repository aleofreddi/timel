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
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;

import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

/**
 * A passthrough TimeIterator which is printing values to stderr.
 *
 * @author Andrea Leofreddi
 */
public class DebugTimeIterator<V> implements TimeIterator<V> {
    protected final ValueNode<?> valueNode;

    protected final String id;

    protected final Interval interval;

    protected final TimeIterator<V> delegate;

    public DebugTimeIterator(ValueNode<?> valueNode, String id, Interval interval, TimeIterator<V> delegate) {
        this.valueNode = valueNode;
        this.id = id;
        this.interval = interval;
        this.delegate = delegate;
    }

    @Override
    public Sample<V> next() throws NoSuchElementException {
        return DebugContexts.get().apply(
                valueNode,
                id,
                interval,
                "next",
                new Callable<Sample<V>>() {
                    @Override
                    public Sample<V> call() throws Exception {
                        return delegate.next();
                    }
                }
        );
    }

    @Override
    public Sample<V> peekNext() throws NoSuchElementException {
        return DebugContexts.get().apply(
                valueNode,
                id,
                interval,
                "peekNext",
                new Callable<Sample<V>>() {
                    @Override
                    public Sample<V> call() throws Exception {
                        return delegate.peekNext();
                    }
                }
        );
    }

    @Override
    public boolean hasNext() {
        return DebugContexts.get().apply(
                valueNode,
                id,
                interval,
                "hasNext",
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return delegate.hasNext();
                    }
                }
        );
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
