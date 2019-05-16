package net.vleo.timel.iterator;

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
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.Sample;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Andrea Leofreddi
 */
@ExtendWith(MockitoExtension.class)
class UpscalerIteratorTest {
    private static final long START_EPOCH = 1388530800000L, STOP_EPOCH = 1420066800000L;
    private static final Interval FULL_INTERVAL = Interval.of(Instant.ofEpochMilli(START_EPOCH), Instant.ofEpochMilli(STOP_EPOCH)),
            HALF_INTERVAL = Interval.of(Instant.ofEpochMilli(START_EPOCH), Instant.ofEpochMilli((STOP_EPOCH + START_EPOCH) / 2));
    private static final Sample<Object> SAMPLE = Sample.of(FULL_INTERVAL, 2);

    @Mock
    private Upscaler<Object> upscaler;
    @Mock
    private TimeIterator<Object> delegate;

    @Test
    void shouldPassThroughHasNext() {
        when(delegate.hasNext())
                .thenReturn(true);

        UpscalableIterator<Object> upscalableIterator = new UpscalerIterator<>(upscaler, delegate);

        assertThat(upscalableIterator.hasNext(), is(true));
        verify(delegate).hasNext();
    }

    @Test
    void shouldPassThroughNext() {
        when(delegate.next())
                .thenReturn(SAMPLE);

        UpscalableIterator<Object> upscalableIterator = new UpscalerIterator<>(upscaler, delegate);

        assertThat(upscalableIterator.next(), sameInstance(SAMPLE));
        verify(delegate).next();
    }

    @Test
    void shouldPassThroughPeekNext() {
        when(delegate.peekNext())
                .thenReturn(SAMPLE);

        UpscalableIterator<Object> upscalableIterator = new UpscalerIterator<>(upscaler, delegate);

        assertThat(upscalableIterator.peekNext(), sameInstance(SAMPLE));
        verify(delegate).peekNext();
    }

    @Test
    void shouldPeekUpscaleNextUsingUpscalerWhenDifferentInterval() {
        when(delegate.peekNext())
                .thenReturn(SAMPLE);
        when(upscaler.interpolate(eq(2), eq(FULL_INTERVAL), eq(HALF_INTERVAL)))
                .thenReturn(1);

        UpscalableIterator<Object> upscalableIterator = new UpscalerIterator<>(upscaler, delegate);
        val actual = upscalableIterator.peekUpscaleNext(HALF_INTERVAL);

        assertThat(actual.getInterval(), is(HALF_INTERVAL));
        assertThat(actual.getValue(), is(1));
        verify(delegate).peekNext();
        verify(upscaler).interpolate(eq(2), eq(FULL_INTERVAL), eq(HALF_INTERVAL));
    }

    @Test
    void shouldPeekUpscaleNextWithoutUpscalerWhenSameInterval() {
        when(delegate.peekNext())
                .thenReturn(SAMPLE);

        UpscalableIterator<Object> upscalableIterator = new UpscalerIterator<>(upscaler, delegate);
        val actual = upscalableIterator.peekUpscaleNext(FULL_INTERVAL);

        assertThat(actual, sameInstance(SAMPLE));
        verify(delegate).peekNext();
        verifyZeroInteractions(upscaler);
    }
}
