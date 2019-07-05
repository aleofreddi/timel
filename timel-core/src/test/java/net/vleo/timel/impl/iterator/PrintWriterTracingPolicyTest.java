package net.vleo.timel.impl.iterator;

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
import net.vleo.timel.time.Interval;
import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Andrea Leofreddi
 */
@ExtendWith(MockitoExtension.class)
class PrintWriterTracingPolicyTest {
    @Mock
    private PrintWriter printWriter;
    @Mock
    private Callable<?> callback;
    @Captor
    ArgumentCaptor<String> capturedEntries;
    @InjectMocks
    private PrintWriterTracingPolicy policy;

    @ParameterizedTest
    @ValueSource(strings = {"next", "hasNext"})
    void applyShouldWriteASingleEntryToStream(String method) throws Exception {
        policy.apply("node1", "id42", Interval.of(0, 1000), method, callback);

        verify(printWriter, times(2)).println(any(String.class));
        verify(callback).call();
    }

    @ParameterizedTest
    @ValueSource(strings = {"peek", "peekUpscale"})
    void applyShouldIgnoreOtherMethods(String method) throws Exception {
        policy.apply("node1", "id42", Interval.of(0, 1000), method, callback);

        verifyNoMoreInteractions(printWriter);
        verify(callback).call();
    }

    @Test
    void applyShouldRethrowCallbackException() throws Exception {
        val expected = new IllegalArgumentException();

        val actual = assertThrows(RuntimeException.class, () ->
                policy.apply("node1", "id42", Interval.of(0, 1000), "next", () -> {
                    throw expected;
                })
        );

        assertThat(actual.getCause(), sameInstance(expected));
    }

    @ParameterizedTest
    @ValueSource(strings = {"next", "hasNext"})
    void applyShouldIndentNestedEntries(String method) throws Exception {
        Callable<Void> nested = () -> {
            policy.apply("node1", "id42", Interval.of(0, 1000), "next", callback);
            return null;
        };

        policy.apply("node1", "id42", Interval.of(0, 1000), "next", nested);

        verify(printWriter, times(4)).println(capturedEntries.capture());
        val logs = capturedEntries.getAllValues();
        verify(callback).call();

        assertThat(logs.get(0), not(StringStartsWith.startsWith("    ")));
        assertThat(logs.get(1), StringStartsWith.startsWith("    "));
        assertThat(logs.get(2), StringStartsWith.startsWith("    "));
        assertThat(logs.get(3), not(StringStartsWith.startsWith("    ")));
    }

    @Test
    void closeShouldCloseWriter() {
        val policy = new PrintWriterTracingPolicy(printWriter);

        policy.close();
        verify(printWriter).close();
    }
}
