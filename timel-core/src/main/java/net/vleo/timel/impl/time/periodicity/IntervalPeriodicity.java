package net.vleo.timel.impl.time.periodicity;

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

import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;

/**
 * Interface for to couple an Periodicity with IntervalSets.
 *
 * @author Andrea Leofreddi
 */
public interface IntervalPeriodicity {
    /**
     * Retrieve the backend periodicity.
     *
     * @return The backend periodicity implementation used by this class.
     */
    Periodicity getPeriodicity();

    /**
     * Retrieve the supremum of an interval into an {@link IntervalSet}.
     *
     * @param interval
     * @return Resulting chopped {@link IntervalSet}
     */
    TimeIterator<Void> intersectionIter(Interval interval);

    /**
     * Retrieve the supremum of an interval into an {@link IntervalSet}.
     *
     * @param interval
     * @return Resulting chopped {@link IntervalSet}
     */
    TimeIterator<Void> supremumIter(Interval interval);

    /**
     * Retrieve the supremum of an interval into an {@link IntervalSet}.
     *
     * @param interval
     * @return Resulting chopped {@link IntervalSet}
     */
    Interval supremum(final long instant);
}
