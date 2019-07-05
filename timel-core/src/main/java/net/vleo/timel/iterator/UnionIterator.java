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

import java.util.*;

/**
 * An upscalable time iterator which performs the union between a given set of upscalable iterators.
 * <p>
 * This iterator will output values with the highest granularity respect its operands, upscaling the inputs
 * when in need of adapting.
 *
 * @author Andrea Leofreddi
 */
public final class UnionIterator extends BufferedTimeIterator<Object[]> implements UpscalableIterator<Object[]> {
    private static final Comparator<? super UpscalableIterator<?>> COMPARATOR = new Comparator<UpscalableIterator<?>>() {
        @Override
        public int compare(UpscalableIterator<?> t, UpscalableIterator<?> u) {
            long t1, u1;

            t1 = t.peekNext().getInterval().getEnd();
            u1 = u.peekNext().getInterval().getEnd();

            if(t1 != u1)
                return t1 < u1 ? -1 : 1;

            // In case the next entry has the same interval end for both t and u, take one randomly
            // as it is not important in which order they go into the queue.
            //
            // Note that we can't return 0 here because it would imply that t == u (so the either t or u gets suppressed as duplicate).

            return 1;
        }
    };

    private enum Status {UNINITIALIZED, STREAMING, END_OF_DATA}

    private final UpscalableIterator<?>[] arguments;

    private TreeSet<UpscalableIterator<?>> queue;

    private long current = Long.MAX_VALUE;

    private Status status = Status.UNINITIALIZED;

    /**
     * Initialize the queue.
     */
    private void initialize() {
        queue = new TreeSet<UpscalableIterator<?>>(COMPARATOR);

        for(UpscalableIterator<?> upscalableIterator : arguments)
            if(upscalableIterator.hasNext()) {
                Interval i = upscalableIterator.peekNext().getInterval();

                if(current > i.getStart())
                    current = i.getStart();

                queue.add(upscalableIterator);
            }

        status = queue.isEmpty() ? Status.END_OF_DATA : Status.STREAMING;
    }

    private UnionIterator(int size) {
        this.arguments = new UpscalableIterator<?>[size];
    }

    @Override
    protected final Sample<Object[]> concreteNext() {
        Sample<Object[]> result = null;

        while(result == null) {
            switch(status) {
                case UNINITIALIZED:
                    // Initialize if needed
                    initialize();
                    break;

                case STREAMING: {
                    while(queue.first().peekNext().getInterval().getEnd() <= current) {
                        UpscalableIterator<?> head = queue.pollFirst();

                        head.next();

                        if(head.hasNext())
                            // Iterator has more data to read
                            queue.add(head);
                        else if(queue.isEmpty()) {
                            status = Status.END_OF_DATA;

                            break;
                        }
                    }
                }
            }

            // Return null when at END_OF_DATA
            if(status == Status.END_OF_DATA)
                return null;

            long start = Long.MAX_VALUE, end = Long.MAX_VALUE;

            Iterator<UpscalableIterator<?>> i = queue.iterator();

            // Detect the start time
            while(i.hasNext()) {
                Interval k = i.next().peekNext().getInterval();

                if(start > k.getStart())
                    start = k.getStart();
            }

            if(start < current)
                start = current;

            i = queue.iterator();

            // Detect the end time
            while(i.hasNext()) {
                Interval k = i.next().peekNext().getInterval();

                if(end > k.getStart() && k.getStart() > start)
                    end = k.getStart();

                if(end > k.getEnd())
                    end = k.getEnd();
            }
            //
            //            i = queue.iterator();
            //
            //            // Detect end as the minimum end or start before the start detected before
            //            while(i.hasNext()) {
            //                Interval k = i.next().peekNext().getInterval();
            //
            //                if(k.getStart() > start && end > k.getStart())
            //                    end = k.getStart();
            //
            //                if(end > k.getEnd())
            //                    end = k.getEnd();
            //            }
            //
            //            // If we were not able to detect any new start (no gaps), use current
            //            if(start == Long.MAX_VALUE)
            //                start = current;

            // Assemble value
            Interval next = Interval.of(start, end);

            Object[] values = new Object[arguments.length];

            for(int k = 0; k < arguments.length; k++) {
                UpscalableIterator<?> itor = arguments[k];

                if(!itor.hasNext()) {
                    // No value
                    values[k] = null;

                    continue;
                }

                Interval interval = itor.peekNext().getInterval();

                if(!interval.overlaps(next)) {
                    // Next value does not overlap current interval, skipped
                    values[k] = null;

                    continue;
                }

                values[k] = itor.peekUpscaleNext(next).getValue();
            }

            result = Sample.of(next, values);

            current = next.getEnd();
        }

        return result;
    }

    /**
     * Peek the next value upscaling it for the given interval.
     *
     * @param interval The interval to upscale the values with
     * @return Upscaled next values
     */
    @Override
    public final Sample<Object[]> peekUpscaleNext(Interval interval) {
        // Adjust interval to match the next sample interval (which is already upscaled to match the minimum possible length of the next combination)
        interval = peekNext().getInterval().overlap(interval);

        Object[] values = new Object[arguments.length];

        for(int i = 0; i < arguments.length; i++) {
            Sample<?> sample = ((UpscalableIterator<Object>) arguments[i]).peekUpscaleNext(interval);

            values[i] = sample.getValue();
        }

        return Sample.of(
                interval,
                values
        );
    }

    /**
     * Construct a intersect iterator from the given arguments.
     *
     * @param first  First source iterator
     * @param others Rest source iterators
     */
    public UnionIterator(UpscalableIterator<?> first, UpscalableIterator<?>... others) {
        this(others.length + 1);

        arguments[0] = first;

        for(int i = 0; i < others.length; i++)
            arguments[i + 1] = others[i];
    }

    /**
     * Construct a intesect iterator from the given arguments.
     *
     * @param arguments Source iterators
     */
    public UnionIterator(Collection<UpscalableIterator<?>> arguments) {
        this(arguments.size());

        Iterator<UpscalableIterator<?>> iter = arguments.iterator();

        for(int i = 0; iter.hasNext(); i++)
            this.arguments[i] = iter.next();
    }

    @Override
    public String toString() {
        return "UnionIterator{" +
                "arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
