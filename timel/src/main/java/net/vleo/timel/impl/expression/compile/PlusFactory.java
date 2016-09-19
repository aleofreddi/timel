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

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.evaluate.operator.arithmetic.TimePlusPeriodImpl;
import net.vleo.timel.impl.expression.evaluate.operator.arithmetic.NumericPlusImpl;
import net.vleo.timel.type.*;

import java.util.List;

/**
 * Plus operator factory.
 *
 * @author Andrea Leofreddi
 */
public class PlusFactory extends FunctionFactory {
    public static final String TOKEN = "Plus";

    public static final String SYMBOL = "+";

    @SuppressWarnings("unchecked")
    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(id.equals(TOKEN)) {
            if(arguments.size() == 2) {
                TreeNode t = arguments.get(0), u = arguments.get(1);

                if(Types.getNumericType().isAssignableFrom(t.getType()) && Types.getNumericType().isAssignableFrom(u.getType())) {
                    // AVERAGE-AVERAGE
                    return new NumericPlusImpl(
                            Types.getNumericDoubleType(),
                            (ValueNode<Double>) t,
                            (ValueNode<Double>) u
                    );

                } else if(Types.getIntegralDoubleType().isAssignableFrom(t.getType()) && Types.getIntegralDoubleType().isAssignableFrom(u.getType())) {
                    // INTEGRAL-INTEGRAL
                    return new NumericPlusImpl(
                            Types.getIntegralDoubleType(1),
                            (ValueNode<Double>) t,
                            (ValueNode<Double>) u
                    );

                } else if(Types.getTimeType().isAssignableFrom(t.getType()) && Types.getPeriodType().isAssignableFrom(u.getType())) {
                    // TIME-PERIOD
                    return new TimePlusPeriodImpl(
                            (ValueNode<Long>) t,
                            (ValueNode<String>) u
                    );
                }

                throw new ParseException("Unsupported plus operation for (" + t.getType() + ", " + u.getType() + ")");
            } else if(arguments.size() == 3) {
                TreeNode t = arguments.get(0), u = arguments.get(1), v = arguments.get(2);

                if(Types.getTimeType().isAssignableFrom(t.getType()) && Types.getPeriodType().isAssignableFrom(u.getType()) && Types.getStringType().isAssignableFrom(v.getType())) {
                    // TIME-PERIOD w tz
                    return new TimePlusPeriodImpl(
                            (ValueNode<Long>) t,
                            (ValueNode<String>) u,
                            (ValueNode<String>) v
                    );
                }

                throw new ParseException("Unsupported plus operation for (" + t.getType() + ", " + u.getType() + ", " + v.getType() + ")");
            } else
                throw new ParseException("Invalid number of arguments for " + TOKEN + " operation");
        }

        return null;
    }
}
