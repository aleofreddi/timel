package net.vleo.timel.impl.time.periodicity;

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

import net.vleo.timel.impl.time.CalendarFields;
import net.vleo.timel.time.CalendarField;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

/**
 * A cyclic periodicity
 *
 * @author Andrea Leofreddi
 */
public class CyclicPeriodicity extends SimplePeriodicity {
    private CalendarField calendarField;

    private int size;

    public CyclicPeriodicity(CalendarField calendarField, int size) {
        this.calendarField = calendarField;
        this.size = size;
    }

    @Override
    public DateTime ceil(DateTime timestamp) {
        DateTimeFieldType field = CalendarFields.getFieldType(calendarField);

        DateTime t = timestamp.property(field).roundCeilingCopy();

        int min = t.property(field).getMinimumValue();

        int cur = t.property(field).get() - min;

        int n = min + ((cur % size) == 0 ? cur : cur - (cur % size) + size);

        if(n > t.property(field).getMaximumValue()) {
            n = t.property(field).getMinimumValue();

            DateTimeFieldType parentFieldType = CalendarFields.getParentFieldType(calendarField);

            if(parentFieldType != null)
                t = t.property(parentFieldType).addToCopy(1);
        }

        return t.property(field).setCopy(n).property(field).roundFloorCopy();
    }

    @Override
    public DateTime floor(DateTime timestamp) {
        DateTimeFieldType field = CalendarFields.getFieldType(calendarField);

        DateTime t = timestamp.property(field).roundFloorCopy();

        int min = t.property(field).getMinimumValue();

        int cur = t.property(field).get() - min;

        int n = min + ((cur % size) == 0 ? cur : cur - (cur % size));

        if(n < t.property(field).getMinimumValue()) {
            n = t.property(field).getMaximumValue();

            DateTimeFieldType parentFieldType = CalendarFields.getParentFieldType(calendarField);

            if(parentFieldType != null)
                t = t.property(parentFieldType).addToCopy(-1);
        }

        return t.property(field).setCopy(n).property(field).roundCeilingCopy();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;

        CyclicPeriodicity that = (CyclicPeriodicity) o;

        if(size != that.size)
            return false;
        return calendarField == that.calendarField;
    }

    @Override
    public int hashCode() {
        int result = calendarField != null ? calendarField.hashCode() : 0;
        result = 31 * result + size;
        return result;
    }
}
