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

import net.vleo.timel.executor.Sample;
import net.vleo.timel.time.Interval;

import java.util.*;

/**
 * A time iterator which performs the coalesce between a given set of upscaler iterators in a sequential way.
 *
 * This iterator will output values with the highest granularity respect its operands, upscaling the inputs
 * when in need of adapting.
 *
 * @author Andrea Leofreddi
 */
public final class CoalesceIterator<V> extends BufferedTimeIterator<V> {
    private static final <T extends Comparable<? super T>> T min(T t, T u) {
        if(t == null)
            return u;

        if(u == null)
            return t;

        if(t.compareTo(u) < 0)
            return t;

        return u;
    }

    private static final <T extends Comparable<? super T>> T max(T t, T u) {
        if(t == null)
            return u;

        if(u == null)
            return t;

        if(t.compareTo(u) > 0)
            return t;

        return u;
    }

    private final List<TimeIterator<?>> arguments;

    private Long time = null;

    private CoalesceIterator(int size) {
        this.arguments = new ArrayList<TimeIterator<?>>(size);
    }

    @Override
    protected Sample<V> concreteNext() {
        ArrayList<Sample<Integer>> values = new ArrayList<Sample<Integer>>(arguments.size());

        // Populate next intervals from all the iterator
        for(int i = 0; i < arguments.size(); i++) {
            TimeIterator<?> itor = arguments.get(i);

            // Fast forward til time
            while(time != null && itor.hasNext() && itor.peekNext().getInterval().getEnd() <= time) {
                //System.out.println("Ok throttling " + itor.next() + ", new next is " + itor.peekNext());
                itor.next();
            }

            // Add the current value to the values set
            if(itor.hasNext())
                values.add(Sample.of(itor.peekNext().getInterval(), i));
            else
                values.add(null);
        }

        ArrayList<Sample<Integer>> priorities;

        // Prioritize the fetch values
        {
            priorities = new ArrayList<Sample<Integer>>(values.size());

            // Copy only non-null values
            for(Sample<Integer> value : values)
                if(value != null)
                    priorities.add(value);

            // Order values by max(start, time), position
            Collections.sort(
                    priorities,
                    new Comparator<Sample<Integer>>() {
                        @Override
                        public int compare(Sample<Integer> t, Sample<Integer> u) {
                            long a = max(t.getInterval().getStart(), time),
                                    b = max(u.getInterval().getStart(), time);

                            if(a != b)
                                return a < b ? -1 : 1;

                            return t.getValue().compareTo(u.getValue());
                        }
                    }
            );
        }

        if(priorities.isEmpty())
            // We have reached end of data
            return null;

        Sample<Integer> selected = priorities.get(0);

        // Get the index of the selected iterator
        int position = selected.getValue();

        // Compute the interval for the entry
        long start = max(time, selected.getInterval().getStart()),
                stop = selected.getInterval().getEnd();

        // Adjust stop
        for(int i = position - 1; i >= 0; i--)
            if(values.get(i) != null)
                stop = min(stop, values.get(i).getInterval().getStart());

        // Update time with the current end of the sample
        time = stop;

        Sample<V> v;

        if(position == 0)
            // First iterator might not be an upscaler, we use then peekNext
            v = (Sample<V>) arguments.get(position).peekNext();
        else
            // We get the upscaled value starting from time
            v = (Sample<V>) ((UpscalableIterator<?>) arguments.get(position)).peekUpscaleNext(Interval.of(start, stop));

        return v;
    }

    /**
     * Construct a coalesce iterator from the given arguments.
     *
     * @param first
     * @param others
     */
    public CoalesceIterator(TimeIterator<?> first, UpscalableIterator<?>... others) {
        this(others.length + 1);

        arguments.add(first);

        arguments.addAll(Arrays.asList(others));
    }

    /**
     * Construct a coalesce iterator from the given arguments.
     *
     * @param arguments
     */
    public CoalesceIterator(Collection<UpscalableIterator<V>> arguments) {
        this(arguments.size());

        this.arguments.addAll(arguments);
    }

    @Override
    public final String toString() {
        return "CoalesceIterator{" +
                "arguments=" + arguments +
                '}';
    }
}
