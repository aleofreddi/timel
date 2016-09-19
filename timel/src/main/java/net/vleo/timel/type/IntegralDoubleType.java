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
package net.vleo.timel.type;

import net.vleo.timel.impl.upscaler.IntegralDoubleUpscaler;
import net.vleo.timel.impl.upscaler.InverseIntegralDoubleUpscaler;
import net.vleo.timel.impl.upscaler.Upscaler;

/**
 * @author Andrea Leofreddi
 */
@SuppressWarnings("unused")
public class IntegralDoubleType extends IntegralType<Double> {
    IntegralDoubleType() {
        super(true);
    }

    IntegralDoubleType(int degree) {
        super(false, degree);
    }

    @Override
    public Class<Double> getValueClass() {
        return Double.class;
    }

    @Override
    public Upscaler<Double> getUpscaler() {
        if(getDegree() > 0)
            return new IntegralDoubleUpscaler();

        return new InverseIntegralDoubleUpscaler();
    }

    @Override
    public String toString() {
        return "Integral Double";
    }
}
