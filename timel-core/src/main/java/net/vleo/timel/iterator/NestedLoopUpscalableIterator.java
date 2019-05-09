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
