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
package net.vleo.timel.impl.expression.evaluate.function.array;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.FunctionArgumentParser;
import net.vleo.timel.impl.type.Utils;
import net.vleo.timel.type.ArrayType;
import net.vleo.timel.type.Types;
import net.vleo.timel.type.ValueType;

import java.util.ArrayList;
import java.util.List;

/**
 * At operator extracts a specified positional element from an array or a tuple.
 *
 * @author Andrea Leofreddi
 */
public class ArrayFactory extends FunctionFactory {
    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(AtImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(AtImpl.TOKEN, arguments);

            ValueNode<Object[]> arrayNode = parser.next().withType(Utils.isArrayPredicate()).chop();

            ArrayType arrayType = parser.current().<ArrayType, Object[]>type();

            ValueNode<Double> indexNode = parser.next().last().withType(Utils.isNumericAveragePredicate()).chop();

            return new AtImpl<Object>(
                    arrayType,
                    arrayNode,
                    indexNode
            );
        } else if(ListImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(ListImpl.TOKEN, arguments);

            List<ValueNode<?>> elements = new ArrayList<ValueNode<?>>();

            List<ValueType<?>> types = new ArrayList<ValueType<?>>();

            do {
                elements.add((ValueNode<?>) parser.next().withType(Utils.isValuePredicate()).chop());

                types.add((ValueType<?>) parser.current().type2());
            } while(parser.hasNext());

            ArrayType arrayType = Types.getArrayType(types);

            return new ListImpl(
                    arrayType,
                    elements
            );
        }

        return null;
    }
}
