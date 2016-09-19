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
package net.vleo.timel.impl.time.periodicity;

import net.vleo.timel.executor.Sample;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.iterator.BufferedTimeIterator;
import net.vleo.timel.time.Interval;
import org.joda.time.DateTime;

/**
 * A wrapper around Periodicity to implement an IntervalPeriodicity.
 *
 * @author Andrea Leofreddi
 */
public class IntervalPeriodicityWrapper implements IntervalPeriodicity {
    private Periodicity periodicity;

    public IntervalPeriodicityWrapper(Periodicity periodicity) {
        this.periodicity = periodicity;
    }

    @Override
    public Periodicity getPeriodicity() {
        return periodicity;
    }

    @Override
    public Interval supremum(final long instant) {
        final DateTime start = periodicity.floor(new DateTime(instant));

        return Interval.of(
                start.getMillis(),
                periodicity.next(start).getMillis()
        );
    }

    @Override
    public TimeIterator<Void> supremumIter(final Interval interval) {
        final DateTime start = periodicity.floor(new DateTime(interval.getStart())),
                end = new DateTime(interval.getEnd());

        return new BufferedTimeIterator<Void>() {
            DateTime t = start, u;

            @Override
            protected Sample<Void> concreteNext() {
                if(!t.isBefore(end))
                    return null;

                u = periodicity.next(t);

                Interval result = Interval.of(t.getMillis(), u.getMillis());

                t = u;

                return Sample.of(
                        result,
                        (Void) null
                );
            }
        };
    }

    @Override
    public TimeIterator<Void> intersectionIter(Interval interval) {
        final DateTime start = new DateTime(interval.getStart()),
                end = new DateTime(interval.getEnd());

        return new BufferedTimeIterator<Void>() {
            DateTime t = start, u;

            @Override
            protected Sample<Void> concreteNext() {
                if(!t.isBefore(end))
                    return null;

                u = periodicity.next(t);

                Interval result;

                if(u.isAfter(end))
                    result = Interval.of(t.getMillis(), end.getMillis());
                else
                    result = Interval.of(t.getMillis(), u.getMillis());

                t = u;

                return Sample.of(
                        result,
                        null
                );
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;

        IntervalPeriodicityWrapper that = (IntervalPeriodicityWrapper) o;

        return !(periodicity != null ? !periodicity.equals(that.periodicity) : that.periodicity != null);

    }

    @Override
    public int hashCode() {
        return periodicity != null ? periodicity.hashCode() : 0;
    }
}
