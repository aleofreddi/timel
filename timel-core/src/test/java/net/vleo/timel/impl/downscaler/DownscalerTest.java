package net.vleo.timel.impl.downscaler;

import net.vleo.timel.time.Interval;
import net.vleo.timel.time.Sample;

/**
 * @author Andrea Leofreddi
 */
class DownscalerTest {
    protected <V> Sample<V> epochSample(long duration, V value) {
        return Sample.of(Interval.of(0, duration), value);
    }
}
