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
