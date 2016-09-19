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
package net.vleo.timel.impl.expression.evaluate.function.math;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.FunctionArgumentParser;
import net.vleo.timel.impl.type.Utils;

import java.util.List;

/**
 * Function factory for math functions.
 *
 * @author Andrea Leofreddi
 */
public class MathFactory extends FunctionFactory {

    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(ModImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(ModImpl.TOKEN, arguments);

            ValueNode<Double> dividendNode = parser.next().withType(Utils.isNumericAveragePredicate()).chop();

            ValueNode<Double> divisorNode = parser.next().withType(Utils.isNumericAveragePredicate()).last().chop();

            return new ModImpl(
                    dividendNode,
                    divisorNode
            );

        } else if(SinImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(SinImpl.TOKEN, arguments);

            ValueNode<Double> valueNode = parser.next().withType(Utils.isNumericAveragePredicate()).last().chop();

            return new SinImpl(
                    valueNode
            );

        } else if(CosImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(CosImpl.TOKEN, arguments);

            ValueNode<Double> valueNode = parser.next().withType(Utils.isNumericAveragePredicate()).last().chop();

            return new CosImpl(
                    valueNode
            );

        } else if(AbsImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(AbsImpl.TOKEN, arguments);

            ValueNode<Double> valueNode = parser.next().withType(Utils.isNumericAveragePredicate()).last().chop();

            return new AbsImpl(
                    valueNode
            );
        }

        return null;
    }
}
