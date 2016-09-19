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
package net.vleo.timel.iterator;

import net.vleo.timel.executor.ExecutionException;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.impl.upscaler.SameUpscaler;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.IntervalMaps;
import net.vleo.timel.type.Types;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Test for the {@link IntersectIterator} class.
 *
 * @author Andrea Leofreddi
 */
public class IntersectIteratorTest {
    public DateTime of(int year, int month, int day) {
        return new DateTime(year, month, day, 0, 0);
    }

    public static String debug(Interval i) {
        char cStart = (char) ('a' - 1 + new DateTime(i.getStart()).getDayOfMonth()),
                cEnd = (char) ('a' - 1 + new DateTime(i.getEnd()).getDayOfMonth());

        return "[" + cStart + ", " + cEnd + "]";
    }

    public static String debug(Sample<?> a) {
        return debug(a.getInterval()) + "(" + a.getValue() + ")";
    }

    /**
     * This method will test the correct behaviour of IntersectIterator when dealing with a single argument.
     *
     * @throws ExecutionException
     */
    @Test
    public void testSingle() {
        TreeMap<Interval, Double> map = new TreeMap<Interval, Double>(IntervalMaps.getIntervalEndComparator());

        DateTime t = DateTime.now();

        for(int i = 0; i < 1000; i++) {
            int n = (int) (Math.random() * 10.0) + 1;

            DateTime u = t.plusSeconds(n);

            map.put(Interval.of(t.getMillis(), u.getMillis()), Math.random());

            t = u;
        }

        IntersectIterator output = new IntersectIterator(Arrays.<UpscalableIterator<?>>asList(
                new UpscalerWrapperIterator<Double>(
                        SameUpscaler.<Double>get(),
                        IntervalMaps.iterator(map)
                )
        ));

        TimeIterator<Double> expected = IntervalMaps.iterator(map);

        while(expected.hasNext() && output.hasNext()) {
            Sample<Object[]> v = output.next();

            assertEquals(
                    expected.next(),
                    v.copyWithValue((Double) v.getValue()[0])
            );
        }

        assertFalse(output.hasNext());
    }

    /**
     * This test will check IntersectIterator against a set of maps with
     * progressive values and different interval length (each a prime number of seconds).
     */
    @Test
    public void testMulti() {
        List<TreeMap<Interval, Double>> maps = new ArrayList<TreeMap<Interval, Double>>();

        List<UpscalableIterator<?>> iters = new ArrayList<UpscalableIterator<?>>();

        int[] primes = new int[]{3, 5, 7, 11};

        int iterations = 10;

        for(int prime : primes)
            iterations *= prime;

        DateTime t = DateTime.now();

        for(int i = 0; i < primes.length; i++) {
            TreeMap<Interval, Double> map = new TreeMap<Interval, Double>(IntervalMaps.getIntervalEndComparator());

            for(int j = 0; j < iterations / primes[i]; j++) {
                map.put(
                        Interval.of(
                                t.plusSeconds(j * primes[i]).getMillis(),
                                t.plusSeconds((j + 1) * primes[i]).getMillis()
                        ),
                        (double) j
                );
            }

            maps.add(map);

            iters.add(
                    new UpscalerWrapperIterator<Double>(
                            SameUpscaler.<Double>get(),
                            IntervalMaps.iterator(map)
                    )
            );
        }

        IntersectIterator output = new IntersectIterator(iters);

        int f = 0;

        while(output.hasNext()) {
            Sample<Object[]> sample = output.next();

            int d = (int) (Interval.of(t.getMillis(), sample.getInterval().getStart()).toDurationMillis() / 1000L);

            f = (int) (Interval.of(t.getMillis(), sample.getInterval().getEnd()).toDurationMillis() / 1000L);

            // Compute expected value e
            int e = 0;

            for(int i = 0; i < primes.length; i++)
                e += d / primes[i];

            // Compute value v
            int v = 0;

            for(Object o : sample.getValue())
                v += ((Double) o).intValue();

            assertEquals(e, v);
        }

        assertEquals(iterations, f);
    }

    @Test
    public void basicTest() {
        long a = new DateTime(2010, 1, 1, 0, 0).getMillis(),
                b = new DateTime(2010, 1, 2, 0, 0).getMillis(),
                c = new DateTime(2010, 1, 3, 0, 0).getMillis(),
                d = new DateTime(2010, 1, 4, 0, 0).getMillis(),
                e = new DateTime(2010, 1, 5, 0, 0).getMillis(),
                f = new DateTime(2010, 1, 6, 0, 0).getMillis(),
                g = new DateTime(2010, 1, 7, 0, 0).getMillis(),
                h = new DateTime(2010, 1, 8, 0, 0).getMillis(),
                i = new DateTime(2010, 1, 9, 0, 0).getMillis(),
                j = new DateTime(2010, 1, 10, 0, 0).getMillis(),
                k = new DateTime(2010, 1, 11, 0, 0).getMillis(),
                l = new DateTime(2010, 1, 12, 0, 0).getMillis();

        Comparator<Interval> comp = IntervalMaps.getIntervalEndComparator();

        TreeMap<Interval, Double> s1 = new TreeMap<Interval, Double>(comp),
                s2 = new TreeMap<Interval, Double>(comp),
                s3 = new TreeMap<Interval, Double>(comp);

        TreeMap<Interval, Object[]> rExpected = new TreeMap<Interval, Object[]>(comp);

        s1.put(Interval.of(a, b), 1.0);
        s1.put(Interval.of(d, e), 1.0);
        s1.put(Interval.of(e, j), 5.0);

        s2.put(Interval.of(c, g), 40.0);
        s2.put(Interval.of(g, h), 10.0);
        s2.put(Interval.of(h, k), 30.0);

        s3.put(Interval.of(f, i), 300.0);
        s3.put(Interval.of(i, l), 300.0);

        rExpected.put(Interval.of(f, g), new Object[]{1.0, 10.0, 100.0});
        rExpected.put(Interval.of(g, h), new Object[]{1.0, 10.0, 100.0});
        rExpected.put(Interval.of(h, i), new Object[]{1.0, 10.0, 100.0});
        rExpected.put(Interval.of(i, j), new Object[]{1.0, 10.0, 100.0});

        Upscaler<Double> upscaler = Types.getIntegralDoubleType(1).getUpscaler();

        IntersectIterator intersect = new IntersectIterator(
                new UpscalerWrapperIterator<Double>(upscaler, IntervalMaps.iterator(s1)),
                new UpscalerWrapperIterator<Double>(upscaler, IntervalMaps.iterator(s2)),
                new UpscalerWrapperIterator<Double>(upscaler, IntervalMaps.iterator(s3))
        );

        TimeIterator<Object[]> expected = IntervalMaps.iterator(rExpected);

        // Can't use assertEquals(rExpected, rGot); as we have arrays as values
        while(intersect.hasNext() && expected.hasNext()) {
            Sample<Object[]> t = intersect.next(), u = expected.next();

            assertEquals(u.getInterval(), t.getInterval());
            assertArrayEquals(u.getValue(), t.getValue());
        }

        assertFalse(intersect.hasNext());
        assertFalse(expected.hasNext());
    }
}
