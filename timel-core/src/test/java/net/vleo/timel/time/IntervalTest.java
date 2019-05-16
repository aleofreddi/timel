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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Leofreddi
 */
class IntervalTest {
    private final long DAY = 86_400_000;

    @Test
    void shouldThrowIllegalArgumentExceptionWhenStartIsGreaterThanEnd() {
        assertThrows(IllegalArgumentException.class, () -> Interval.of(1, 0));
    }

    @ParameterizedTest
    @CsvSource({
            "1,3,1,3,true",
            "1,3,2,3,true",
            "1,3,1,2,true",
            "1,3,2,2,true",
            "1,3,0,3,false",
            "1,3,1,4,false",
            "1,3,0,4,false",
            "1,3,0,1,false",
            "1,3,3,4,false"
    })
    void shouldContainInterval(int tFrom, int tEnd, int uFrom, int uEnd, boolean expected) {
        Interval t = Interval.of(tFrom * DAY, tEnd * DAY),
                u = Interval.of(uFrom * DAY, uEnd * DAY);

        assertThat(t.contains(u), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "1,1,1,false",

            "1,3,0,false",
            "1,3,1,true",
            "1,3,2,true",
            "1,3,3,false"
    })
    void shouldContainInstant(int tFrom, int tEnd, int u, boolean expected) {
        Interval t = Interval.of(tFrom * DAY, tEnd * DAY);

        assertThat(t.contains(u * DAY), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "1,1,0,true",
            "1,1,1,true",
            "1,1,2,false",

            "1,3,0,true",
            "1,3,1,true",
            "1,3,2,false",
            "1,3,3,false",
            "1,3,4,false",
    })
    void shouldCheckIsAfterInstant(int tFrom, int tEnd, int u, boolean expected) {
        Interval t = Interval.of(tFrom * DAY, tEnd * DAY);

        assertThat(t.isAfter(u * DAY), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "1,1,0,0,true",
            "1,1,1,1,true",
            "1,1,2,2,false",

            "1,3,0,1,true",
            "1,3,1,1,true",
            "1,3,1,2,false",
            "1,3,2,3,false",
            "1,3,3,4,false",
            "1,3,0,4,false"
    })
    void shouldCheckIsAfterInterval(int tFrom, int tEnd, int uFrom, int uEnd, boolean expected) {
        Interval t = Interval.of(tFrom * DAY, tEnd * DAY),
                u = Interval.of(uFrom * DAY, uEnd * DAY);

        assertThat(t.isAfter(u), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "1,1,0,false",
            "1,1,1,true",
            "1,1,2,true",

            "1,3,0,false",
            "1,3,1,false",
            "1,3,2,false",
            "1,3,3,true",
            "1,3,4,true"
    })
    void shouldCheckIsBeforeInstant(int tFrom, int tEnd, int u, boolean expected) {
        Interval t = Interval.of(tFrom * DAY, tEnd * DAY);

        assertThat(t.isBefore(u * DAY), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "1,1,0,0,false",
            "1,1,1,1,true",
            "1,1,2,2,true",

            "1,3,0,1,false",
            "1,3,1,1,false",
            "1,3,1,2,false",
            "1,3,2,3,false",
            "1,3,3,3,true",
            "1,3,3,4,true",
            "1,3,0,4,false"
    })
    void shouldCheckIsBeforeInterval(int tFrom, int tEnd, int uFrom, int uEnd, boolean expected) {
        Interval t = Interval.of(tFrom * DAY, tEnd * DAY),
                u = Interval.of(uFrom * DAY, uEnd * DAY);

        assertThat(t.isBefore(u), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "1,1,1,1,false",
            "1,3,1,3,true",
            "1,3,2,3,true",
            "1,3,1,2,true",
            "1,3,2,2,true",
            "1,3,0,3,true",
            "1,3,1,4,true",
            "1,3,0,4,true",
            "1,3,0,1,false",
            "1,3,3,4,false"
    })
    void shouldOverlapInterval(int tFrom, int tEnd, int uFrom, int uEnd, boolean expected) {
        Interval t = Interval.of(tFrom * DAY, tEnd * DAY),
                u = Interval.of(uFrom * DAY, uEnd * DAY);

        assertThat(t.overlaps(u), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "1,3,1,3,1,3",
            "1,3,2,3,2,3",
            "1,3,1,2,1,2",
            "1,3,2,2,2,2",
            "1,3,0,3,1,3",
            "1,3,1,4,1,3",
            "1,3,0,4,1,3"
    })
    void shouldOverlapInterval(int tFrom, int tEnd, int uFrom, int uEnd, int eFrom, int eEnd) {
        Interval t = Interval.of(tFrom * DAY, tEnd * DAY),
                u = Interval.of(uFrom * DAY, uEnd * DAY),
                expected = Interval.of(eFrom * DAY, eEnd * DAY);

        assertThat(t.overlap(u), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "1,1,1,1",
            "1,3,0,1",
            "1,3,3,4"
    })
    void shouldThrowIllegalArgumentExceptionWhenInvalidOverlap(int tFrom, int tEnd, int uFrom, int uEnd) {
        Interval t = Interval.of(tFrom * DAY, tEnd * DAY),
                u = Interval.of(uFrom * DAY, uEnd * DAY);

        assertThrows(IllegalArgumentException.class, () -> t.overlap(u));
    }

    @ParameterizedTest
    @CsvSource({
            "1,1,0",
            "1,3,2",
            "0,4,4",
    })
    void shouldComputeDuration(int tFrom, int tEnd, int expected) {
        Interval t = Interval.of(tFrom * DAY, tEnd * DAY);

        assertThat(t.toDurationMillis(), is(expected * DAY));
    }
}
