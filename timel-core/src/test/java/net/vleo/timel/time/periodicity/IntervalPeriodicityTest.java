package net.vleo.timel.time.periodicity;

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

import net.vleo.timel.impl.time.periodicity.*;
import net.vleo.timel.time.CalendarField;
import net.vleo.timel.time.Interval;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for IntervalPeriodicity.
 *
 * @author Andrea Leofreddi
 */
class IntervalPeriodicityTest {
    @Test
    void testSupremum() {
        Periodicity p = new CyclicPeriodicity(CalendarField.DAY_OF_MONTH, 1);

        IntervalPeriodicity w = new IntervalPeriodicityWrapper(
                new TimeZoneWrappedPeriodicity(p, DateTimeZone.UTC)
        );

        long a = 1454284800000L, b = a + 86400000L, c = b + 86400000L;

        assertEquals(
                Interval.of(
                        a,
                        b
                ),
                w.supremum(a)
        );

        assertEquals(
                Interval.of(
                        a,
                        b
                ),
                w.supremum(a + 3600000L)
        );

        assertEquals(
                Interval.of(
                        a,
                        b
                ),
                w.supremum(b - 1)
        );

        assertEquals(
                Interval.of(
                        b,
                        c
                ),
                w.supremum(b)
        );
    }
}
