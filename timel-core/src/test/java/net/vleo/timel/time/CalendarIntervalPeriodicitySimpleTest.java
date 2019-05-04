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

import net.vleo.timel.impl.time.periodicity.CyclicPeriodicity;
import net.vleo.timel.impl.time.periodicity.Periodicity;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Andrea Leofreddi
 */
class CalendarIntervalPeriodicitySimpleTest {
    @ParameterizedTest
    @CsvSource({
            "WEEK_OF_YEAR,2,6",
            "DAY_OF_YEAR,3,0",
    })
    void shouldIterateConsistently(CalendarField field, int size, int iterations) {
        Periodicity periodicty = new CyclicPeriodicity(field, size);

        DateTime reference = IntervalTestHelper.parse("2015-01-01T00:00:00 Europe/Rome");
        reference = reference.withZone(DateTimeZone.forID("Europe/Rome"));

        DateTime t = reference, last;
        t = periodicty.next(t);
        System.out.println(reference + " -> " + t);

        for(int i = 0; i < iterations - 1; i++) {
            last = t;
            t = periodicty.next(t);

            assertThat(last, is(periodicty.previous(t)));
            assertThat(t, is(periodicty.next(last)));
            System.out.println(last + " -> " + t);
        }

        System.out.println(t);
    }
}
