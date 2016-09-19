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
package net.vleo.timel.executor;

import net.vleo.timel.time.Interval;

/**
 * An immutable object representing a constant value over a given time interval.
 *
 * @author Andrea Leofreddi
 */
public final class Sample<V> {
    /**
     * Static factory method to instance a new Sample.
     *
     * @param interval
     * @param value
     * @param <T>
     * @return The new sample object
     */
    public static <T> Sample<T> of(Interval interval, T value) {
        return new Sample<T>(interval, value);
    }

    private final Interval interval;

    private final V value;

    private Sample(Interval interval, V value) {
        this.interval = interval;

        this.value = value;
    }

    /**
     * Retrieve the interval of the sample.
     *
     * @return The interval
     */
    public Interval getInterval() {
        return interval;
    }

    /**
     * Retrieve the value of the sample.
     *
     * @return The value
     */
    public V getValue() {
        return value;
    }

    /**
     * Clone the current Sample into a new one with the same value but different interval.
     *
     * @param newInterval The interval of the new sample
     * @return The new sample
     */
    public Sample<V> copyWithInterval(Interval newInterval) {
        return new Sample<V>(
                newInterval,
                value
        );
    }

    /**
     * Clone the current Sample into a new one with the same interval but different value.
     *
     * @param newValue The value of the new sample
     * @return The new sample
     */
    public <T> Sample<T> copyWithValue(T newValue) {
        return new Sample<T>(
                getInterval(),
                newValue
        );
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Sample<?> sample = (Sample<?>) o;

        if(interval != null ? !interval.equals(sample.interval) : sample.interval != null) return false;
        return !(value != null ? !value.equals(sample.value) : sample.value != null);

    }

    @Override
    public int hashCode() {
        int result = interval != null ? interval.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "interval=" + interval +
                ", value=" + value +
                '}';
    }
}
