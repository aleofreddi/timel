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
    void reduceShouldThrowNullPointerExceptionWhenEmpty() {
        val downscaler = new IntegerDownscaler();

        assertThrows(NullPointerException.class, downscaler::reduce);
    }

    @Test
    void reduceShouldThrowNullPointerExceptionWhenReset() {
        val downscaler = new IntegerDownscaler();
        downscaler.add(epochSample(1000, 1000));
        downscaler.reduce();

        assertThrows(NullPointerException.class, downscaler::reduce);
    }
}