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

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.time.Instant;

/**
 * An immutable class that represents an interval.
 *
 * @author Andrea Leofreddi
 */
@Value
@EqualsAndHashCode
public final class Interval implements Serializable {
    private final long start, end;

    private Interval(long start, long end) {
        if(start > end)
            throw new IllegalArgumentException("Interval end must be greater or equal to start");

        this.start = start;
        this.end = end;
    }

    /**
     * Retrieve an interval in between start and end instants.
     *
     * @param start Start instant
     * @param end   End instant
     * @return Interval [start, end)
     */
    public static Interval of(Instant start, Instant end) {
        return new Interval(start.toEpochMilli(), end.toEpochMilli());
    }

    /**
     * Retrieve an interval in between start and end expressed as millis from epoch.
     *
     * @param start Start time (epoch millis)
     * @param end   End time (epoch millis)
     * @return Interval [start, end)
     */
    public static Interval of(long start, long end) {
        return new Interval(start, end);
    }

    /**
     * Test if this interval contains the given instant.
     *
     * @param instant Instant (epoch millis)
     * @return Test result
     */
    public boolean contains(long instant) {
        return start <= instant && instant < end;
    }

    /**
     * Test if this interval contains the given interval.
     *
     * @param interval Interval to test
     * @return Test result
     */
    public boolean contains(Interval interval) {
        return start <= interval.getStart() && end >= interval.getEnd();
    }

    public boolean isAfter(long instant) {
        return start >= instant;
    }

    public boolean isAfter(Interval interval) {
        return start >= interval.getEnd();
    }

    public boolean isBefore(long instant) {
        return end <= instant;
    }

    public boolean isBefore(Interval interval) {
        return end <= interval.getStart();
    }

    public boolean overlaps(Interval interval) {
        return start < interval.end
                && interval.start < end;
    }

    public Interval overlap(Interval interval) {
        if(!overlaps(interval))
            throw new IllegalArgumentException("Provided intervals do not overlap: " + this + "-" + interval);

        return Interval.of(
                Math.max(start, interval.start),
                Math.min(end, interval.end)
        );
    }

    public long toDurationMillis() {
        return end - start;
    }

    @Override
    public String toString() {
        return "Interval{" +
                "start=" + new DateTime(start) +
                ", end=" + new DateTime(end) +
                '}';
    }
}
