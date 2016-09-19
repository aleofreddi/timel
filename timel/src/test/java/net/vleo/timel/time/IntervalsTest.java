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
package net.vleo.timel.time;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for the {@link Interval} class.
 *
 * @author Andrea Leofreddi
 */
public class IntervalsTest {
    @Test
    public void overlapTest() {
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
