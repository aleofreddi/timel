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
import net.vleo.timel.impl.expression.evaluate.function.round.CeilingImpl;
import net.vleo.timel.impl.expression.evaluate.function.round.FloorImpl;
import net.vleo.timel.impl.expression.evaluate.function.round.RoundImpl;
import net.vleo.timel.impl.type.Utils;

import java.util.List;

/**
 * Factory for rounding functions.
 *
 * @author Andrea Leofreddi
 */
public class RoundFactory extends FunctionFactory {
    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(FloorImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(id, arguments);

            ValueNode<Double> valueNode = parser.next().withType(Utils.isValuePredicate()).last().chop();

            return new FloorImpl(valueNode);
        } else if(CeilingImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(id, arguments);

            ValueNode<Double> valueNode = parser.next().withType(Utils.isValuePredicate()).last().chop();

            return new CeilingImpl(valueNode);
        } else if(RoundImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(id, arguments);

            ValueNode<Double> valueNode = parser.next().withType(Utils.isValuePredicate()).last().chop();

            return new RoundImpl(valueNode);
        }

        return null;
    }
}
