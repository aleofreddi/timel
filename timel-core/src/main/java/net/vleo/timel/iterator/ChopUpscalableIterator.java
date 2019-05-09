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
 * An implementation of UpscalableIterator that will ensure its output to be chopped over a given interval.
 *
 * @author Andrea Leofreddi
 */
public final class ChopUpscalableIterator<V> implements UpscalableIterator<V> {
    private final Interval interval;

    private final UpscalableIterator<V> delegate;

    private boolean forwarded = false;

    private void fastForward() {
        if(!forwarded) {
            // Fast forward until we cross Interval
            while(delegate.hasNext() && delegate.peekNext().getInterval().getEnd() < interval.getStart())
                delegate.next();

            forwarded = true;
        }
    }

    /**
     * Instance a new ChopUpscalableIterator for the given delegate and interval.
     *
     * @param delegate
     * @param interval
     */
    public ChopUpscalableIterator(UpscalableIterator<V> delegate, Interval interval) {
        this.interval = interval;
        this.delegate = delegate;
    }

    @Override
    public final boolean hasNext() {
        fastForward();

        boolean r = delegate.hasNext() && delegate.peekNext().getInterval().overlaps(interval);

        return r;
    }

    @Override
    public final Sample<V> peekUpscaleNext(Interval interval) {
        fastForward();

        if(!hasNext())
            throw new NoSuchElementException();

        return delegate.peekUpscaleNext(this.interval.overlap(interval));
    }

    @Override
    public final Sample<V> next() throws NoSuchElementException {
        fastForward();

        if(!hasNext())
            throw new NoSuchElementException();

        Sample<V> sample = delegate.peekUpscaleNext(interval);

        delegate.next();

        return sample;
    }

    @Override
    public final Sample<V> peekNext() throws NoSuchElementException {
        fastForward();

        return delegate.peekUpscaleNext(interval);
    }

    @Override
    public final String toString() {
        return "ChopUpscalableIterator{" +
                "delegate=" + delegate +
                ", interval=" + interval +
                '}';
    }
}
