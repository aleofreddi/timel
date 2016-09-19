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
package net.vleo.timel.impl.expression.evaluate.function.comparison;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.FunctionArgumentParser;
import net.vleo.timel.impl.type.Utils;
import net.vleo.timel.type.Type;
import net.vleo.timel.type.ValueType;
import net.vleo.timel.impl.expression.utils.Predicate;
import net.vleo.timel.impl.expression.utils.Predicates;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrea Leofreddi
 */
public class ComparisonFactory extends FunctionFactory {
    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(GreatestImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(GreatestImpl.TOKEN, arguments);

            Predicate<Type> isValid = Predicates.and(Utils.isValuePredicate(), Utils.isSamePredicate());

            List<ValueNode<? extends Comparable<?>>> funArgs = new ArrayList<ValueNode<? extends Comparable<?>>>();

            do {
                ValueNode<? extends Comparable<?>> value = parser.next().withType(isValid).chop();

                funArgs.add(value);
            } while(parser.hasNext());

            ValueType<?> type = (ValueType<?>) parser.current().genType();

            return new GreatestImpl(
                    type,
                    funArgs
            );
        } else if(LeastImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(LeastImpl.TOKEN, arguments);

            Predicate<Type> isValid = Predicates.and(Utils.isValuePredicate(), Utils.isSamePredicate());

            List<ValueNode<? extends Comparable<?>>> funArgs = new ArrayList<ValueNode<? extends Comparable<?>>>();

            do {
                ValueNode<? extends Comparable<?>> value = parser.next().withType(isValid).chop();

                funArgs.add(value);
            } while(parser.hasNext());

            ValueType<?> type = (ValueType<?>) parser.current().genType();

            return new LeastImpl(
                    type,
                    funArgs
            );
        }

        return null;
    }
}
