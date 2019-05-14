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
import net.vleo.timel.impl.downscaler.Downscaler;
import net.vleo.timel.time.Interval;

/**
 * A wrapper to adapt a {@link Downscaler} into a {@link TimeIterator}.
 *
 * @author Andrea Leofreddi
 */
public class DownscalerIterator<T> extends BufferedTimeIterator<T> implements TimeIterator<T> {
    private final Downscaler<T> downscaler;
    private final UpscalableIterator<T> iterator;
    private final Interval interval;

    public DownscalerIterator(Downscaler<T> downscaler, UpscalableIterator<T> iterator, Interval interval) {
        super();

        this.downscaler = downscaler;
        this.iterator = new ChopUpscalableIterator<>(iterator, interval);
        this.interval = interval;
    }

    @Override
    protected Sample<T> concreteNext() {
        if(!iterator.hasNext())
            return null;

        long t = interval.getStart();

        downscaler.reset();
        while(iterator.hasNext()) {
            Sample<T> sample = iterator.next();

            if(t != sample.getInterval().getStart())
                // No contiguous data
                return null;

            t = sample.getInterval().getEnd();
            downscaler.add(sample);
        }

        if(t != interval.getEnd())
            // Not enough data to cover the whole interval
            return null;

        return Sample.of(
                interval,
                downscaler.reduce()
        );
    }

    @Override
    public String toString() {
        return "DownscalerIterator{" +
                "downscaler=" + downscaler +
                ", interval=" + interval +
                ", iterator=" + iterator +
                '}';
    }
}
