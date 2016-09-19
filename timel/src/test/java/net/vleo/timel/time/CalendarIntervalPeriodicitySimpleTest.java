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

import net.vleo.timel.impl.time.periodicity.CyclicPeriodicity;
import net.vleo.timel.impl.time.periodicity.Periodicity;
import org.joda.time.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Andrea Leofreddi
 */
public class CalendarIntervalPeriodicitySimpleTest {

    @Test
    public void f() {
        DateTimeZone dtz = DateTimeZone.forOffsetHours(1);

        DateTime y14 = new DateTime(2014, 1, 1, 0, 0).withZone(dtz),
            y15 = new DateTime(2015, 1, 1, 0, 0).withZone(dtz);

        DateTimeFieldType week = DateTimeFieldType.weekOfWeekyear();

        System.out.println(y14.property(week).setCopy(52).property(week).roundCeilingCopy());
        System.out.println(y15.property(week).setCopy(1).property(week).roundFloorCopy());
    }

    @Test
    public void test1() {
        DateTimeZone tz = DateTimeZone.forID("Europe/Rome");

        Periodicity p = new CyclicPeriodicity(CalendarField.WEEK_OF_YEAR, 2);

        if(true) {
            DateTime reference = IntervalTestHelper.parse("2014-12-01T00:00:00 Europe/Rome");

            reference = reference.withZone(DateTimeZone.forID("Europe/Rome"));

            DateTime t = reference, last;

            t = p.next(t);

            for(int i = 0; i < 0 * 400; i++) {
                last = t;

                t = p.next(t);

                if(!last.isEqual(p.previous(t))) {
                    System.out.println("MISMATCH");

                    p.next(last);

                    System.out.println("Last -> " + last);
                    System.out.println("Next -> " + t);
                    System.out.println("Prev(Next) -> " + p.previous(t));

                    Assert.assertEquals(last, p.previous(t));
                }

                System.out.println(t);
            }
        }
    }
    @Test
    public void test3() {
        DateTime reference = IntervalTestHelper.parse("2014-03-28T00:00:01 Europe/Rome");
        //ateTime.now().property(DateTimeFieldType.dayOfMonth()).roundFloorCopy();

        reference = reference.withZone(DateTimeZone.forID("Europe/Rome"));

        System.out.println("Reference " + reference);

        CyclicPeriodicity p = new CyclicPeriodicity(
            //CalendarField.WEEK_OF_YEAR, 2
            CalendarField.HOUR_OF_DAY, 12
        );

        DateTime t = reference, last;

        t = p.next(t);

        for(int i = 0; i < 800; i++) {
            last = t;

            t = p.next(t);

            if(!last.isEqual(p.previous(t)))
                Assert.assertEquals(last, p.previous(t));

            System.out.println(t);
//            System.out.println("Prev " + p.previous(t));
//            System.out.println("What " + t);
//            System.out.println("Next " + p.next(t));
//            System.out.println();
//
//            t = p.next(t);
        }
    }

    @Test
    public void test2() {
        DateTime reference = IntervalTestHelper.parse("2014-03-28T00:00:01 Europe/Rome");
        //ateTime.now().property(DateTimeFieldType.dayOfMonth()).roundFloorCopy();

        reference = reference.withZone(DateTimeZone.forID("Europe/Rome"));

        System.out.println("Reference " + reference);

        CyclicPeriodicity p = new CyclicPeriodicity(
            CalendarField.MONTH_OF_YEAR, 3
        );

        DateTime t = reference, last;

        t = p.next(t);

        for(int i = 0; i < 400; i++) {
            last = t;

            t = p.next(t);

            if(!last.isEqual(p.previous(t)))
                Assert.assertEquals(last, p.previous(t));

            System.out.println(t);
        }
    }
}
