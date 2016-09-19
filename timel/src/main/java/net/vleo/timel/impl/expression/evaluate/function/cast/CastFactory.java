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
package net.vleo.timel.impl.expression.evaluate.function.cast;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.FunctionArgumentParser;
import net.vleo.timel.impl.type.Utils;

import java.util.List;

/**
 * @author Andrea Leofreddi
 */
public class CastFactory extends FunctionFactory {
    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(AsAverageImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(AsAverageImpl.TOKEN, arguments);

            ValueNode<Double> valueNode = parser.next().withType(Utils.isNumericPredicate()).last().chop();

            return new AsAverageImpl(valueNode);
        } else if(AsIntegralImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(AsIntegralImpl.TOKEN, arguments);

            ValueNode<Double> valueNode = parser.next().withType(Utils.isNumericPredicate()).last().chop();

            return new AsIntegralImpl(valueNode);
        }

        return null;
    }
}
