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
 * A TimeIterator that will adapt values from a source intermediate S to a destination intermediate D, using the adapt function.
 *
 * The adapt function is called once per sample and its result is cached to serve multiple next or peekNext calls.
 *
 * @deprecated Use {@link ValueAdapterTimeIterator}
 *
 * @author Andrea Leofreddi
 */
@Deprecated
public abstract class AdapterTimeIterator<S, D> implements TimeIterator<D> {
    private final TimeIterator<S> delegate;

    private Sample<D> adaptedNext;

    /**
     * Build a new adapter around the given delegate.
     *
     * @param delegate
     */
    public AdapterTimeIterator(TimeIterator<S> delegate) {
        this.delegate = delegate;

        this.adaptedNext = null;
    }

    /**
     * Perform the adaptation.
     *
     * @param sample Sample to adapt
     * @return Adapter sample
     */
    protected abstract Sample<D> adapt(Sample<S> sample);

    @Override
    public final Sample<D> next() throws NoSuchElementException {
        try {
            if(adaptedNext != null) {
                // We forward delegate anyway but ignore the return value, as we have an already cached adapted version
                delegate.next();

                return adaptedNext;
            }

            return adapt(delegate.next());
        } finally {
            adaptedNext = null;
        }
    }

    @Override
    public final Sample<D> peekNext() throws NoSuchElementException {
        if(adaptedNext != null)
            return adaptedNext;

        adaptedNext = adapt(delegate.peekNext());

        return adaptedNext;
    }

    @Override
    public final boolean hasNext() {
        if(adaptedNext != null)
            return true;

        return delegate.hasNext();
    }

    @Override
    public String toString() {
        return "AdapterTimeIterator{" +
                "delegate=" + delegate +
                '}';
    }
}
