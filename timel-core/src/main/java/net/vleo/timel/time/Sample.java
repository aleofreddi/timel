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
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * An immutable object representing a constant value over a given time interval.
 *
 * @author Andrea Leofreddi
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
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

    /**
     * Clone the current Sample into a new one with the same value but different interval.
     *
     * @param newInterval The interval of the new sample
     * @return The new sample
     */
    public Sample<V> copyWithInterval(Interval newInterval) {
        return new Sample<>(
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
        return new Sample<>(
                getInterval(),
                newValue
        );
    }

    @Override
    public String toString() {
        return "Sample{" +
                "interval=" + interval +
                ", value=" + value +
                '}';
    }
}
