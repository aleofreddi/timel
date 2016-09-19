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

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * An immutable class that represents an interval.
 *
 *
 *
 * @author Andrea Leofreddi
 */
public final class Interval implements Serializable {
    private final long start, end;

    private Interval(long start, long end) {
        if(start > end)
            throw new IllegalArgumentException("Interval end must be greater than start");

        this.start = start;
        this.end = end;
    }

    /**
     * Retrieve an interval in between start and end.
     *
     * @param start
     * @param end
     * @return
     */
    public static Interval of(long start, long end) {
        return new Interval(start, end);
    }

    /**
     * Test if this interval contains the given instant.
     *
     * @param instant
     * @return Test result
     */
    public boolean contains(long instant) {
        return start <= instant && instant < end;
    }

    /**
     * Test if this interval contains the given interval.
     *
     * @param interval
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

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public long toDurationMillis() {
        return end - start;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Interval interval = (Interval) o;

        if(start != interval.start) return false;
        return end == interval.end;

    }

    @Override
    public int hashCode() {
        int result = (int) (start ^ (start >>> 32));
        result = 31 * result + (int) (end ^ (end >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Interval{" +
                "start=" + new DateTime(start) +
                ", end=" + new DateTime(end) +
                '}';
    }
}
