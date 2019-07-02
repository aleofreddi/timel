package net.vleo.timel.impl.downscaler;

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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Leofreddi
 */
class IntegerDownscalerTest extends DownscalerTest {
    @Test
    void reduceShouldDownscale() {
        val downscaler = new IntegerDownscaler();

        downscaler.add(epochSample(1000, 1000));
        downscaler.add(epochSample(1000, 2000));

        assertThat(downscaler.reduce(), is(1500));
    }

    @ParameterizedTest
    @CsvSource({
            Integer.MAX_VALUE + "," + 0 + "," + (Integer.MAX_VALUE / 2),
            Integer.MAX_VALUE + "," + Integer.MAX_VALUE + "," + Integer.MAX_VALUE,
            Integer.MAX_VALUE + "," + Integer.MIN_VALUE + "," + 0
    })
    void addShouldNotOverflow(int t, int u, int expected) {
        val downscaler = new IntegerDownscaler();

        downscaler.add(epochSample(1000, t));
        downscaler.add(epochSample(1000, u));

        assertThat(downscaler.reduce(), is(expected));
    }

    @Test
    void reduceShouldReturnNullWhenEmpty() {
        val downscaler = new IntegerDownscaler();

        val actual = downscaler.reduce();

        assertThat(actual, nullValue());
    }

    @Test
    void reduceShouldReturnNullWhenReset() {
        val downscaler = new IntegerDownscaler();
        downscaler.add(epochSample(1000, 1000));
        downscaler.reduce();

        val actual = downscaler.reduce();

        assertThat(actual, nullValue());
    }
}
