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
import net.vleo.timel.executor.ExecutionException;
import net.vleo.timel.time.Interval;

import java.util.*;

/**
 * An upscalable time iterator which performs the intersections between a given set of upscalable iterators.
 *
 * This iterator will output values with the highest granularity respect its operands, upscaling the inputs
 * when in need of adapting.
 *
 * @author Andrea Leofreddi
 */
public final class IntersectIterator extends BufferedTimeIterator<Object[]> implements UpscalableIterator<Object[]> {
    private static final Comparator<? super UpscalableIterator<?>> COMPARATOR = new Comparator<UpscalableIterator<?>>() {
        @Override
        public int compare(UpscalableIterator<?> t, UpscalableIterator<?> u) {
            long t1, u1;

            try {
                t1 = t.peekNext().getInterval().getEnd();
                u1 = u.peekNext().getInterval().getEnd();
            } catch(ExecutionException e) {
                throw new RuntimeException(e);
            }

            if(t1 != u1)
                return t1 < u1 ? -1 : 1;

            // In case the next entry has the same interval end for both t and u, take one randomly
            // as it is not important in which order they go into the queue.
            //
            // Note that we can't return 0 here because TreeSet would then deduce that t == u and suppress one entry.

            return 1;
        }
    };

    private enum Status {UNINITIALIZED, STREAMING, END_OF_DATA}

    private final UpscalableIterator<?>[] arguments;

    private TreeSet<UpscalableIterator<?>> queue;

    private Status status = Status.UNINITIALIZED;

    /**
     * Initialize the queue.
     */
    private void initialize() {
        queue = new TreeSet<UpscalableIterator<?>>(COMPARATOR);

        for(UpscalableIterator<?> upscalableIterator : arguments) {
            if(!upscalableIterator.hasNext()) {
                status = Status.END_OF_DATA;

                return;
            }

            queue.add(upscalableIterator);
        }

        status = Status.STREAMING;
    }

    private IntersectIterator(int size) {
        this.arguments = new UpscalableIterator<?>[size];
    }

    @Override
    protected final Sample<Object[]> concreteNext() {
        Sample<Object[]> result = null;

        for(Interval next, j; result == null; ) {
            switch(status) {
                case UNINITIALIZED:
                    // Initialize if needed
                    initialize();
                    break;

                case STREAMING: {
                    // Pop first element if streaming
                    UpscalableIterator<?> head = queue.pollFirst();

                    head.next();

                    if(!head.hasNext()) {
                        // Nothing else to read
                        status = Status.END_OF_DATA;

                        return null;
                    }

                    queue.add(head);

                    break;
                }
            }

            // Return null when at END_OF_DATA
            if(status == Status.END_OF_DATA)
                return null;

            Iterator<UpscalableIterator<?>> i = queue.iterator();

            // Find the next occurrence
            j = i.next().peekNext().getInterval();

            while(j != null && i.hasNext()) {
                Interval k = i.next().peekNext().getInterval();

                if(j.overlaps(k))
                    j = j.overlap(k);
                else
                    j = null;
            }

            // Check if we got a match
            if(j != null && !i.hasNext()) {
                next = j;

                Object[] values = new Object[arguments.length];

                for(int k = 0; k < arguments.length; k++)
                    values[k] = arguments[k].peekUpscaleNext(next).getValue();

                result = Sample.of(next, values);
            }
        }

        return result;
    }

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
     * @param first
     * @param others
     */
    public IntersectIterator(UpscalableIterator<?> first, UpscalableIterator<?>... others) {
        this(others.length + 1);

        arguments[0] = first;

        for(int i = 0; i < others.length; i++)
            arguments[i + 1] = others[i];
    }

    /**
     * Construct a intesect iterator from the given arguments.
     *
     * @param arguments
     */
    public IntersectIterator(Collection<UpscalableIterator<?>> arguments) {
        this(arguments.size());

        Iterator<UpscalableIterator<?>> iter = arguments.iterator();

        for(int i = 0; iter.hasNext(); i++)
            this.arguments[i] = iter.next();
    }

    @Override
    public String toString() {
        return "IntersectIterator{" +
                "arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
