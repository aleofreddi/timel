package net.vleo.timel.ctor;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static net.vleo.timel.ctor.HasPrivateThrowingCtor.hasPrivateThrowingCtor;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.*;

/**
 * @author Andrea Leofreddi
 */
class HasPrivateThrowingCtorTest {
    private static class PrivateThrowingCtor {
        private PrivateThrowingCtor() {
            throw new AssertionError();
        }
    }

    private static class PrivateCtor {
        private PrivateCtor() {
        }
    }

    private static class ProtectedCtor {
        private ProtectedCtor() {
        }
    }

    private static class MultipleCtors {
        public MultipleCtors(int dummy) {
        }

        private MultipleCtors() {
        }
    }

    @Test
    void shouldMatchPrivateThrowingCtorClass() {
        assertThat(PrivateThrowingCtor.class, hasPrivateThrowingCtor());
    }

    @ParameterizedTest
    @ValueSource(classes = {PrivateCtor.class, ProtectedCtor.class, MultipleCtors.class})
    void shouldNotMatchNonPrivateThrowingCtorClass(Class<?> class_) {
        assertThat(class_, not(hasPrivateThrowingCtor()));
    }
}
