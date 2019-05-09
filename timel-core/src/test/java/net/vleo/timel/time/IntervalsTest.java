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

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for the {@link Interval} class.
 *
 * @author Andrea Leofreddi
 */
class IntervalsTest {
    @Test
    void shouldOverlap() {
        Interval i = Interval.of(
                new DateTime(2014, 12, 15, 0, 0, 0).getMillis(),
                new DateTime(2015, 2, 1, 0, 0, 0).getMillis()
        );

        Interval j = Interval.of(
                new DateTime(2015, 1, 3, 0, 0, 0).getMillis(),
                new DateTime(2015, 1, 28, 0, 0, 0).getMillis()
        );

        assertTrue(i.overlaps(j));
        assertTrue(j.overlaps(i));
    }
}
