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
import net.vleo.timel.time.Interval;

/**
 * A TimeIterator which support upscaling operation, that is is able
 * to peek the next value upscaling it to the given interval.
 *
 * @author Andrea Leofreddi
 */
public interface UpscalableIterator<V> extends TimeIterator<V> {
    /**
     * Retrieve a sample which contains the upscaled value, relative
     * to the given interval, for the next value.
     *
     * As this operation peeks the next value, the iterator is not
     * moved forward.
     *
     * @param interval  The interval to upscaler the value with
     * @return The upscaled next
     */
    Sample<V> peekUpscaleNext(Interval interval);
}
