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

import net.vleo.timel.time.Interval;
import net.vleo.timel.impl.upscaler.Upscaler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The array type. An array type is value type containing an ordered tuple of {#ValueType}s.
 *
 * @author Andrea Leofreddi
 */
public class ArrayType extends ValueType<Object[]> {
    private final List<ValueType<?>> innerTypes;

    private final ArrayUpscaler upscaler;

    private class ArrayUpscaler implements Upscaler<Object[]> {
        private List<Upscaler<Object>> upscalers;

        public ArrayUpscaler() {
            upscalers = new ArrayList<Upscaler<Object>>(innerTypes.size());

            for(ValueType<?> innerType : innerTypes) {
                @SuppressWarnings("unchecked")
                Upscaler<Object> upscaler = (Upscaler<Object>) innerType.getUpscaler();

                upscalers.add(upscaler);
            }
        }

        @Override
        public Object[] interpolate(Object[] value, Interval from, Interval to) {
            Object[] result = new Object[upscalers.size()];

            for(int i = 0; i < upscalers.size(); i++)
                result[i] = upscalers.get(i).interpolate(value[i], from, to);

            return result;

        }
    }

    ArrayType(Collection<ValueType<?>> innerTypes) {
        super(false);

        this.innerTypes = new ArrayList<ValueType<?>>(innerTypes);

        upscaler = new ArrayUpscaler();
    }

    @Override
    public boolean isAssignableFrom(Type type) {
        if(!(type instanceof ArrayType))
            return false;

        ArrayType t = (ArrayType) type;

        if(getInnerTypes().size() != t.getInnerTypes().size())
            return false;

        for(int i = 0; i < getInnerTypes().size(); i++)
            if(!getInnerTypes().get(i).isAssignableFrom(t.getInnerTypes().get(i)))
                return false;

        return true;
    }

    public List<ValueType<?>> getInnerTypes() {
        return innerTypes;
    }

    @Override
    public Class<Object[]> getValueClass() {
        return Object[].class;
    }

    @Override
    public Upscaler<Object[]> getUpscaler() {
        return upscaler;
    }
}
