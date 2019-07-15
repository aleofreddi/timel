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

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Leofreddi
 */
class TypeTest {
    @Test
    void ctorShouldThrowIllegalStateWhenNonEmptyCollection() {
        assertThrows(IllegalStateException.class, () -> new Type<Integer>(
                Stream.of(1).collect(Collectors.toList())
        ) {
        });
    }

    @Test
    void ctorShouldWorkWhenEmptyCollection() {
        val actual = new Type<Integer>(
                Collections.emptyList()
        ) {
        };

        assertThat(actual.getParameters(), empty());
    }

    @Test
    void specializeShouldThrowUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> new Type<Integer>() {
        }.specialize(1));
    }

    @ParameterizedTest
    @CsvSource({
            "false,false,true",
            "false,true,true",
            "true,false,false",
            "true,true,true",
    })
    void isConcreteShouldWork(boolean unbound, boolean specialized, boolean expected) {
        val actual = new Type<Integer>() {
            @Override
            public boolean isUnboundTemplate() {
                return unbound;
            }

            @Override
            public boolean isSpecializedTemplate() {
                return specialized;
            }
        }.isConcrete();

        assertThat(actual, is(expected));
    }
}
