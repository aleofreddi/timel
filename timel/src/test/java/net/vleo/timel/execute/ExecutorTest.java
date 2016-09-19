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
package net.vleo.timel.execute;

import junit.framework.TestCase;
import net.vleo.timel.TimEL;
import net.vleo.timel.compiler.Expression;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.impl.expression.evaluate.variable.TreeMapVariable;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.NumericDoubleType;
import net.vleo.timel.type.NumericType;
import net.vleo.timel.type.Types;
import net.vleo.timel.type.ValueType;
import org.joda.time.DateTime;

import java.util.Map;

/**
 * Basic tests for the Compiler class.
 *
 * @author Andrea Leofreddi
 */
public class ExecutorTest extends TestCase {
    public void test() throws ParseException {
        DateTime now = new DateTime();

        TreeMapVariable<Double> varA = new TreeMapVariable<Double>(),
                varB = new TreeMapVariable<Double>();

        Interval i = Interval.of(now.getMillis(), now.plusSeconds(1000).getMillis());

        varB.getBackMap().put(i, 3.0);

        Expression<Double> expression = TimEL
                .getCompiler("A=1+B")
                .declareVariable("B", Types.getNumericType())
                .compileExpression(Types.getNumericDoubleType());

        TimeIterator<Double> itor = TimEL
                .getExecutor(expression)
                .bindVariable("A", varA)
                .bindVariable("B", varB)
                //.autoBind(TreeMapVariable.getFactory())
                .evaluateFor(i);

        System.out.println("Starting evaluation...");

        while(itor.hasNext()) {
            System.out.println(itor.next());
        }

        System.out.println("Done!");

        itor = TimEL
                .getExecutor(expression)
                .bindVariable("A", varA)
                .bindVariable("B", varB)
                //.autoBind(TreeMapVariable.getFactory())
                .evaluateFor(i);

        System.out.println("Starting evaluation...");

        while(itor.hasNext()) {
            System.out.println(itor.next());
        }

        System.out.println("Done!");

        System.out.printf("Variable A raw dump:");

        for(Map.Entry<Interval, Double> entry : varA.getBackMap().entrySet()) {
            System.out.printf("---> " + entry.getKey() + " -> " + entry.getValue());
        }
    }
}
