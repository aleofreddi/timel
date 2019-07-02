package net.vleo.timel;

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

import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.IntegerType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static net.vleo.timel.ctor.HasPrivateThrowingCtor.hasPrivateThrowingCtor;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Leofreddi
 */
class TimELTest {
    @Test
    void testSimple() throws ParseException {
        // Compile the expression 1 + 1
        Expression<Integer> expression = TimEL
                .parse("1 + 1") // Get the compiler builder for the expression 1+1
                .compile(new IntegerType()); // Compile the source as an expression

        // Execute the expression
        Interval interval = Interval.of(Instant.now(), Instant.now().plus(10, ChronoUnit.MINUTES));
        TimeIterator<Integer> itor = TimEL
                .evaluate(expression, interval);

        // Since 1+1 is constant we'll have a single sample for the whole interval
        Integer result = itor.next().getValue();

        assertThat(result, instanceOf(Integer.class));
        assertThat(result, is(2));
    }

    @Test
    void parseShouldThrowNullPointerExceptionWhenNullSource() {
        assertThrows(NullPointerException.class, () -> TimEL.parse(null));
    }

    @Test
    void shouldNotBeConstructable() {
        assertThat(TimEL.class, hasPrivateThrowingCtor());
    }
}
