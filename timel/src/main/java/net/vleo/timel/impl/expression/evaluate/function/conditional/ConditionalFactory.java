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
package net.vleo.timel.impl.expression.evaluate.function.conditional;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.FunctionArgumentParser;
import net.vleo.timel.impl.type.Utils;
import net.vleo.timel.type.Type;
import net.vleo.timel.type.Types;
import net.vleo.timel.type.ValueType;
import net.vleo.timel.impl.expression.utils.Predicate;
import net.vleo.timel.impl.expression.utils.Predicates;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for conditionals.
 *
 * @author Andrea Leofreddi
 */
public class ConditionalFactory extends FunctionFactory {
    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(IfImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(IfImpl.TOKEN, arguments);

            ValueNode<Double> condition = parser.next().withType(Utils.isNumericAveragePredicate()).chop();

            Predicate<Type> valuePred = Predicates.and(
                    Utils.isValuePredicate(),
                    Utils.isSamePredicate()
            );

            ValueNode<Object> t = parser.next().withType(valuePred).chop(),
                    u = parser.next().withType(valuePred).last().chop();

            return new IfImpl<Object>(
                    condition,
                    t,
                    u
            );
        }

        else if(CaseImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(CaseImpl.TOKEN, arguments);

            Utils.SamePredicate samePredicate = Utils.isSamePredicate();

            List<ValueNode<?>> conditionValuePairs = new ArrayList<ValueNode<?>>();

            while(parser.remaining() >= 2) {
                ValueNode<Double> conditionNode = parser.next().withType(Utils.isNumericPredicate()).chop();

                ValueNode<Object> valueNode = parser.next().withType(samePredicate).chop();

                conditionValuePairs.add(conditionNode);
                conditionValuePairs.add(valueNode);
            }

            if(parser.hasNext())
                conditionValuePairs.add((ValueNode<?>) parser.next().withType(samePredicate).last().chop());

            return new CaseImpl<Object>(
                conditionValuePairs
            );
        }

        return null;
    }
}
