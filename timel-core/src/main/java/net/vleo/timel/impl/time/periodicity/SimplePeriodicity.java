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

/**
 * Simple implementation of methods.
 *
 * @author Andrea Leofreddi
 */
public abstract class SimplePeriodicity implements Periodicity {
    @Override
    public DateTime next(DateTime timestamp) {
        if(matches(timestamp))
            timestamp = timestamp.plusMillis(1);

        return ceil(timestamp);
    }

    @Override
    public DateTime previous(DateTime timestamp) {
        if(matches(timestamp))
            timestamp = timestamp.minusMillis(1);

        return floor(timestamp);
    }

    @Override
    public boolean matches(DateTime timestamp) {
        return floor(timestamp).isEqual(timestamp);
    }
}
