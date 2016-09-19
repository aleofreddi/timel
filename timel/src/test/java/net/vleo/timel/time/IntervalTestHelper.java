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
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Miscellanea functions to support interval testing.
 *
 * @author Andrea Leofreddi
 */
public class IntervalTestHelper {
    private static final DateTimeFormatter[] DATE_TIME_FORMATS = {
        DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss ZZZ"),
        DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ"),
        //DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")
    };

    private IntervalTestHelper() {
        throw new AssertionError();
    }

    /**
     * Convenience method to parse dates in a convenient format (see DATE_TIME_FORMATS).
     *
     * @param t
     * @return
     */
    public static DateTime parse(String t) {
        for(DateTimeFormatter f : DATE_TIME_FORMATS) {
            try {
                return f.parseDateTime(t); //.toDateTime(DateTimeZone.UTC);
            } catch(IllegalArgumentException e) {
                // Do nothing: we try the next formatter in line
            }
        }

        throw new IllegalArgumentException("Cannot parse " + t);
    }

    /**
     * Convenience method to instance an Interval using date FORMAT.
     *
     * @param from
     * @param to
     * @return
     */
    public static Interval getInterval(String from, String to) {
        return new Interval(parse(from), parse(to));
    }
}
