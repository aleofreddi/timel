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
import net.vleo.timel.iterator.ChopUpscalableIterator;
import net.vleo.timel.iterator.BufferedTimeIterator;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;

/**
 * A downscaler that will compute the prorated average.
 *
 * @author Andrea Leofreddi
 */
public class AverageDownscaler extends BufferedTimeIterator<Double> implements TimeIterator<Double> {
    private final UpscalableIterator<Double> iterator;

    private final Interval interval;

    public AverageDownscaler(UpscalableIterator<Double> iterator, Interval interval) {
        super();

        this.iterator = new ChopUpscalableIterator<Double>(iterator, interval);
        this.interval = interval;
    }

    @Override
    protected Sample<Double> concreteNext() {
        if(!iterator.hasNext())
            return null;

        double value = 0;

        long length = 0;

        long t = interval.getStart();

        while(iterator.hasNext()) {
            Sample<Double> sample = iterator.next();

            if(t != sample.getInterval().getStart())
                // No contiguous data
                return null;

            t = sample.getInterval().getEnd();

            long duration = sample.getInterval().toDurationMillis();

            value += sample.getValue() * duration;

            length += sample.getInterval().toDurationMillis();
        }

        if(t != interval.getEnd())
            // Not enough data to cover the whole interval
            return null;

        return Sample.of(
                interval,
                value / length
        );
    }

    @Override
    public String toString() {
        return "AverageDownscaler{" +
                "interval=" + interval +
                ", iterator=" + iterator +
                '}';
    }
}
