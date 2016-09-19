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
package net.vleo.timel.iterator;


import net.vleo.timel.executor.Sample;

import java.util.Iterator;

/**
 * An adapter to adapt a standard Java iterator a TimeIterator.
 *
 * @author Andrea Leofreddi
 */
public abstract class JavaIterator2TimeIteratorAdapter<S, D> extends BufferedTimeIterator<D> {
    private final Iterator<S> delegate;

    /**
     * Constructs a JavaIterator2TimeIteratorAdapter from the given delegate.
     *
     * @param delegate The delegate Java iterator
     */
    public JavaIterator2TimeIteratorAdapter(Iterator<S> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected final Sample<D> concreteNext() {
        if(!delegate.hasNext())
            return null;

        return adapt(delegate.next());
    }

    /**
     * Adapt the value to a #Sample.
     *
     * @param value Value to adapt
     * @return Adapted sample
     */
    protected abstract Sample<D> adapt(S value);
}
