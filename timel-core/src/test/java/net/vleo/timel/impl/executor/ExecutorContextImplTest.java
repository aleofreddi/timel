package net.vleo.timel.impl.executor;

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
import net.vleo.timel.impl.iterator.TracingPolicy;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.when;

/**
 * @author Andrea Leofreddi
 */
@ExtendWith(MockitoExtension.class)
class ExecutorContextImplTest {
    private final static Object REFERENCE = new Object();
    private final static String ID = "id42";
    private final static Interval INTERVAL = Interval.of(0, 1000);
    @Mock
    private TracingPolicy tracingPolicy;
    @Mock
    private TimeIterator<Object> timeIterator;
    @Mock
    private UpscalableIterator<Object> upscalableIterator;

    @Test
    void debugShouldPassThroughWhenTimeIteratorAndTracingIsDisabled() {
        ExecutorContextImpl context = new ExecutorContextImpl(null);

        val actual = context.debug(null, null, null, timeIterator);

        assertThat(actual, is(timeIterator));
    }

    @Test
    void debugShouldPassThroughWhenUpscalableIteratorAndTracingIsDisabled() {
        ExecutorContextImpl context = new ExecutorContextImpl(null);

        val actual = context.debug(null, null, null, upscalableIterator);

        assertThat(actual, is(upscalableIterator));
    }

    @Test
    void debugShouldWrapWhenTimeIteratorAndTracingIsEnabled() {
        ExecutorContextImpl context = new ExecutorContextImpl(tracingPolicy);
        when(tracingPolicy.apply(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(null);
        when(tracingPolicy.apply(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq("hasNext"), Mockito.any())).thenReturn(true);

        val actual = context.debug(REFERENCE, ID, INTERVAL, timeIterator);

        assertThat(actual, not(is(timeIterator)));
        actual.next();
        Mockito.verify(tracingPolicy).apply(Mockito.same(REFERENCE), Mockito.same(ID), Mockito.same(INTERVAL), Mockito.eq("next"), Mockito.any());
        actual.hasNext();
        Mockito.verify(tracingPolicy).apply(Mockito.same(REFERENCE), Mockito.same(ID), Mockito.same(INTERVAL), Mockito.eq("hasNext"), Mockito.any());
        actual.peekNext();
        Mockito.verify(tracingPolicy).apply(Mockito.same(REFERENCE), Mockito.same(ID), Mockito.same(INTERVAL), Mockito.eq("peekNext"), Mockito.any());
    }

    @Test
    void debugShouldWrapWhenUpscalableIteratorAndTracingIsEnabled() {
        ExecutorContextImpl context = new ExecutorContextImpl(tracingPolicy);
        when(tracingPolicy.apply(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(null);
        when(tracingPolicy.apply(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.eq("hasNext"), Mockito.any())).thenReturn(true);

        val actual = context.debug(REFERENCE, ID, INTERVAL, upscalableIterator);

        assertThat(actual, not(is(upscalableIterator)));
        actual.next();
        Mockito.verify(tracingPolicy).apply(Mockito.same(REFERENCE), Mockito.same(ID), Mockito.same(INTERVAL), Mockito.eq("next"), Mockito.any());
        actual.hasNext();
        Mockito.verify(tracingPolicy).apply(Mockito.same(REFERENCE), Mockito.same(ID), Mockito.same(INTERVAL), Mockito.eq("hasNext"), Mockito.any());
        actual.peekNext();
        Mockito.verify(tracingPolicy).apply(Mockito.same(REFERENCE), Mockito.same(ID), Mockito.same(INTERVAL), Mockito.eq("peekNext"), Mockito.any());
        actual.peekUpscaleNext(INTERVAL);
        Mockito.verify(tracingPolicy).apply(Mockito.same(REFERENCE), Mockito.same(ID), Mockito.same(INTERVAL), Mockito.eq("peekUpscaleNext"), Mockito.any());
    }
}
