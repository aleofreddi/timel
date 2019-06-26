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

import net.vleo.timel.iterator.BufferedTimeIterator;
import net.vleo.timel.iterator.TimeIterator;

import java.util.*;

/**
 * Static method class to adapt a {@link NavigableMap} into a {@link TimeIterator}.
 *
 * @author Andrea Leofreddi
 */
public final class IntervalMaps {
    private IntervalMaps() {
        throw new AssertionError();
    }

    private static final Comparator<Interval> INTERVAL_END_COMPARATOR = Comparator.comparing(Interval::getEnd);

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
     * @param first First sample
     * @param others Rest samples
     * @param <V> Value Java type
     * @return A NavigableMap filled with the given samples
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
     * Returns the supremum of interval on given map, that is an iterator that will iterate all map intervals overlapping the given interval parameter.
     *
     * <b>For this method to work properly, it is mandatory that the map is ordered by Interval.end (see #getIntervalEndComparator)</b>
     *
     * @param map      The map to iterate
     * @param interval Interval to use as a reference for the supremum intersection
     * @param <V>      Value Java type
     * @return Supremum time iterator
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

    /**
     * Reverse version of the supremum method.
     *
     * @param map      The map to iterate
     * @param interval Interval to use as a reference for the supremum intersection
     * @param <V>      Value Java type
     * @return The backward supremum time iterator
     */
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
     * @param <V> Value Java type
     * @param map The map to iterate
     * @return A time iterator
     */
    public static <V> TimeIterator<V> iterator(NavigableMap<Interval, V> map) {
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
