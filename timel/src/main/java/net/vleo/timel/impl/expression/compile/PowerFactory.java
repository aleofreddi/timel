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

import java.util.Date;
import java.util.List;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.executor.ExecutionException;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.evaluate.operator.arithmetic.PowerImpl;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.*;

/**
 * Power operator factory.
 *
 * @author Andrea Leofreddi
 */
public class PowerFactory extends FunctionFactory {
    public static final String TOKEN = "Power";

    public static final String SYMBOL = "^";

    private int checkExponent(ValueNode<Double> expNode) throws ParseException {
        if(!expNode.isConstant())
            throw new ParseException("Power exponent should be constant");

        double value;

        try {
            long now = new Date().getTime();

            if(!expNode.isConstant())
                throw new ParseException(TOKEN + ": exponent must be constant");

            value = expNode.evaluate(Interval.of(now, now + 1), null).next().getValue();
        } catch(ExecutionException e) {
            throw new ParseException("Unable to evaluate power exponent " + expNode.toCanonicalExpression(), e);
        }

        if(value != Math.floor(value))
            throw new ParseException("Only integer exponents are supported");

        if(value < -1.0)
            throw new ParseException("Cannot exponentiate with an exponent less than -1");

        return (int) value;
    }

    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(id.equals(TOKEN)) {
            if(arguments.size() != 2)
                throw new ParseException("Invalid number of arguments for " + TOKEN + " operator");

            TreeNode t = arguments.get(0), u = arguments.get(1);

            if(Types.getNumericType().isAssignableFrom(t.getType()) && Types.getNumericType().isAssignableFrom(u.getType())) {
                checkExponent((ValueNode<Double>) u);

                return new PowerImpl(
                        Types.getNumericDoubleType(),
                        (ValueNode<Double>) t,
                        (ValueNode<Double>) u
                );
            } else if(Types.getIntegralDoubleType().isAssignableFrom(t.getType()) && Types.getNumericType().isAssignableFrom(u.getType())) {
                int exp = checkExponent((ValueNode<Double>) u);

                int baseDegree = ((IntegralType<?>) t.getType()).getDegree();

                int resultDegree = baseDegree * exp;

                ValueType<Double> outputType;

                if(exp == 0 || resultDegree == 0)
                    outputType = Types.getNumericDoubleType();
                else
                    outputType = Types.getIntegralDoubleType(resultDegree);

                return new PowerImpl(
                        outputType,
                        (ValueNode<Double>) t,
                        (ValueNode<Double>) u
                );
            }

            throw new IllegalArgumentException("Unsupported power operation for (" + t + ", " + u + ")");
        }

        return null;
    }
}
