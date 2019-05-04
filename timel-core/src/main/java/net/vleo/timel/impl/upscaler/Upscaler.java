package net.vleo.timel.impl.upscaler;

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

import net.vleo.timel.time.Interval;

/**
 * An upscaler is a policy able to upscale a given value from an interval to a target interval.
 *
 * @author Andrea Leofreddi
 */
public interface Upscaler<V> {
    /**
     * Interpolate value from the given interval to the target interval.
     * <p>
     * Note that source and target must overlap, otherwise {@link IllegalArgumentException} is thrown.
     *
     * @param value The value to interpolate
     * @param from  The source interval
     * @param to    The target interval
     * @return The interpolated value
     * @throws IllegalArgumentException Iff from does not overlap to
     */
    V interpolate(V value, Interval from, Interval to);
}
