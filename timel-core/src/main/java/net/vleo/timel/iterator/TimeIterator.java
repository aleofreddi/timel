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
 * A time iterator is a forward, read-only iterator which allows streaming of time serie data expressed as {@link Sample}s.
 *
 * This interface provides similar semantics to Java's Iterator, with the added capability of peeking the next value without moving forward the iterator.
 *
 * @author Andrea Leofreddi
 */
public interface TimeIterator<V> {
    /**
     * Move forward the iterator and return the next value.
     *
     * @return
     * @throws NoSuchElementException
     */
    Sample<V> next() throws NoSuchElementException;

    /**
     * Peek the next element without moving forward the iterator.
     *
     * @return The next element
     * @throws NoSuchElementException
     */
    Sample<V> peekNext() throws NoSuchElementException;

    /**
     * Test if there is a next element.
     *
     * @return True iff there is a next element
     */
    boolean hasNext();
}
