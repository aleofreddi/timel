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

import lombok.val;
import net.vleo.timel.time.Sample;

/**
 * Integer downscaler.
 *
 * @author Andrea Leofreddi
 */
public class IntegerDownscaler implements Downscaler<Integer> {
    private Long value;
    private long length;

    public IntegerDownscaler() {
        reset();
    }

    @Override
    public void reset() {
        value = null;
        length = 0;
    }

    @Override
    public void add(Sample<Integer> sample) {
        long duration = sample.getInterval().toDurationMillis();
        if(value != null) {
            value += sample.getValue() * duration;
            length += duration;
        } else {
            value = sample.getValue() * duration;
            length = duration;
        }
    }

    @Override
    public Integer reduce() {
        val result = value / length;
        reset();
        return (int) result;
    }
}
