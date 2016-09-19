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
package net.vleo.timel.executor.variable;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;

/**
 * Interface for variables. A variable should a consistent sample store.
 *
 * @author Andrea Leofreddi
 */
public interface Variable<V> {
    /**
     * Read the given interval returning a forward time iterator from start to end.
     *
     * @param interval The interval to read
     * @param context Evaluation context
     */
    TimeIterator<V> readForward(Interval interval, ExecutorContext context);

    /**
     * Read the given interval returning a reverse time iterator from the end to start.
     *
     * @param interval The interval to read
     * @param context Evaluation context
     */
    TimeIterator<V> readBackward(Interval interval, ExecutorContext context);

    /**
     * Read values for a given interval, using the given backend node.
     *
     * @param sample The sample to store
     */
    void write(Sample<V> sample, ExecutorContext context);
}
