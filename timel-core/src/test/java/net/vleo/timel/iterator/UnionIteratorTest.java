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

import net.vleo.timel.impl.upscaler.IntegralDoubleUpscaler;
import net.vleo.timel.impl.upscaler.SameUpscaler;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.IntervalMaps;
import net.vleo.timel.time.Sample;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Andrea Leofreddi
 */
class UnionIteratorTest {
    @Test
    void shouldPassThroughSingleArgumentRandomData() {
        TreeMap<Interval, Double> map = new TreeMap<>(IntervalMaps.getIntervalEndComparator());

        DateTime t = DateTime.now();

        for(int i = 0; i < 1000; i++) {
            int n = (int) (Math.random() * 10.0) + 1;

            DateTime u = t.plusSeconds(n);
            map.put(Interval.of(t.getMillis(), u.getMillis()), Math.random());
            t = u;
        }

        IntersectIterator output = new IntersectIterator(Collections.singletonList(
                new UpscalerIterator<>(
                        SameUpscaler.get(),
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

    @Test
    void shouldUnionWithSimpleDataSet() {
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

        TreeMap<Interval, Double> s1 = new TreeMap<>(comp), s2 = new TreeMap<>(comp), s3 = new TreeMap<>(comp);
        TreeMap<Interval, Object[]> rExpected = new TreeMap<>(comp);

        s1.put(Interval.of(a, b), 1.0);
        s1.put(Interval.of(d, e), 1.0);
        s1.put(Interval.of(e, j), 5.0);

        s2.put(Interval.of(c, g), 40.0);
        s2.put(Interval.of(g, h), 10.0);
        s2.put(Interval.of(h, k), 30.0);

        s3.put(Interval.of(f, i), 300.0);
        s3.put(Interval.of(i, l), 300.0);

        rExpected.put(Interval.of(a, b), new Object[] {1.0, null, null});
        rExpected.put(Interval.of(c, d), new Object[] {null, 10.0, null});
        rExpected.put(Interval.of(d, e), new Object[] {1.0, 10.0, null});
        rExpected.put(Interval.of(e, f), new Object[] {1.0, 10.0, null});
        rExpected.put(Interval.of(f, g), new Object[] {1.0, 10.0, 100.0});
        rExpected.put(Interval.of(g, h), new Object[] {1.0, 10.0, 100.0});
        rExpected.put(Interval.of(h, i), new Object[] {1.0, 10.0, 100.0});
        rExpected.put(Interval.of(i, j), new Object[] {1.0, 10.0, 100.0});
        rExpected.put(Interval.of(j, k), new Object[] {null, 10.0, 100.0});
        rExpected.put(Interval.of(k, l), new Object[] {null, null, 100.0});

        Upscaler<Double> upscaler = new IntegralDoubleUpscaler();

        UnionIterator intersect = new UnionIterator(
                new UpscalerIterator<>(upscaler, IntervalMaps.iterator(s1)),
                new UpscalerIterator<>(upscaler, IntervalMaps.iterator(s2)),
                new UpscalerIterator<>(upscaler, IntervalMaps.iterator(s3))
        );

        TimeIterator<Object[]> expected = IntervalMaps.iterator(rExpected);

        while(intersect.hasNext() && expected.hasNext()) {
            Sample<Object[]> t = intersect.next(), u = expected.next();

            assertEquals(u.getInterval(), t.getInterval());
            assertArrayEquals(u.getValue(), t.getValue());
        }

        assertFalse(intersect.hasNext());
        assertFalse(expected.hasNext());
    }

    @Test
    void shouldUnionWhenMultipleIteratorsWithRandomData() {
        List<TreeMap<Interval, Double>> maps = new ArrayList<TreeMap<Interval, Double>>();

        List<UpscalableIterator<?>> iters = new ArrayList<UpscalableIterator<?>>();

        int[] primes = new int[] {3, 5, 7, 11};

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
                    new UpscalerIterator<Double>(
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
}
