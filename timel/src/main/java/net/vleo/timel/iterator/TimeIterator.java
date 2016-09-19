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
 * A time iterator is a forward, read-only iterator which allows streaming of data changing
 * over time, expressed as {@link Sample}s.
 *
 * This interface provides similar semantics to Java's Iterator, with the added capability
 * of peeking the next value without moving forward the iterator.
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
