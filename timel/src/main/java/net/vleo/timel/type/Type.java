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

/**
 * Superclass for types.
 *
 * @author Andrea Leofreddi
 */
public class Type {
    private final boolean abstract_;

    /**
     * Construct a type.
     *
     * @param abstract_ True iff values of this type cannot be directly instantiated.
     */
    protected Type(boolean abstract_) {
        this.abstract_ = abstract_;
    }

    /**
     * Check if the type is abstract, that is cannot be instantiated.
     *
     * @return True iff type is abstract
     */
    public final boolean isAbstract() {
        return abstract_;
    }

    /**
     * Check if the given argument is the same or a subtype of this.
     *
     * @param type Type to check
     * @return True iff type is the same or a subtype of this
     */
    public boolean isAssignableFrom(Type type) {
        return this.getClass().isAssignableFrom(type.getClass());
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;

        if(!(o instanceof Type))
            return false;

        Type type = (Type) o;

        return isAssignableFrom(type) && type.isAssignableFrom(this);
    }

    @Override
    public int hashCode() {
        return (abstract_ ? 1 : 0);
    }

    @Override
    public String toString() {
        return "Any";
    }
}
