package net.vleo.timel.function;

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

import net.vleo.timel.impl.function.conditional.CoalesceFunction;
import net.vleo.timel.impl.function.conditional.IfFunction;
import net.vleo.timel.impl.function.conditional.WhenFunction;
import net.vleo.timel.impl.function.eval.DefinedFunction;
import net.vleo.timel.impl.function.eval.ScaleFunction;
import net.vleo.timel.impl.function.eval.ScaleIntervalFunction;
import net.vleo.timel.impl.function.integral.UniformDoubleFunction;
import net.vleo.timel.impl.function.integral.UniformFloatFunction;
import net.vleo.timel.impl.function.integral.UniformIntegerFunction;
import net.vleo.timel.impl.function.interval.EveryFunction;
import net.vleo.timel.impl.function.math.RandFunction;
import net.vleo.timel.impl.function.time.ExtractFunction;
import net.vleo.timel.impl.function.version.VersionFunction;
import net.vleo.timel.impl.operator.arithmetic.*;
import net.vleo.timel.impl.operator.logical.LogicalAndOperator;
import net.vleo.timel.impl.operator.logical.LogicalNotOperator;
import net.vleo.timel.impl.operator.logical.LogicalOrOperator;
import net.vleo.timel.impl.operator.logical.LogicalXorOperator;
import net.vleo.timel.impl.operator.relational.*;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Standard functions.
 *
 * @author Andrea Leofreddi
 */
public final class StandardFunctions {
    public static final Set<Function<?>> STANDARD_FUNCTIONS = Stream.<Function<?>>of(
            // Conditional functions
            new CoalesceFunction(),
            new IfFunction(),
            new WhenFunction(),

            // Eval
            new DefinedFunction(),
            new ScaleFunction(),
            new ScaleIntervalFunction(),

            // Integral
            new UniformDoubleFunction(),
            new UniformFloatFunction(),
            new UniformIntegerFunction(),

            // Interval
            new EveryFunction(),

            // Math functions
            new RandFunction(),

            // *** Operators

            // Arithmetic
            new AddDoubleOperator(),
            new AddFloatOperator(),
            new AddIntegerOperator(),
            new AddIntegralDoubleOperator(),
            new AddIntegralFloatOperator(),
            new AddIntegralIntegerOperator(),

            new DivDoubleOperator(),
            new DivFloatOperator(),
            new DivIntegerOperator(),
            new DivIntegralFloatCombineOperator(),
            new DivIntegralFloatInverseOperator(),
            new DivIntegralFloatScaleOperator(),
            new DivIntegralDoubleCombineOperator(),
            new DivIntegralDoubleInverseOperator(),
            new DivIntegralDoubleScaleOperator(),
            new DivIntegralIntegerCombineOperator(),
            new DivIntegralIntegerInverseOperator(),
            new DivIntegralIntegerScaleOperator(),

            new MulFloatOperator(),
            new MulDoubleOperator(),
            new MulIntegerOperator(),
            new MulIntegralFloatCombineOperator(),
            new MulIntegralFloatScaleOperator(),
            new MulIntegralDoubleCombineOperator(),
            new MulIntegralDoubleScaleOperator(),
            new MulIntegralIntegerCombineOperator(),
            new MulIntegralIntegerScaleOperator(),

            new SubDoubleOperator(),
            new SubFloatOperator(),
            new SubIntegerOperator(),
            new SubIntegralFloatOperator(),
            new SubIntegralDoubleOperator(),
            new SubIntegralIntegerOperator(),

            new SubUnaryDoubleOperator(),
            new SubUnaryFloatOperator(),
            new SubUnaryIntegerOperator(),
            new SubUnaryIntegralFloatOperator(),
            new SubUnaryIntegralDoubleOperator(),
            new SubUnaryIntegralIntegerOperator(),

            // Logical
            new LogicalAndOperator(),
            new LogicalOrOperator(),
            new LogicalXorOperator(),
            new LogicalNotOperator(),

            // Relational
            new EqualOperator(),
            new InequalOperator(),
            new GreaterOperator(),
            new GreaterOrEqualOperator(),
            new LessOperator(),
            new LessOrEqualOperator(),

            // Time
            new ExtractFunction(),

            // Version
            new VersionFunction()
    ).collect(Collectors.toSet());

    private StandardFunctions() {
        throw new AssertionError();
    }
}
