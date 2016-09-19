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
package net.vleo.timel.impl.time;

import net.vleo.timel.time.CalendarField;
import org.joda.time.DateTimeFieldType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea Leofreddi
 */
public class CalendarFields {
    private static Map<CalendarField, DateTimeFieldType[]> calendarFieldToDateTimeFieldType;

    static {
        calendarFieldToDateTimeFieldType = new HashMap<CalendarField, DateTimeFieldType[]>();

        calendarFieldToDateTimeFieldType.put(CalendarField.SECOND_OF_MINUTE, new DateTimeFieldType[]{DateTimeFieldType.secondOfMinute(), DateTimeFieldType.minuteOfHour()});
        calendarFieldToDateTimeFieldType.put(CalendarField.SECOND_OF_DAY, new DateTimeFieldType[]{DateTimeFieldType.secondOfDay(), DateTimeFieldType.dayOfYear()});

        calendarFieldToDateTimeFieldType.put(CalendarField.MINUTE_OF_HOUR, new DateTimeFieldType[]{DateTimeFieldType.minuteOfHour(), DateTimeFieldType.hourOfDay()});
        calendarFieldToDateTimeFieldType.put(CalendarField.MINUTE_OF_DAY, new DateTimeFieldType[]{DateTimeFieldType.minuteOfDay(), DateTimeFieldType.dayOfYear()});

        calendarFieldToDateTimeFieldType.put(CalendarField.HOUR_OF_DAY, new DateTimeFieldType[]{DateTimeFieldType.hourOfDay(), DateTimeFieldType.dayOfMonth()});

        calendarFieldToDateTimeFieldType.put(CalendarField.DAY_OF_WEEK, new DateTimeFieldType[]{DateTimeFieldType.dayOfWeek(), DateTimeFieldType.weekOfWeekyear()});
        calendarFieldToDateTimeFieldType.put(CalendarField.DAY_OF_MONTH, new DateTimeFieldType[]{DateTimeFieldType.dayOfMonth(), DateTimeFieldType.monthOfYear()});
        calendarFieldToDateTimeFieldType.put(CalendarField.DAY_OF_YEAR, new DateTimeFieldType[]{DateTimeFieldType.dayOfYear(), DateTimeFieldType.year()});

        calendarFieldToDateTimeFieldType.put(CalendarField.WEEK_OF_YEAR, new DateTimeFieldType[]{DateTimeFieldType.weekOfWeekyear(), DateTimeFieldType.year()});

        calendarFieldToDateTimeFieldType.put(CalendarField.MONTH_OF_YEAR, new DateTimeFieldType[]{DateTimeFieldType.monthOfYear(), DateTimeFieldType.year()});

        calendarFieldToDateTimeFieldType.put(CalendarField.YEAR, new DateTimeFieldType[]{DateTimeFieldType.year(), null});
    }

    private CalendarFields() {
        throw new AssertionError();
    }

    public static DateTimeFieldType getFieldType(CalendarField calendarField) {
        DateTimeFieldType[] f = calendarFieldToDateTimeFieldType.get(calendarField);

        if(f == null)
            throw new AssertionError("Unexpected calendarField " + calendarField);

        return f[0];
    }

    public static DateTimeFieldType getParentFieldType(CalendarField calendarField) {
        DateTimeFieldType[] f = calendarFieldToDateTimeFieldType.get(calendarField);

        if(f == null)
            throw new AssertionError("Unexpected calendarField " + calendarField);

        return f[1];
    }
}
