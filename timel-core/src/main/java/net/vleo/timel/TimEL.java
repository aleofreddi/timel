package net.vleo.timel;

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

import net.vleo.timel.impl.CompilerBuilderImpl;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;

/**
 * Convenience facade to use TimEL. You can use this class to compile and evaluate expressions:
 *
 * <blockquote><pre>
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
    public static CompilerBuilder parse(String source) {
        if(source == null)
            throw new NullPointerException("Null source provided for compilation");
        return new CompilerBuilderImpl(source);
    }

    /**
     * Evaluate a compiled expression. As the evaluation is performed lazily, one needs to pull the returned iterator
     * to complete it. If more statements are provided, all but the last one are executed, and the last value iterator
     * is returned.
     *
     * @param expression The expression to evaluate
     * @return Result iterator
     */
    public static <V> TimeIterator<V> evaluate(Expression<V> expression, Interval interval) {
        ExpressionImpl<V> exprImpl = (ExpressionImpl<V>) expression;

        return (TimeIterator<V>) exprImpl.getTree().evaluate(interval, exprImpl.getExecutorContext());
    }
}
