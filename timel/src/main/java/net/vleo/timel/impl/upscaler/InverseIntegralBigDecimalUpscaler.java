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
package net.vleo.timel.impl.upscaler;

import net.vleo.timel.time.Interval;

import java.math.BigDecimal;

/**
 * An upscaler to model inverse integrals with a BigDecimal implementation.
 *
 * @author Andrea Leofreddi
 */
public class InverseIntegralBigDecimalUpscaler implements Upscaler<BigDecimal> {
    private static final InverseIntegralBigDecimalUpscaler INSTANCE = new InverseIntegralBigDecimalUpscaler();

    public static InverseIntegralBigDecimalUpscaler get() {
        return INSTANCE;
    }

    @Override
    public BigDecimal interpolate(BigDecimal value, Interval from, Interval to) {
        if(!from.overlaps(to))
            throw new IllegalArgumentException("Interval " + to + " does not overlap sample interval " + from);

        Interval i = from, o = from.overlap(to);

        return value
                .multiply(
                        BigDecimal.valueOf(i.toDurationMillis())
                )
                .divide(
                        BigDecimal.valueOf(o.toDurationMillis()),
                        20,
                        BigDecimal.ROUND_HALF_UP
                );
    }
}
