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
 * A {@link Downscaler} that will downscale values equal to each other.
 *
 * @author Andrea Leofreddi
 */
public class SameDownscaler<T> implements Downscaler<T> {
    private boolean seen = false;
    private T value;

    @Override
    public void reset() {
        seen = false;
        value = null;
    }

    @Override
    public void add(Sample<T> sample) {
        if(!seen) {
            value = sample.getValue();
            seen = true;
        } else if(value == null && sample.getValue() != null || value != null && !value.equals(sample.getValue())) {
            value = null;
        }
    }

    @Override
    public T reduce() {
        T t = value;
        reset();
        return t;
    }
}
