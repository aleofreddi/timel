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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * A wrapper around Periodicity to enforce a particular timezone.
 *
 * @author Andrea Leofreddi
 */
public class TimeZoneWrappedPeriodicity implements Periodicity {
    private Periodicity periodicity;

    private DateTimeZone dateTimeZone;

    public TimeZoneWrappedPeriodicity(Periodicity periodicity, DateTimeZone dateTimeZone) {
        this.periodicity = periodicity;
        this.dateTimeZone = dateTimeZone;

        if(dateTimeZone == null)
            throw new NullPointerException("Null timezone provided");
    }

    private DateTime convert(DateTime timestamp) {
        return timestamp.withZone(dateTimeZone);
    }

    @Override
    public DateTime ceil(DateTime timestamp) {
        return periodicity.ceil(convert(timestamp));
    }

    @Override
    public DateTime floor(DateTime timestamp) {
        return periodicity.floor(convert(timestamp));
    }

    @Override
    public DateTime next(DateTime timestamp) {
        return periodicity.next(convert(timestamp));
    }

    @Override
    public DateTime previous(DateTime timestamp) {
        return periodicity.previous(convert(timestamp));
    }

    @Override
    public boolean matches(DateTime timestamp) {
        return periodicity.matches(convert(timestamp));
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;

        TimeZoneWrappedPeriodicity that = (TimeZoneWrappedPeriodicity) o;

        if(periodicity != null ? !periodicity.equals(that.periodicity) : that.periodicity != null)
            return false;
        return !(dateTimeZone != null ? !dateTimeZone.equals(that.dateTimeZone) : that.dateTimeZone != null);

    }

    @Override
    public int hashCode() {
        int result = periodicity != null ? periodicity.hashCode() : 0;
        result = 31 * result + (dateTimeZone != null ? dateTimeZone.hashCode() : 0);
        return result;
    }
}
