/*
 * Copyright 2014-2016 Andrea Leofreddi
 *
 * This file is part of TimEL.
 *
 * TimEL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TimEL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TimEL.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.vleo.timel.time;

import net.vleo.timel.executor.Sample;
import net.vleo.timel.iterator.TimeIterator;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Unit test of the IntervalMaps class.
 *
 * @author Andrea Leofreddi
 */
public class IntervalMapsTest {
    @Test
    public void testSupremum() throws Exception {
        for(int iters = 0; iters < 100000; iters++) {
            List<Sample<Integer>> values = new ArrayList<Sample<Integer>>();

            TreeMap<Interval, Integer> map = new TreeMap<Interval, Integer>(IntervalMaps.getIntervalEndComparator());

            long position = new Date().getTime(), start = position;

            // Add random samples
            for(int i = 0; i < (int) (10000.0 * Math.random()); i++) {
                int length = 1 + (int) (Math.random() * 120000.0);

                Interval interval = Interval.of(position, position + length);

                values.add(Sample.of(interval, i));

                map.put(interval, i);

                position += length;
            }

            long stop = position;

            long length = stop - start;

            // Now iterate the map forward
            long startOffset = start + (long) (Math.random() * length / 10.0),
                    stopOffset = stop - (long) (Math.random() * length / 10.0);

            Interval j = Interval.of(startOffset, stopOffset);

            ArrayList<Sample<Integer>> valuesOverlap = new ArrayList<Sample<Integer>>(),
                    valuesForward = new ArrayList<Sample<Integer>>(),
                    valuesBackward = new ArrayList<Sample<Integer>>();

            // Populate the overlapping values
            for(Sample<Integer> sample : values)
                if(sample.getInterval().overlaps(j))
                    valuesOverlap.add(sample);

            // Check the forward iterator against the values list in the j interval
            {
                TimeIterator<Integer> forward = IntervalMaps.supremum(map, j);

                while(forward.hasNext())
                    valuesForward.add(forward.next());

                Assert.assertEquals(valuesOverlap, valuesForward);
            }

            // Check the forward iterator against the values list in the j interval
            {
                TimeIterator<Integer> backward = IntervalMaps.supremumBackward(map, j);

                while(backward.hasNext())
                    valuesBackward.add(backward.next());

                Collections.reverse(valuesOverlap);

                Assert.assertEquals(valuesOverlap, valuesBackward);
            }
        }
    }

    @Test
    public void testSampleMap() throws Exception {
        for(int iters = 0; iters < 100000; iters++) {
            List<Sample<Integer>> values = new ArrayList<Sample<Integer>>();

            SampleMap<Integer> map = new SampleMap<Integer>();

            long position = new Date().getTime(), start = position;

            // Add random samples
            for(int i = 0; i < (int) (10000.0 * Math.random()); i++) {
                int length = 1 + (int) (Math.random() * 120000.0);

                Interval interval = Interval.of(position, position + length);

                values.add(Sample.of(interval, i));

                map.put(interval, i);

                position += length;
            }

            long stop = position;

            long length = stop - start;

            // Now iterate the map forward
            long startOffset = start + (long) (Math.random() * length / 10.0),
                    stopOffset = stop - (long) (Math.random() * length / 10.0);

            Interval j = Interval.of(startOffset, stopOffset);

            ArrayList<Sample<Integer>> valuesOverlap = new ArrayList<Sample<Integer>>(),
                    valuesForward = new ArrayList<Sample<Integer>>(),
                    valuesBackward = new ArrayList<Sample<Integer>>();

            // Populate the overlapping values
            for(Sample<Integer> sample : values)
                if(sample.getInterval().overlaps(j))
                    valuesOverlap.add(sample);

            // Check the forward iterator against the values list in the j interval
            {
                TimeIterator<Integer> forward = map.forwardTimeIterator(j); //IntervalMaps.supremum(map, j);

                while(forward.hasNext())
                    valuesForward.add(forward.next());

                Assert.assertEquals(valuesOverlap, valuesForward);
            }

            // Check the forward iterator against the values list in the j interval
            {
                TimeIterator<Integer> backward = map.backwardTimeIterator(j);

                while(backward.hasNext())
                    valuesBackward.add(backward.next());

                Collections.reverse(valuesOverlap);

                Assert.assertEquals(valuesOverlap, valuesBackward);
            }
        }
    }
}
