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
import net.vleo.timel.time.Interval;

import java.util.NoSuchElementException;

/**
 * Upscalable iterator version of NestedLoopTimeIterator.
 *
 * @author Andrea Leofreddi
 */
public abstract class NestedLoopUpscalableIterator<S, D> extends NestedLoopTimeIterator<S, D> implements UpscalableIterator<D> {
    abstract protected UpscalableIterator<D> nestedIterator(Sample<S> value);

    /**
     * Protected constructor.
     *
     * @param iterator
     */
    protected NestedLoopUpscalableIterator(TimeIterator<S> iterator) {
        super(iterator);
    }

    @Override
    public final Sample<D> peekUpscaleNext(Interval interval) {
        while(nestedIterator == null || !nestedIterator.hasNext()) {
            if(!iterator.hasNext())
                throw new NoSuchElementException("Nested iterator beyond limit");

            nestedIterator = nestedIterator(iterator.next());
        }

        return ((UpscalableIterator<D>)nestedIterator).peekUpscaleNext(interval);
    }

    @Override
    public String toString() {
        return "NestedLoopUpscalableIterator";
    }
}
