package net.vleo.timel.console;

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

import lombok.val;
import net.vleo.timel.ParseException;
import net.vleo.timel.conversion.StandardConversions;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.function.FunctionRegistry;
import net.vleo.timel.function.StandardFunctions;
import net.vleo.timel.impl.executor.ExecutorContextImpl;
import net.vleo.timel.impl.function.interval.EveryFunction;
import net.vleo.timel.impl.intermediate.SyntaxTreeAdapter;
import net.vleo.timel.impl.intermediate.SyntaxTreeDumper;
import net.vleo.timel.impl.parser.Parser;
import net.vleo.timel.impl.parser.ParserTreeDumper;
import net.vleo.timel.impl.target.TargetTreeAdapter;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.Sample;
import net.vleo.timel.type.TypeSystem;
import net.vleo.timel.variable.TreeMapVariable;
import net.vleo.timel.variable.VariableRegistry;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 * A runnable console usable to do sample tests.
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

        final TypeSystem TYPE_SYSTEM = new TypeSystem(StandardConversions.STANDARD_CONVERSIONS, Collections.emptySet());

        while(true) {
            try {
                System.out.print(">> ");
                System.out.flush();
                String line = br.readLine();

                if(line.trim().isEmpty())
                    continue;

                System.out.println("Parsing " + line);

                val parseTree = new Parser().parse(line);

                System.out.println("Parsed {\n" + new ParserTreeDumper().dump(parseTree) + "}\n");

                FunctionRegistry functionRegistry = new FunctionRegistry(TYPE_SYSTEM);

                functionRegistry.addAll(StandardFunctions.STANDARD_FUNCTIONS);

                VariableRegistry variableRegistry = new VariableRegistry();
                variableRegistry.setVariableFactory((variable, type) -> new TreeMapVariable<>());

                // Time
                functionRegistry.add(new EveryFunction());

                val intTree = parseTree.accept(new SyntaxTreeAdapter(variableRegistry, functionRegistry));

                System.out.println("Parsed {\n" + new SyntaxTreeDumper().dump(intTree) + "}\n");

                val targetTree = intTree.accept(new TargetTreeAdapter());

                System.out.println("T> " + targetTree);

                System.out.println("Expression type is " + targetTree.getReference().getType());

                // Evaluate the expression
                Interval evalInterval = Interval.of(start.getMillis(), stop.getMillis());
                System.out.println("Evaluating expression for " + evalInterval);
                TimeIterator<?> iter = targetTree.evaluate(evalInterval, new ExecutorContextImpl(null));

                while(iter.hasNext()) {
                    Sample<?> sample = iter.next();

                    System.out.println("\t" + sample.getInterval() + " => " + sample.getValue());
                }

                System.out.println("Evaluation complete");
            } catch(ParseException e) {
                System.err.println(e.getMessage() + " at " + e.getSourceReference());
                e.printStackTrace();
            } catch(Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
