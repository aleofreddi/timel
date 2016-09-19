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
package net.vleo.timel.impl.expression.evaluate.function.eval;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.FunctionArgumentParser;
import net.vleo.timel.impl.expression.utils.Predicate;
import net.vleo.timel.impl.expression.utils.Predicates;
import net.vleo.timel.impl.type.Utils;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for evaluation-related functions.
 *
 * @author Andrea Leofreddi
 */
public class EvalFactory extends FunctionFactory {
    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(EvalImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(EvalImpl.TOKEN, arguments);

            ValueNode<Double> valueNode = parser.next().withType(Utils.isNumericPredicate()).chop();

            ValueNode<Interval> intervalNode = parser.next().withType(Utils.isIntervalPredicate()).last().chop();

            return new EvalImpl(
                    valueNode,
                    intervalNode
            );

        } else if(ShiftImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(ShiftImpl.TOKEN, arguments);

            ValueNode<Object> valueNode = parser.next().withType(Utils.isValuePredicate()).chop();

            ValueNode<String> periodNode = parser.next().withType(Utils.isPeriodPredicate()).last().chop();

            return new ShiftImpl<Object>(
                    valueNode,
                    periodNode
            );

        } else if(CoalesceImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(EvalImpl.TOKEN, arguments);

            Predicate<Type> isSame = Utils.isSamePredicate();

            List<ValueNode<Object>> argNodes = new ArrayList<ValueNode<Object>>(arguments.size());

            do {
                ValueNode<Object> valueNode = parser.next().withType(isSame).chop();

                argNodes.add(valueNode);
            } while(parser.hasNext());

            return new CoalesceImpl<Object>(argNodes);

        } else if(PreviousStop.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(PreviousValue.TOKEN, arguments);

            ValueNode<Object> valueNode = parser.next().withType(
                    Predicates.and(
                            Utils.isValuePredicate(),
                            Predicates.not(
                                    Utils.isNumericIntegralPredicate()
                            )
                    )
            ).chop();

            ValueNode<Long> beforeNode = parser.next().withType(Utils.isDateTimePredicate()).last().chop();

            return new PreviousStop(valueNode, beforeNode);

        } else if(PreviousValue.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(PreviousValue.TOKEN, arguments);

            ValueNode<Object> valueNode = parser.next().withType(
                    Predicates.and(
                            Utils.isValuePredicate(),
                            Predicates.not(
                                    Utils.isNumericIntegralPredicate()
                            )
                    )
            ).chop();

            ValueNode<Long> beforeNode = parser.next().withType(Utils.isDateTimePredicate()).last().chop();

            return new PreviousValue<Object>(valueNode, beforeNode);

        }

        return null;
    }
}
