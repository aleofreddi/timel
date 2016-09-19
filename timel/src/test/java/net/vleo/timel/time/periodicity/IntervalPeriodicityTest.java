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
package net.vleo.timel.time.periodicity;

import net.vleo.timel.impl.time.periodicity.*;
import net.vleo.timel.time.CalendarField;
import net.vleo.timel.time.Interval;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for IntervalPeriodicity.
 *
 * @author Andrea Leofreddi
 */
public class IntervalPeriodicityTest {
    @Test
    public void testSupremum() {
        Periodicity p = new CyclicPeriodicity(CalendarField.DAY_OF_MONTH, 1);

        IntervalPeriodicity w = new IntervalPeriodicityWrapper(
                new TimeZoneWrappedPeriodicity(p, DateTimeZone.UTC)
        );

        long a = 1454284800000L, b = a + 86400000L, c = b + 86400000L;

        Assert.assertEquals(
                Interval.of(
                        a,
                        b
                ),
                w.supremum(a)
        );

        Assert.assertEquals(
                Interval.of(
                        a,
                        b
                ),
                w.supremum(a + 3600000L)
        );

        Assert.assertEquals(
                Interval.of(
                        a,
                        b
                ),
                w.supremum(b - 1)
        );

        Assert.assertEquals(
                Interval.of(
                        b,
                        c
                ),
                w.supremum(b)
        );
    }
}
