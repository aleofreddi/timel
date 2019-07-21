package net.vleo.timel.iterator;

/*-
 * #%L
 * TimEL core
 * %%
 * Copyright (C) 2015 - 2019 Andrea Leofreddi
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import net.vleo.timel.time.Sample;

import java.util.NoSuchElementException;

/**
 * An abstract base for {@link TimeIterator}s that do buffer the next entry from a delegate iterator.
 * <p>
 * This class takes care of the #peek and #next methods, which are implemented in terms of #concreteNext method.
 *
 * @param <V> Value Java type
 * @author Andrea Leofreddi
 */
public abstract class BufferedTimeIterator<V> implements TimeIterator<V> {
    private Sample<V> peekedNext;

    /**
     * Fetch the next element.
     * <p>
     * This method must return null when reaching the end of the stream.
     *
     * @return Next sample, or null if at the end of the stream
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
