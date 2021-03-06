package net.vleo.timel.tuple;

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

import lombok.Value;

/**
 * Tuple3 implementation.
 *
 * @param <T> First type
 * @param <U> Second type
 * @param <V> Third type
 * @author Andrea Leofreddi
 */
@Value
public class Tuple3<T, U, V> {
    T first;
    U second;
    V third;

    /**
     * Extend the current tuple with an additional (last) value.
     *
     * @param value Value to append
     * @param <W>   Type to append
     * @return Extended tuple
     */
    public <W> Tuple4<T, U, V, W> append(W value) {
        return new Tuple4<>(first, second, third, value);
    }
}
