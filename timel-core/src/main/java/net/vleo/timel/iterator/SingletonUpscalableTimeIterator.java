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

import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.Sample;

/**
 * {@link UpscalableIterator} that will return a single sample.
 *
 * @author Andrea Leofreddi
 */
public final class SingletonUpscalableTimeIterator<V> extends AbstractSingletonTimeIterator<V> implements UpscalableIterator<V> {
    private final Upscaler<V> upscaler;

    public SingletonUpscalableTimeIterator(Upscaler<V> upscaler, Sample<V> value) {
        super(value);
        this.upscaler = upscaler;
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
