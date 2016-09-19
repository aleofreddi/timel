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
package net.vleo.timel.impl.expression.evaluate.type;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.impl.expression.compile.FunctionArgumentParser;
import net.vleo.timel.impl.expression.evaluate.constant.NumericConstantNode;
import net.vleo.timel.impl.expression.evaluate.operator.relational.*;
import net.vleo.timel.impl.expression.utils.Predicate;
import net.vleo.timel.impl.expression.utils.Predicates;
import net.vleo.timel.impl.type.TreeNodes;
import net.vleo.timel.impl.type.Utils;
import net.vleo.timel.type.Type;
import net.vleo.timel.type.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrea Leofreddi
 */
public class MetaTypeFactory extends FunctionFactory {
    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(IntegralDoubleImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(EqualImpl.TOKEN, arguments);

            NumericConstantNode degreeNode = parser.next()
                    .withNode(TreeNodes.isInstanceOfPredicate(NumericConstantNode.class))
                    .last()
                    .chop();

            return new IntegralDoubleImpl(
                    degreeNode
            );

        } else if(NumericDoubleImpl.TOKEN.equals(id)) {
            if(!arguments.isEmpty())
                throw new ParseException(NumericDoubleImpl.TOKEN + " function requires no arguments");

            return new NumericDoubleImpl();

        } else if(NumericDoubleImpl.TOKEN.equals(id)) {
            if(!arguments.isEmpty())
                throw new ParseException(NumericDoubleImpl.TOKEN + " function requires no arguments");

            return new NumericDoubleImpl();

        } else if(ArrayImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(ArrayImpl.TOKEN, arguments);

            List<AbstractMetaType> innerTypeNodes = new ArrayList<AbstractMetaType>();

            do {
                AbstractMetaType innerTypeNode =  parser.next().withType(Utils.isMetaTypePredicate()).chop();

                innerTypeNodes.add(innerTypeNode);
            } while(parser.hasNext());

            return new ArrayImpl(innerTypeNodes);
        }

        return null;
    }
}
