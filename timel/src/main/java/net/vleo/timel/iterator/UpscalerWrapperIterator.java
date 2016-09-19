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
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.impl.upscaler.Upscaler;

import java.util.NoSuchElementException;

/**
 * A wrapper to make an Upscaler a UpscalableIterator.
 *
 * @author Andrea Leofreddi
 */
public class UpscalerWrapperIterator<V> implements UpscalableIterator<V> {
    private final Upscaler<V> upscaler;

    private final TimeIterator<V> delegate;

    public UpscalerWrapperIterator(Upscaler<V> upscaler, TimeIterator<V> delegate) {
        this.upscaler = upscaler;
        this.delegate = delegate;
    }

    @Override
    public Sample<V> next() throws NoSuchElementException {
        return delegate.next();
    }

    @Override
    public Sample<V> peekNext() throws NoSuchElementException {
        return delegate.peekNext();
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public Sample<V> peekUpscaleNext(Interval interval) {
        Sample<V> next = peekNext();

        Interval nextInterval = next.getInterval();

        Interval toInterval = interval.overlap(nextInterval);

        return Sample.of(
                toInterval,
                upscaler.interpolate(
                        next.getValue(),
                        nextInterval,
                        toInterval
                )
        );
    }
}
