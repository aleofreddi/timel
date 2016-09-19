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
package net.vleo.timel.impl.expression.evaluate.operator.relational;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.FunctionArgumentParser;
import net.vleo.timel.impl.type.Utils;
import net.vleo.timel.type.Type;
import net.vleo.timel.impl.expression.utils.Predicate;

import java.util.List;

/**
 * @author Andrea Leofreddi
 */
public class RelationalOperatorFactory extends FunctionFactory {
    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(id.equals(EqualImpl.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(EqualImpl.TOKEN, arguments);

            Predicate<Type> same = Utils.isSamePredicate();

            return new EqualImpl(
                    parser.next().withType(same).chopGenValue(),
                    parser.next().withType(same).last().chopGenValue()
            );

        } else if(id.equals(UnequalImpl.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(UnequalImpl.TOKEN, arguments);

            Predicate<Type> same = Utils.isSamePredicate();

            return new UnequalImpl(
                    parser.next().withType(same).chopGenValue(),
                    parser.next().withType(same).last().chopGenValue()
            );

        } else if(id.equals(LessEqualImpl.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(LessEqualImpl.TOKEN, arguments);

            Predicate<Type> same = Utils.isSamePredicate();

            return new LessEqualImpl(
                    parser.next().withType(same).chopGenValue(),
                    parser.next().withType(same).last().chopGenValue()
            );

        } else if(id.equals(LessImpl.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(LessImpl.TOKEN, arguments);

            Predicate<Type> same = Utils.isSamePredicate();

            return new LessImpl(
                    parser.next().withType(same).chopGenValue(),
                    parser.next().withType(same).last().chopGenValue()
            );

        } else if(id.equals(GreaterEqualImpl.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(GreaterEqualImpl.TOKEN, arguments);

            Predicate<Type> same = Utils.isSamePredicate();

            return new GreaterEqualImpl(
                    parser.next().withType(same).chopGenValue(),
                    parser.next().withType(same).last().chopGenValue()
            );

        } else if(id.equals(GreaterImpl.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(GreaterImpl.TOKEN, arguments);

            Predicate<Type> same = Utils.isSamePredicate();

            return new GreaterImpl(
                    parser.next().withType(same).chopGenValue(),
                    parser.next().withType(same).last().chopGenValue()
            );

        } else if(id.equals(Defined.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(Defined.TOKEN, arguments);

            ValueNode<?> valueNode = parser.next().withType(Utils.isValuePredicate()).last().chop();

            return new Defined(
                    valueNode
            );
        }

        return null;
    }
}
