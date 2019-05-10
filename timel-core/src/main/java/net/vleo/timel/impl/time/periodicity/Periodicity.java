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

import org.joda.time.DateTime;
import org.joda.time.Interval;


/**
 * Interface for generic periodicity.
 *
 * @author Andrea Leofreddi
 */
public interface Periodicity {
   /**
     * Retrieve the ceil of the given timestamp.
     *
     * @param timestamp
     * @return Timestamp ceil
     */
    DateTime ceil(DateTime timestamp);

    /**
     * Retrieve the floor of the given timestamp.
     *
     * @param timestamp
     * @return Timestamp floor
     */
    DateTime floor(DateTime timestamp);

    /**
     * Retrieves the next occurrence of this periodicity
     * that happens after the given timestamp
     *
     * @param timestamp
     * @return
     */
    DateTime next(DateTime timestamp);

    /**
     * Retrieves the previous occurrence of this periodicity
     * that happens after the given timestamp
     *
     * @param timestamp
     * @return
     */
    DateTime previous(DateTime timestamp);

    /**
     * States whether or not the given timestamp
     * coincides with a event of this periodicity
     *
     * @param timestamp
     * @return
     */
    boolean matches(DateTime timestamp);
}
