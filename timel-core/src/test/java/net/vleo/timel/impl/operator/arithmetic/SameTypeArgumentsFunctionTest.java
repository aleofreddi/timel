package net.vleo.timel.impl.operator.arithmetic;

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
import net.vleo.timel.type.IntegerType;
import net.vleo.timel.type.IntegralIntegerType;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;

/**
 * @author Andrea Leofreddi
 */
class SameTypeArgumentsFunctionTest {
    private SameTypeArgumentsFunction<?> sameTypeFunction = (interval, context, upscaler, downscaler, arguments) -> null;

    @Test
    void shouldResolveTypeFromArgumentsWhenSame() {
        val actual = sameTypeFunction.resolveReturnType(null, Collections.emptyMap(),
                new IntegralIntegerType(1),
                new IntegralIntegerType(1),
                new IntegralIntegerType(1)
        );

        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get(), is(new IntegralIntegerType(1)));
    }

    @Test
    void shouldNotResolveTypeFromArgumentsWhenDifferentTypes() {
        val actual = sameTypeFunction.resolveReturnType(null, Collections.emptyMap(),
                new IntegralIntegerType(1),
                new IntegerType()
        );

        assertThat(actual.isPresent(), is(false));
    }

    @Test
    void shouldNotResolveTypeFromArgumentsWhenDifferentSpecializations() {
        val actual = sameTypeFunction.resolveReturnType(null, Collections.emptyMap(),
                new IntegralIntegerType(1),
                new IntegralIntegerType(1),
                new IntegralIntegerType(2)
        );

        assertThat(actual.isPresent(), is(false));
    }
}
