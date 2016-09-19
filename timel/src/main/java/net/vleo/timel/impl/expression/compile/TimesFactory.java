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
package net.vleo.timel.impl.expression.compile;

import java.util.List;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.evaluate.operator.arithmetic.NumericTimesImpl;
import net.vleo.timel.impl.expression.evaluate.operator.arithmetic.PeriodScaleImpl;
import net.vleo.timel.type.*;

/**
 * Times operator factory.
 *
 * @author Andrea Leofreddi
 */
public class TimesFactory extends FunctionFactory {
    private static TimesFactory INSTANCE = new TimesFactory();

    public static final String TOKEN = "Times";

    public static final String SYMBOL = "*";

    public static TimesFactory get() {
        return INSTANCE;
    }

    private TimesFactory() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(id.equals(TOKEN)) {
            if(arguments.size() != 2)
                throw new ParseException("Invalid number of arguments for " + TOKEN + " operation");

            TreeNode t = arguments.get(0), u = arguments.get(1);

            if(Types.getNumericDoubleType().isAssignableFrom(t.getType()) && Types.getNumericDoubleType().isAssignableFrom(u.getType())) {
                return new NumericTimesImpl(
                        Types.getNumericDoubleType(),
                        (ValueNode<Double>) t,
                        (ValueNode<Double>) u
                );

            } else if(Types.getNumericDoubleType().isAssignableFrom(t.getType()) && Types.getPeriodType().isAssignableFrom(u.getType())) {
                return new PeriodScaleImpl(
                        (ValueNode<String>) u,
                        (ValueNode<Double>) t
                );

            } else if(Types.getPeriodType().isAssignableFrom(t.getType()) && Types.getNumericDoubleType().isAssignableFrom(u.getType())) {
                return new PeriodScaleImpl(
                        (ValueNode<String>) t,
                        (ValueNode<Double>) u
                );

            }

            /// INTEGRAL STUFF

            else if(Types.getIntegralDoubleType().isAssignableFrom(t.getType()) && Types.getNumericDoubleType().isAssignableFrom(u.getType())) {
                // INTEGRAL-AVERAGE
                IntegralType<?> t1 = (IntegralType<?>) t.getType();

                return new NumericTimesImpl(
                        Types.getIntegralDoubleType(t1.getDegree()),
                        (ValueNode<Double>) t,
                        (ValueNode<Double>) u
                );

            } else if(Types.getNumericDoubleType().isAssignableFrom(t.getType()) && Types.getIntegralDoubleType().isAssignableFrom(u.getType())) {
                // AVERAGE-INTEGRAL
                IntegralType<?> u1 = (IntegralType<?>) u.getType();

                return new NumericTimesImpl(
                        Types.getIntegralDoubleType(u1.getDegree()),
                        (ValueNode<Double>) t,
                        (ValueNode<Double>) u
                );

            } else if(Types.getIntegralDoubleType().isAssignableFrom(t.getType()) && Types.getIntegralDoubleType().isAssignableFrom(u.getType())) {
                // INTEGRAL-INTEGRAL
                IntegralType<?> t1 = (IntegralType<?>) t.getType();
                IntegralType<?> u1 = (IntegralType<?>) u.getType();

                int resultDegree = t1.getDegree() + u1.getDegree();

                ValueType<Double> outputType = resultDegree == 0 ?
                        Types.getNumericDoubleType()
                        : Types.getIntegralDoubleType(resultDegree);

                return new NumericTimesImpl(
                        outputType,
                        (ValueNode<Double>) t,
                        (ValueNode<Double>) u
                );

            } else {
                throw new ParseException("Unsupported times operation for (" + t.getType() + ", " + u.getType() + ")");
            }
        }

        return null;
    }
}
