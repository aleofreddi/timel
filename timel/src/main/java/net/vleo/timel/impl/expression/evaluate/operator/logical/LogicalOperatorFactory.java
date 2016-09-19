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
package net.vleo.timel.impl.expression.evaluate.operator.logical;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.FunctionArgumentParser;
import net.vleo.timel.impl.type.Utils;

import java.util.List;

/**
 *
 * @author Andrea Leofreddi
 */
public class LogicalOperatorFactory extends FunctionFactory {
    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(id.equals(AndImpl.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(AndImpl.TOKEN, arguments);

            return new AndImpl(
                    (ValueNode<Double>) parser.next().withType(Utils.isValuePredicate()).chop2(),
                    (ValueNode<Double>) parser.next().withType(Utils.isValuePredicate()).last().chop2()
            );

        } else if(id.equals(OrImpl.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(OrImpl.TOKEN, arguments);

            return new OrImpl(
                    (ValueNode<Double>) parser.next().withType(Utils.isValuePredicate()).chop2(),
                    (ValueNode<Double>) parser.next().withType(Utils.isValuePredicate()).last().chop2()
            );

        } else if(id.equals(XorImpl.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(OrImpl.TOKEN, arguments);

            return new XorImpl(
                    (ValueNode<Double>) parser.next().withType(Utils.isValuePredicate()).chop2(),
                    (ValueNode<Double>) parser.next().withType(Utils.isValuePredicate()).last().chop2()
            );

        } else if(id.equals(NotImpl.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(NotImpl.TOKEN, arguments);

            return new NotImpl(
                    (ValueNode<Double>) parser.next().withType(Utils.isValuePredicate()).last().chop2()
            );
        }

        return null;
    }
}
