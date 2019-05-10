package net.vleo.timel.type;

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

import net.vleo.timel.ConfigurationException;
import net.vleo.timel.impl.downscaler.Downscaler;
import net.vleo.timel.impl.downscaler.SameDownscaler;
import net.vleo.timel.impl.upscaler.SameUpscaler;
import net.vleo.timel.impl.upscaler.Upscaler;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Superclass to define a TimEL type.
 * <p>
 * Types can be either regular, so that each two instances are equal, or a template, that is a parametrized type.
 *
 * @param <T> Expected payload Java type
 * @author Andrea Leofreddi
 */
public abstract class Type<T> {
    protected Type(Collection a) {
        if(a.iterator().hasNext())
            throw new IllegalStateException();
    }

    protected Type() {
    }

    /**
     * @return True iff this type is a non-specialized template
     */
    public boolean isUnboundTemplate() {
        return false;
    }

    /**
     * @return True iff this type is a specialized template
     */
    public boolean isSpecializedTemplate() {
        return false;
    }

    /**
     * @return True if template is a concrete type, that is either not a template  or it is a specialized template
     */
    public boolean isConcrete() {
        return !isUnboundTemplate() || isSpecializedTemplate();
    }

    /**
     * @return The unbound template type for a specialized template, or itself for a non-template type.
     */
    public Type<T> template() {
        return this;
    }

    public Type<T> specialize(Object... parameters) {
        throw new UnsupportedOperationException();
    }

    public List<Object> getParameters() {
        return Collections.emptyList();
    }

    /**
     * Retrieve an {@link Upscaler} for this type.
     *
     * @return The type upscaler
     */
    public Upscaler<?> getUpscaler() {
        return new SameUpscaler<>();
    }

    /**
     * Retrieve the {@link Downscaler} for this type.
     *
     * @return The type downscaler
     */
    public Downscaler<?> getDownscaler() {
        return new SameDownscaler<>();
    }

    /**
     * Returns the type name as shown to the user.
     *
     * @return Type name
     */
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == getClass();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
