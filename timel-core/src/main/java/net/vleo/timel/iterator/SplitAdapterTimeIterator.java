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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A TimeIterator that will adapt values from a source intermediate S to a destination intermediate D, using the adapt function
 * possibly being able to split an input sample into an arbitrary number of out samples (including zero).
 * <p>
 * The adapt function is called once per sample and its result is cached to serve multiple next or peekNext calls.
 *
 * @author Andrea Leofreddi
 */
public abstract class SplitAdapterTimeIterator<S, D> implements TimeIterator<D> {
    private final TimeIterator<S> delegate;

    private List<Sample<D>> adaptedBlock;

    private Iterator<Sample<D>> adaptedIter;

    private Sample<D> adaptedNext;

    /**
     * Build a new adapter around the given delegate.
     *
     * @param delegate
     */
    public SplitAdapterTimeIterator(TimeIterator<S> delegate) {
        this.delegate = delegate;

        this.adaptedBlock = Collections.emptyList();

        this.adaptedIter = adaptedBlock.iterator();
    }

    /**
     * Perform the adaptation.
     *
     * @param sample Sample to adapt
     * @return A list of adapted samples. When null means that we've reached the end of stream.
     */
    protected abstract List<Sample<D>> adapt(Sample<S> sample);

    @Override
    public final Sample<D> next() throws NoSuchElementException {
        if(adaptedNext != null) {
            Sample<D> t = adaptedNext;

            adaptedNext = null;

            return t;
        }

        while(!adaptedIter.hasNext())
            if(!fetchNextBlock())
                // We have reached end of stream
                return null;

        return adaptedIter.next();
    }

    @Override
    public final Sample<D> peekNext() throws NoSuchElementException {
        if(adaptedNext != null)
            return adaptedNext;

        while(!adaptedIter.hasNext())
            if(!fetchNextBlock())
                // We have reached end of stream
                throw new ArrayIndexOutOfBoundsException();

        adaptedNext = adaptedIter.next();

        return adaptedNext;
    }

    @Override
    public final boolean hasNext() {
        if(adaptedNext != null)
            return true;

        while(!adaptedIter.hasNext())
            if(!fetchNextBlock())
                // We have reached end of stream
                return false;

        adaptedNext = adaptedIter.next();

        return true;
    }

    private boolean fetchNextBlock() {
        if(!delegate.hasNext())
            return false;

        Sample<S> delegateNext = delegate.next();

        List<Sample<D>> nextBlock = adapt(delegateNext);

        if(nextBlock == null)
            return false;

        adaptedBlock = nextBlock;

        adaptedIter = adaptedBlock.iterator();

        return true;
    }

    @Override
    public String toString() {
        return "SplitAdapterTimeIterator{" +
                "delegate=" + delegate +
                '}';
    }
}
