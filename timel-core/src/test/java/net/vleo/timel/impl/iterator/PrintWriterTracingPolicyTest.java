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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.util.concurrent.Callable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Andrea Leofreddi
 */
@ExtendWith(MockitoExtension.class)
class PrintWriterTracingPolicyTest {
    @Mock
    private PrintWriter printWriter;
    @Mock
    private Callable<?> callback;
    @InjectMocks
    private PrintWriterTracingPolicy policy;

    @Test
    void applyShouldWriteToStream() throws Exception {
        policy.apply("node1", "id42", Interval.of(0, 1000), "next", callback);

        verify(printWriter, times(2)).println(any(String.class));
        verify(callback).call();
    }

    @Test
    void closeShouldCloseWriter() {
        val policy = new PrintWriterTracingPolicy(printWriter);

        policy.close();
        verify(printWriter).close();
    }
}
