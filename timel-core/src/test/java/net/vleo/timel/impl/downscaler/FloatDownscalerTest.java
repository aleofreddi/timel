package net.vleo.timel.impl.downscaler;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;
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
    void reduceShouldThrowNullPointerExceptionWhenEmpty() {
        val downscaler = new FloatDownscaler();

        assertThrows(NullPointerException.class, downscaler::reduce);
    }

    @Test
    void reduceShouldThrowNullPointerExceptionWhenReset() {
        val downscaler = new FloatDownscaler();
        downscaler.add(epochSample(1000, 1000f));
        downscaler.reduce();

        assertThrows(NullPointerException.class, downscaler::reduce);
    }
}