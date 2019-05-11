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

import net.vleo.timel.time.Interval;
import net.vleo.timel.time.Sample;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andrea Leofreddi
 */
class SplitAdapterTimeIteratorTest {
    @Test
    void shouldSplit() {
        final long start = DateTime.now().getMillis();
        final int samples = (int) (Math.random() * 1000000.0);

        // Generate all the integers between 0 and samples
        BufferedTimeIterator<Integer> iota = new BufferedTimeIterator<Integer>() {
            int i = 0;

            @Override
            protected Sample<Integer> concreteNext() {
                int r = i++;

                if(r < samples)
                    return Sample.of(
                            Interval.of(start + r, start + r + 1),
                            r
                    );

                return null;
            }
        };

        // Split them into 2 samples when value is odd and no value when value is even
        SplitAdapterTimeIterator<Integer, Integer> randomSplitter = new SplitAdapterTimeIterator<Integer, Integer>(
                iota
        ) {
            @Override
            protected List<Sample<Integer>> adapt(Sample<Integer> sample) {
                int v = sample.getValue();

                if(v % 2 == 0) {
                    // Split
                    return Arrays.asList(
                            Sample.of(Interval.of(start + v, start + v + 1), v),
                            Sample.of(Interval.of(start + v + 1, start + v + 2), v + 1)
                    );
                } else
                    // Suppress
                    return Collections.emptyList();
            }
        };

        // Read the results and ensure that we've get back everything in 0..samples
        {
            int j = 0;

            while(randomSplitter.hasNext()) {
                if(Math.random() > 0.5) {
                    Sample<Integer> peekNext = randomSplitter.peekNext();

                    assertEquals(j, (int) peekNext.getValue());
                }

                Sample<Integer> next = randomSplitter.next();

                assertEquals(j++, (int) next.getValue());
            }

            assertEquals(samples % 2 == 0 ? samples : samples + 1, j);
        }
    }
}
