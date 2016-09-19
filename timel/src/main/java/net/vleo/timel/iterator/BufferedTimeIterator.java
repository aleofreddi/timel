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

import java.util.NoSuchElementException;

/**
 * An abstract base for TimeIterators which ease the development
 * taking care of the #peek and #next methods, which are implemented
 * in terms of #concreteNext method.
 *
 * Note that this implementation will buffer the next entry.
 *
 * @author Andrea Leofreddi
 */
public abstract class BufferedTimeIterator<V> implements TimeIterator<V> {
    private Sample<V> peekedNext;

    /**
     * Fetch the next element.
     *
     * This method must return null when reaching the end of the iterator.
     *
     * @return
     */
    protected abstract Sample<V> concreteNext();

    @Override
    public final Sample<V> next() {
        try {
            if(peekedNext != null)
                return peekedNext;

            Sample<V> r = concreteNext();

            if(r == null)
                throw new NoSuchElementException();

            return r;
        } finally {
            peekedNext = null;
        }
    }

    @Override
    public final Sample<V> peekNext() {
        if(peekedNext == null)
            peekedNext = concreteNext();

        if(peekedNext == null)
            throw new NoSuchElementException();

        return peekedNext;
    }

    public final boolean hasNext() {
        if(peekedNext != null)
            return true;

        peekedNext = concreteNext();

        return peekedNext != null;
    }
}
