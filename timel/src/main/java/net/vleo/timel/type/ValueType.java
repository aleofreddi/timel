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
 * An abstract superclass for expression types, that is nodes
 * which can be evaluated to a value.
 *
 * @author Andrea Leofreddi
 */
public class ValueType<T> extends StatementType {
    ValueType(boolean abstract_) {
        super(abstract_);
    }

    public Class<T> getValueClass() {
        throw new AssertionError("Invoked getValueClass on abstract type");
    }

    public Upscaler<T> getUpscaler() {
        throw new AssertionError("Invoked getUpscaler on abstract type");
    }

    @Override
    public String toString() {
        return "Value";
    }
}
