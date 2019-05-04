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

import lombok.RequiredArgsConstructor;
import net.vleo.timel.time.Sample;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.time.Interval;

import java.util.NoSuchElementException;

/**
 * A wrapper to adapt an {@link Upscaler} into a {@link UpscalableIterator}.
 *
 * @author Andrea Leofreddi
 */
@RequiredArgsConstructor
public class UpscalerIterator<V> implements UpscalableIterator<V> {
    private final Upscaler<V> upscaler;
    private final TimeIterator<V> delegate;

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
