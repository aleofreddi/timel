package net.vleo.timel.impl.target;

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
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;

/**
 * An object that can be evaluated for a certain time interval.
 *
 * @author Andrea Leofreddi
 */
public interface Evaluable<T> {
    /**
     * Evaluate the node for the given interval.
     * <p>
     * IT is mandatory for the implementor to implement evaluateIntervals as a <strong>pure</strong> function,
     * that is a call to evaluateIntervals has no side-effects.
     *
     * @param interval The interval period to evaluate
     * @param context
     * @return A set containing the intervals needed to compute this node
     */
    UpscalableIterator<T> evaluate(Interval interval, ExecutorContext context);
}
