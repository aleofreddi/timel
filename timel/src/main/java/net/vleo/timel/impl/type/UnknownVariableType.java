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
package net.vleo.timel.impl.type;

import net.vleo.timel.type.Type;

/**
 * A type to represent unassigned variables.
 *
 * Usually these are short-lived because they get replaced by the Set function.
 *
 * If at the end of the compilation process, the compiler spots any instance
 * of this type in the compiled tree, an exception is thrown.
 *
 * @author Andrea Leofreddi
 */
public final class UnknownVariableType extends Type {
    private static final UnknownVariableType INSTANCE = new UnknownVariableType();

    /**
     * Retrieve the instance of this type.
     *
     * @return Type instance
     */
    public static UnknownVariableType get() {
        return INSTANCE;
    }

    private UnknownVariableType() {
        super(false);
    }

    @Override
    public String toString() {
        return "Unknown Variable";
    }
}
