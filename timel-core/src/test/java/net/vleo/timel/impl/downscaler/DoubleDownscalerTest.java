package net.vleo.timel.impl.downscaler;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Leofreddi
 */
class DoubleDownscalerTest extends DownscalerTest {
    @Test
    void reduceShouldDownscale() {
        val downscaler = new DoubleDownscaler();

        downscaler.add(epochSample(1000, 1000d));
        downscaler.add(epochSample(1000, 2000d));

        assertThat(downscaler.reduce(), is(1500d));
    }

    @Test
    void reduceShouldThrowNullPointerExceptionWhenEmpty() {
        val downscaler = new DoubleDownscaler();

        assertThrows(NullPointerException.class, downscaler::reduce);
    }

    @Test
    void reduceShouldThrowNullPointerExceptionWhenReset() {
        val downscaler = new DoubleDownscaler();
        downscaler.add(epochSample(1000, 1000d));
        downscaler.reduce();

        assertThrows(NullPointerException.class, downscaler::reduce);
    }
}