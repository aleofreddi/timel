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

import java.lang.reflect.InvocationTargetException;

/**
 * Commodity functions for {@link Type}s.
 *
 * @author Andrea Leofreddi
 */
public final class Types {
    private Types() {
        throw new AssertionError();
    }

    /**
     * Instance a type from a given class. The resulting instance will be either a non-template instance, or an unbounded template one.
     *
     * @param class_ Type class
     * @param <T>
     * @return Type instance
     */
    public static <T> Type<T> instance(Class<? extends Type<T>> class_) {
        try {
            /// FIXME: we could cache these, but we should need a context to avoid memory leaking
            return class_.getDeclaredConstructor().newInstance();
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ConfigurationException("Unable to instantiate type " + class_);
        }
    }
}
