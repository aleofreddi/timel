package net.vleo.timel.legacy.compiler;

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

import net.vleo.timel.Expression;
import net.vleo.timel.ParseException;
import net.vleo.timel.TimEL;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.Sample;
import net.vleo.timel.type.IntegerType;
import net.vleo.timel.variable.TreeMapVariable;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.TreeMap;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Basic tests for the Compiler class.
 *
 * @author Andrea Leofreddi
 */
class CompilerTest {
    @Test
    void shouldCompileAndEvaluateBasicTest() throws ParseException {
        Expression<?> expression = TimEL
                .parse("scale(scale(uniform(1), every(1, \"DAY_OF_YEAR\", \"UTC\")))")
                .compile();

        Interval interval = Interval.of(Instant.ofEpochMilli(0), Instant.ofEpochMilli(0).plus(1000, ChronoUnit.DAYS));

        Object value = TimEL.evaluate(expression, interval).next().getValue();

        assertThat(value, is(1000));
    }

    @Test
    void shouldCompileAndEvaluateSimpleTest() throws ParseException {
        // Assemble the evaluation interval as [now, now + 1 second)
        Instant now = Instant.now();
        Interval interval = Interval.of(now, now.plus(1, ChronoUnit.SECONDS));

        // Compile the expression 1 + 1.
        // As we expect an Integer here, we explicitly request an IntegerType
        // for the sake of type safety, but we can compile generic code as well.
        Expression<Integer> expression = TimEL
                .parse("1 + 1")
                .compile(new IntegerType());

        // Iterate through the results
        TimeIterator<Integer> itor = TimEL
                .evaluate(expression, interval);

        // Since 1+1 is constant we'll have a single sample for the whole interval
        int v = itor.next().getValue();

        assertEquals(2.0, v, 0);
    }

    @Test
    void shouldCompileAndEvaluateSimpleTestWithVariables() throws ParseException {
        // Create a new variable backed by a TreeMap
        TreeMapVariable<Integer> variable = new TreeMapVariable<>();
        TreeMap<Interval, Integer> values = variable.getTreeMap();

        // Add some values to the variable
        Instant now = Instant.now();
        for(int[] i : new int[][] {
                // start, end, value
                {0, 1, 1},
                {1, 2, 2},
                // gap between 2nd and 3rd minute
                {3, 4, 4}
        }) {
            values.put(Interval.of(
                    now.plus(i[0], ChronoUnit.MINUTES),
                    now.plus(i[1], ChronoUnit.MINUTES)
            ), i[2]);
        }

        // Compile "a * a" without type requirement
        Expression<?> expression = TimEL
                .parse("a * a")
                .withVariable("a", new IntegerType(), variable) // Declare 'a' as a int
                .compile();

        // Evaluate the expression in the interval [now, now + 5 minutes)
        TimeIterator<?> itor = TimEL
                .evaluate(expression, Interval.of(now, now.plus(5, ChronoUnit.MINUTES)));

        assertThat(itor.next(), is(
                Sample.of(
                        Interval.of(now.plus(0, ChronoUnit.MINUTES), now.plus(1, ChronoUnit.MINUTES)),
                        1
                )
        ));
        assertThat(itor.next(), is(
                Sample.of(
                        Interval.of(now.plus(1, ChronoUnit.MINUTES), now.plus(2, ChronoUnit.MINUTES)),
                        4
                )
        ));
        assertThat(itor.next(), is(
                Sample.of(
                        Interval.of(now.plus(3, ChronoUnit.MINUTES), now.plus(4, ChronoUnit.MINUTES)),
                        16
                )
        ));

        assertThat(itor.hasNext(), is(false));
    }
}

