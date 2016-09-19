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
package net.vleo.timel;

import net.vleo.timel.compiler.CompilerBuilder;
import net.vleo.timel.compiler.Expression;
import net.vleo.timel.compiler.Program;
import net.vleo.timel.executor.ExpressionExecutorBuilder;
import net.vleo.timel.executor.ProgramExecutorBuilder;
import net.vleo.timel.impl.compiler.CompilerBuilderImpl;
import net.vleo.timel.impl.evaluate.ExpressionExecutorBuilderImpl;
import net.vleo.timel.impl.evaluate.ProgramExecutorBuilderImpl;

/**
 * Convenience facade to use TimEL. Use this class to compile and execute programs and expressions,
 * as per example below:
 *
 * <p><blockquote><pre>
 * {@code
 * long now = new Date().getTime();
 *
 * // Assemble the evaluation interval as [now, now + 1 second)
 * Interval interval = Interval.of(now, now + 1000L);
 *
 * // Compile the expression 1 + 1
 * Expression<Double> expression = TimEL
 *         .getCompiler("1 + 1")
 *         .compileExpression(Types.getNumericDoubleType());
 *
 * // Iterate through the results
 * TimeIterator<Double> itor = TimEL
 *         .getExecutor(expression)
 *         .evaluateFor(interval);
 *
 * // Since 1+1 is constant we'll have a single sample for the whole interval
 * double v = itor.next().getValue();
 * }
 * </pre></blockquote>
 *
 * @author Andrea Leofreddi
 */
public final class TimEL {
    private TimEL() {
        throw new AssertionError();
    }

    /**
     * Initiates the compilation of the given source. The method will return a compiler builder.
     *
     * @param source
     * @return The compiler builder
     */
    public static CompilerBuilder getCompiler(String source) {
        if(source == null)
            throw new NullPointerException("Null source provided for compilation");

        return new CompilerBuilderImpl(source);
    }

    /**
     * Initiates the evaluation process of an expression.
     *
     * @param expression
     * @param <V>
     * @return The evaluator builder
     */
    public static <V> ExpressionExecutorBuilder<V> getExecutor(Expression<V> expression) {
        if(expression == null)
            throw new NullPointerException("Null expression provided for evaluation");

        return new ExpressionExecutorBuilderImpl(expression);
    }

    /**
     * Initiates the evaluation process of a program.
     *
     * @param program The program to evaluate
     * @return The evaluator builder
     */
    public static ProgramExecutorBuilder getExecutor(Program program) {
        if(program == null)
            throw new NullPointerException("Null program provided for execution");

        return new ProgramExecutorBuilderImpl(program);
    }
}
