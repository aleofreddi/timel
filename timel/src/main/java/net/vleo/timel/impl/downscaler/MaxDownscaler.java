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
package net.vleo.timel.impl.downscaler;

import net.vleo.timel.executor.Sample;
import net.vleo.timel.iterator.BufferedTimeIterator;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;

/**
 * Max downscaler.
 *
 * @author Andrea Leofreddi
 */
public class MaxDownscaler<V extends Comparable<V>> extends BufferedTimeIterator<V> implements TimeIterator<V> {
    private final UpscalableIterator<V> iterator;

    private final Interval interval;

    private V max;

    public MaxDownscaler(UpscalableIterator<V> iterator, Interval interval) {
        super();

        this.iterator = iterator;
        this.interval = interval;
    }

    @Override
    protected Sample<V> concreteNext() {
        if(!iterator.hasNext())
            return null;

        long t = interval.getStart();

        while(iterator.hasNext()) {
            Sample<V> sample = iterator.next();

            if(t != sample.getInterval().getStart())
                // No contiguous data
                return null;

            t = sample.getInterval().getEnd();

            if(max == null || sample.getValue().compareTo(max) > 0)
                max = sample.getValue();
        }

        if(t != interval.getEnd())
            // Not enough data to cover the whole interval
            return null;

        return Sample.of(
                interval,
                max
        );
    }
}
