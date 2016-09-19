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
 * A nested loop iterator which allows, for each iteration of a given TimeIterator,
 * to loop over a nested iterator.
 *
 * @author Andrea Leofreddi
 */
public abstract class NestedLoopTimeIterator<S, D> implements TimeIterator<D> {
    protected final TimeIterator<S> iterator;

    protected TimeIterator<D> nestedIterator;

    /**
     * Protected constructor.
     *
     * @param iterator
     */
    protected NestedLoopTimeIterator(TimeIterator<S> iterator) {
        this.iterator = iterator;
    }

    abstract protected TimeIterator<D> nestedIterator(Sample<S> value);

    @Override
    public final Sample<D> next() throws NoSuchElementException {
        while(nestedIterator == null || !nestedIterator.hasNext()) {
            if(!iterator.hasNext())
                throw new NoSuchElementException("Nested iterator beyond limit");

            nestedIterator = nestedIterator(iterator.next());
        }

        return nestedIterator.next();
    }

    @Override
    public final Sample<D> peekNext() throws NoSuchElementException {
        while(nestedIterator == null || !nestedIterator.hasNext()) {
            if(!iterator.hasNext())
                throw new NoSuchElementException("Nested iterator beyond limit");

            nestedIterator = nestedIterator(iterator.next());
        }

        return nestedIterator.peekNext();
    }

    @Override
    public final boolean hasNext() {
        while(nestedIterator == null || !nestedIterator.hasNext()) {
            if(!iterator.hasNext())
                return false;

            nestedIterator = nestedIterator(iterator.next());
        }

        //assert nestedIterator.hasNext();

        return true;
    }

    @Override
    public String toString() {
        return "NestedLoopTimeIterator";
    }
}
