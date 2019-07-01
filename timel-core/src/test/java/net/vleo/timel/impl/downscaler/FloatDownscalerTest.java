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
class FloatDownscalerTest extends DownscalerTest {
    @Test
    void reduceShouldDownscale() {
        val downscaler = new FloatDownscaler();

        downscaler.add(epochSample(1000, 1000f));
        downscaler.add(epochSample(1000, 2000f));

        assertThat(downscaler.reduce(), is(1500f));
    }

    @ParameterizedTest
    @CsvSource({
            Float.MAX_VALUE + "," + 0 + "," + (Float.MAX_VALUE / 2),
            Float.MAX_VALUE + "," + Float.MAX_VALUE + "," + Float.MAX_VALUE,
            Float.MAX_VALUE + "," + -Float.MAX_VALUE + "," + 0
    })
    void addShouldNotOverflow(float t, float u, float expected) {
        val downscaler = new FloatDownscaler();

        downscaler.add(epochSample(1000, t));
        downscaler.add(epochSample(1000, u));

        assertThat(downscaler.reduce(), is(expected));
    }

    @Test
    void reduceShouldReturnNullWhenEmpty() {
        val downscaler = new FloatDownscaler();

        val actual = downscaler.reduce();

        assertThat(actual, nullValue());
    }

    @Test
    void reduceShouldReturnNullWhenReset() {
        val downscaler = new FloatDownscaler();
        downscaler.add(epochSample(1000, 1000f));
        downscaler.reduce();

        val actual = downscaler.reduce();

        assertThat(actual, nullValue());
    }
}
