package net.vleo.timel.variable;

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

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.time.Sample;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.Type;

/**
 * Interface for variables. A variable should be a consistent sample store.
 *
 * @param <V> Value Java type
 * @author Andrea Leofreddi
 */
public interface Variable<V> {
    /**
     * Read the given interval returning a forward time iterator from start to end.
     * More specifically, return an iterator for all the samples whose interval overlaps or is contained by interval.
     *
     * @param interval The interval to read
     * @param context  Evaluation context
     * @return A {@link TimeIterator} reading the map forward for the given interval.
     */
    TimeIterator<V> readForward(Interval interval, ExecutorContext context);

    /**
     * Read the given interval returning a reverse time iterator from the end to start.
     * More specifically, return an iterator for all the samples whose interval overlaps or is contained by interval in reverse order.
     *
     * @param interval The interval to read
     * @param context  Evaluation context
     * @return A {@link TimeIterator} reading the map backward for the given interval.
     */
    TimeIterator<V> readBackward(Interval interval, ExecutorContext context);

    /**
     * Write a sample into the variable.
     *
     * @param sample The sample to store
     * @param context Executor context
     */
    void write(Sample<V> sample, ExecutorContext context);
}
