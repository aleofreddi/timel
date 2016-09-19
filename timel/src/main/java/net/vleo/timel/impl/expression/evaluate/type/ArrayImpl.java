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
package net.vleo.timel.impl.expression.evaluate.type;

import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.type.ArrayType;
import net.vleo.timel.type.Type;
import net.vleo.timel.type.Types;
import net.vleo.timel.type.ValueType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrea Leofreddi
 */
public class ArrayImpl extends AbstractMetaType {
    public static final String TOKEN = "Array";

    private static ArrayType toType(List<AbstractMetaType> innerTypes) throws ParseException {
        List<ValueType<?>> types = new ArrayList<ValueType<?>>(innerTypes.size());

        for(AbstractMetaType innerType : innerTypes) {
            Type type = innerType.getNestedType();

            if(!Types.getValueType().isAssignableFrom(type))
                throw new ParseException(TOKEN + " type can only contain value types, but got " + type);

            types.add((ValueType<?>) type);
        }

        return Types.getArrayType(types);
    }

    public ArrayImpl(List<AbstractMetaType> innerTypes) throws ParseException {
        super(TOKEN, toType(innerTypes), innerTypes);
    }
}
