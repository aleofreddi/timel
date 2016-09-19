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

import net.vleo.timel.time.PeriodField;
import org.joda.time.Period;

/**
 * Commodity functions for PeriodFields.
 *
 * @author Andrea Leofreddi
 */
public class PeriodFields {
    private PeriodFields() {
        throw new AssertionError();
    }

    public static Period periodOf(int size, PeriodField p) {
        Period period;

        switch(p) {
            case YEAR:
                period = new Period(size, 0, 0, 0, 0, 0, 0, 0);
                break;

            case MONTH:
                period = new Period(0, size, 0, 0, 0, 0, 0, 0);
                break;

            case WEEK:
                period = new Period(0, 0, size, 0, 0, 0, 0, 0);
                break;

            case DAY:
                period = new Period(0, 0, 0, size, 0, 0, 0, 0);
                break;

            case HOUR:
                period = new Period(0, 0, 0, 0, size, 0, 0, 0);
                break;

            case MINUTE:
                period = new Period(0, 0, 0, 0, 0, size, 0, 0);
                break;

            case SECOND:
                period = new Period(0, 0, 0, 0, 0, 0, size, 0);
                break;

            case MILLISECOND:
                period = new Period(0, 0, 0, 0, 0, 0, 0, size);
                break;

            default:
                throw new AssertionError();
        }

        return period.normalizedStandard();
    }
}
