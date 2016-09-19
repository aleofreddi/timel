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
import net.vleo.timel.iterator.BufferedTimeIterator;
import net.vleo.timel.iterator.TimeIterator;

import java.util.*;

/**
 * Static method class to adapt a SortedMap to the time iterator interface.
 *
 * @author Andrea Leofreddi
 */
public final class IntervalMaps {
    private IntervalMaps() {
        throw new AssertionError();
    }

    private static final Comparator<Interval> INTERVAL_END_COMPARATOR = new Comparator<Interval>() {
        public int compare(Interval t, Interval u) {
            long a = t.getEnd(), b = u.getEnd();

            if(a < b)
                return -1;

            if(a > b)
                return 1;

            return 0;
        }
    };

    /**
     * Retrieve a comparator that will order intervals by end ascending.
     *
     * @return An interval comparator
     */
    public static Comparator<Interval> getIntervalEndComparator() {
        return INTERVAL_END_COMPARATOR;
    }

    /**
     * Retrieves a navigable map initialized with the given values.
     *
     * @param first
     * @param others
     * @param <V>
     * @return
     */
    public static <V> NavigableMap<Interval, V> of(
            Sample<V> first,
            Sample<V>... others
    ) {
        TreeMap<Interval, V> map = new TreeMap<Interval, V>(getIntervalEndComparator());

        map.put(first.getInterval(), first.getValue());

        for(Sample<V> sample : others)
            map.put(sample.getInterval(), sample.getValue());

        return map;
    }

    /**
     * Returns the supremum of interval on given map, that is an iterator that will iterate
     * all map intervals overlapping the given interval parameter.
     *
     * <b>For this method to work it is mandatory that the map is ordered by Interval.end (see #getIntervalEndComparator)</b>
     *
     * @param map      The map to iterate
     * @param interval Interval to use as a reference for the supremum intersection.
     * @return A time iterator
     */
    public static <V> TimeIterator<V> supremum(final NavigableMap<Interval, V> map, final Interval interval) {
        return new BufferedTimeIterator<V>() {
            long position = interval.getStart();

            @Override
            protected Sample<V> concreteNext() {
                Interval i = Interval.of(position, position);

                Map.Entry<Interval, V> entry = map.higherEntry(i);

                // Check if we are not out of boundary
                if(entry == null || entry.getKey().getStart() >= interval.getEnd())
                    return null;

                position = entry.getKey().getEnd();

                return Sample.of(
                        entry.getKey(),
                        entry.getValue()
                );
            }
        };
    }

    public static <V> TimeIterator<V> supremum(final SampleMap<V> map, final Interval interval) {
        throw new IllegalArgumentException("When dealing with SampleMaps, use the time iterator methods there");
    }

    public static <V> TimeIterator<V> supremumBackward(final NavigableMap<Interval, V> map, final Interval interval) {
        return new BufferedTimeIterator<V>() {
            long position = interval.getEnd();

            @Override
            protected Sample<V> concreteNext() {
                Interval i = Interval.of(position, position);

                Map.Entry<Interval, V> entry = map.ceilingEntry(i);

                // Check if we are not out of boundary
                if(entry == null || entry.getKey().getStart() > interval.getEnd() || entry.getKey().getStart() >= position)
                    entry = map.lowerEntry(i);

                if(entry == null || entry.getKey().getEnd() <= interval.getStart() || position == entry.getKey().getStart())
                    return null;

                position = entry.getKey().getStart();

                return Sample.of(
                        entry.getKey(),
                        entry.getValue()
                );
            }
        };
    }

    /**
     * Returns a time iterator for the given map.
     *
     * <b>For this method to work it is mandatory that the map is ordered by Interval.end (see #getIntervalEndComparator)</b>
     *
     * @param map The map to iterate
     * @return A time iterator
     */
    public static <V> TimeIterator<V> iterator(SortedMap<Interval, V> map) {
        final Iterator<Map.Entry<Interval, V>> iterator = map.entrySet().iterator();

        return new BufferedTimeIterator<V>() {
            @Override
            protected Sample<V> concreteNext() {
                if(!iterator.hasNext())
                    return null;

                Map.Entry<Interval, V> value = iterator.next();

                return Sample.of(value.getKey(), value.getValue());
            }
        };
    }
}
