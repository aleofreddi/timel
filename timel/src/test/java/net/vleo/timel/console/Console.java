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
package net.vleo.timel.console;

import net.vleo.timel.TimEL;
import net.vleo.timel.compiler.Expression;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.Types;
import net.vleo.timel.type.ValueType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * A console usable to do sample tests.
 *
 * @author Andrea Leofreddi
 */
public class Console {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        DateTimeZone timeZone = DateTimeZone.UTC;
                DateTimeZone.forID("Europe/Rome");

        DateTime start = new DateTime(2015, 1, 15, 0, 0, 0, 0, timeZone);

        DateTime stop = new DateTime(2016, 2, 14, 0, 0, 0, 0, timeZone);

        try {
            StringBuilder sb = new StringBuilder();

            for(String line; (line = br.readLine()) != null; ) {
                System.out.println(">> " + line);

                sb.append(line);
            }

            Interval evalInterval = Interval.of(start.getMillis(), stop.getMillis());

            System.out.println("Compiling expression...");

            // Compile the expression
            Expression<?> expression = TimEL
                    .getCompiler(sb.toString())
                    .compileExpression(Types.getValueType());

            System.out.println("Executing expression...");

            // Evaluation the expression
            TimeIterator<?> iter = TimEL
                    .getExecutor(expression)
                    .evaluateFor(evalInterval);

            while(iter.hasNext()) {
                Sample<?> sample = iter.next();

                System.out.println(sample.getInterval() + " => " + sample.getValue());
            }

            System.out.println("Evaluation complete");
        } catch(Exception e) {
            System.out.println("Unexpected exception");

            e.printStackTrace();
        }
    }
}
