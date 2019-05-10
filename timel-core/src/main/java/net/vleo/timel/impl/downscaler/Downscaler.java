package net.vleo.timel.impl.downscaler;

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

/**
 * An downscaler is a policy able to downscale a series of values (given their weight) to a single value.
 *
 * @author Andrea Leofreddi
 */
public interface Downscaler<V> {
    /**
     * Reset the state of the downscaler.
     */
    void reset();

    /**
     * Add a value for downscaling.
     *
     * @param sample Add a sample to downscale.
     */
    void add(Sample<V> sample);

    /**
     * Reduce the added items, and reset the downscaler state.
     *
     * @return The reduced (downscaled) value.
     */
    V reduce();
}
