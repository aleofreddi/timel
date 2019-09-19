package net.vleo.timel.impl.sneaky;

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

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Leofreddi
 */
class ScopedSneakyThrowerTest {
    @Test
    void shouldLimitSneakyScope() {
        try {
            Stream.of(1, 2)
                    .map(new ScopedSneakyThrower<IOException>().unchecked(this::throwingFunction))
                    .count();
        } catch(IOException e) {
        }
    }

    @Test
    void shouldWorkForStreamMap() {
        assertThrows(IOException.class, () ->
                Stream.of(1, 2, new IOException())
                        .map(new ScopedSneakyThrower<IOException>().unchecked(this::throwingFunction))
                        .reduce((t, a) -> a)
        );
    }

    private Object throwingFunction(Object obj) throws IOException {
        if(obj instanceof IOException)
            throw (IOException) obj;
        return obj;
    }
}
