package net.vleo.timel.time;

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

import net.vleo.timel.iterator.TimeIterator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test of the IntervalMaps class.
 *
 * @author Andrea Leofreddi
 */
class IntervalMapsTest {
    @Test
    void shouldIterateRandomData() {
        for(int iters = 0; iters < 10000; iters++) {
            List<Sample<Integer>> values = new ArrayList<>();

            TreeMap<Interval, Integer> map = new TreeMap<>(IntervalMaps.getIntervalEndComparator());

            long position = new Date().getTime(), start = position;

            // Add random samples
            for(int i = 0; i < (int) (10000.0 * Math.random()); i++) {
                int length = 1 + (int) (Math.random() * 120000.0);

                Interval interval = Interval.of(position, position + length);

                values.add(Sample.of(interval, i));

                map.put(interval, i);

                position += length;
            }

            // Now iterate the map forward
            long stop = position, length = stop - start,
                    startOffset = start + (long) (Math.random() * length / 10.0),
                    stopOffset = stop - (long) (Math.random() * length / 10.0);

            Interval j = Interval.of(startOffset, stopOffset);

            // Populate the overlapping values
            ArrayList<Sample<Integer>> valuesOverlap = new ArrayList<>(), valuesForward = new ArrayList<>(), valuesBackward = new ArrayList<>();
            for(Sample<Integer> sample : values)
                if(sample.getInterval().overlaps(j))
                    valuesOverlap.add(sample);

            // Check the forward iterator against the values list in the j interval
            {
                TimeIterator<Integer> forward = IntervalMaps.supremum(map, j);

                while(forward.hasNext())
                    valuesForward.add(forward.next());

                assertEquals(valuesOverlap, valuesForward);
            }

            // Check the forward iterator against the values list in the j interval
            {
                TimeIterator<Integer> backward = IntervalMaps.supremumBackward(map, j);

                while(backward.hasNext())
                    valuesBackward.add(backward.next());

                reverse(valuesOverlap);

                assertEquals(valuesOverlap, valuesBackward);
            }
        }
    }
}
