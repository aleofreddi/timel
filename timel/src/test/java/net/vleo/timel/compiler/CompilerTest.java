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
package net.vleo.timel.compiler;

import net.vleo.timel.TimEL;
import net.vleo.timel.impl.expression.evaluate.variable.TreeMapVariable;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.NumericDoubleType;
import net.vleo.timel.type.NumericType;
import net.vleo.timel.type.Types;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Basic tests for the Compiler class.
 *
 * @author Andrea Leofreddi
 */
public class CompilerTest {
    @Test
    public void testBasic() throws ParseException {
        long now = new Date().getTime();

        // Assemble the evaluation interval as [now, now + 1 second)
        Interval interval = Interval.of(now, now + 1000L);

        // Compile the expression 1 + 1
        Expression<Double> expression = TimEL
                .getCompiler("1 + 1")
                .compileExpression(Types.getNumericDoubleType());

        // Iterate through the results
        TimeIterator<Double> itor = TimEL
                .getExecutor(expression)
                .evaluateFor(interval);

        // Since 1+1 is constant we'll have a single sample for the whole interval
        double v = itor.next().getValue();

        Assert.assertEquals(2.0, v, 0);
    }

    @Test
    public void testBasic2() throws ParseException {
        TreeMapVariable<Double> variable = new TreeMapVariable<Double>();

        long now = new Date().getTime();

        // Retrieve the map backing the TreeMapVariable
        TreeMap<Interval, Double> values = variable.getBackMap();

        // Add some values to the variable
        values.put(Interval.of(now, now + 1000), 1.0); // [now, now + 1s) = 1.0
        values.put(Interval.of(now + 1000, now + 2000), 2.0); // [now + 1s, now + 2s) = 2.0
        values.put(Interval.of(now + 2000, now + 3000), 3.0); // [now + 2s, now + 3s) = 3.0

        // Compile the A * A expression
        Expression<Double> expression = TimEL
                .getCompiler("A * A")
                .declareVariable("A", Types.getNumericDoubleType()) // Declare A as a numeric type
                .compileExpression(Types.getNumericDoubleType()); // Expect a numeric result

        // Evaluate the expression in the interval [now, now + 3 seconds)
        TimeIterator<Double> itor = TimEL
                .getExecutor(expression)
                .bindVariable("A", variable)
                .evaluateFor(Interval.of(now, now + 3000));

        System.out.println("Starting evaluation...");

        while(itor.hasNext())
            System.out.println(itor.next());
    }
}

