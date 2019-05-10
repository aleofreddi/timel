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

import java.util.*;

/**
 * A calendar periodicity
 *
 * @author Andrea Leofreddi
 */
public class ListPeriodicity extends SimplePeriodicity {
    private CalendarField calendarField;

    public TreeSet<Integer> values;

    public ListPeriodicity(CalendarField calendarField, Collection<Integer> values) {
        this.calendarField = calendarField;

        this.values = new TreeSet<Integer>(values);
    }

    @Override
    public DateTime ceil(DateTime timestamp) {
        DateTimeFieldType field = CalendarFields.getFieldType(calendarField);

        DateTime t = timestamp.property(field).roundCeilingCopy();

        int cur = t.property(field).get();

        Integer n = values.ceiling(cur);

        if(n == null) {
            n = values.first();

            DateTimeFieldType parentFieldType = CalendarFields.getParentFieldType(calendarField);

            if(parentFieldType != null)
                t = t.property(parentFieldType).addToCopy(1);
        }

        return t.property(field).setCopy(n);
    }

    @Override
    public DateTime floor(DateTime timestamp) {
        DateTimeFieldType field = CalendarFields.getFieldType(calendarField);

        DateTime t = timestamp.property(field).roundFloorCopy();

        int cur = t.property(field).get();

        Integer n = values.floor(cur);

        if(n == null) {
            n = values.last();

            DateTimeFieldType parentFieldType = CalendarFields.getParentFieldType(calendarField);

            if(parentFieldType != null)
                t = t.property(parentFieldType).addToCopy(-1);
        }

        return t.property(field).setCopy(n);
    }
}
