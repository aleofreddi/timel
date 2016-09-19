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

import net.vleo.timel.impl.upscaler.Upscaler;

/**
 * @author Andrea Leofreddi
 */
public class IntegralType<T extends Number> extends ValueType<T> {
    private final Integer degree;

    IntegralType(boolean abstract_) {
        super(abstract_);

        degree = null;
    }

    IntegralType(boolean abstract_, int degree) {
        super(abstract_);

        this.degree = degree;
    }

    /**
     * Retrieve the degree of the integral. May be null when dealing with an abstract integral type.
     *
     * @return The degree
     */
    public final Integer getDegree() {
        return degree;
    }

    @Override
    public Class<T> getValueClass() {
        throw new AssertionError();
    }

    @Override
    public Upscaler<T> getUpscaler() {
        throw new AssertionError();
    }

    /**
     * Check if the given argument is the same or a subtype of this.
     *
     * @param type  Type to check
     * @return True iff type is the same or a subtype of this
     */
    @Override
    public boolean isAssignableFrom(Type type) {
        if(!super.isAssignableFrom(type))
            return false;

        IntegralType<?> t = (IntegralType<?>) type;

        if(degree == null)
            return true;

        if(t.degree == null)
            return false;

        return t.degree == getDegree();
    }

    @Override
    public String toString() {
        return "Integral";
    }
}
