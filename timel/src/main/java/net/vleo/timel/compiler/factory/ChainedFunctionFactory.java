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
package net.vleo.timel.compiler.factory;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A function factory that will chain multiple factories.
 *
 * When instancing a token, this factory will sequentially try to use each
 * provided factory returning the first non null result (if any).
 *
 *
 * @author Andrea Leofreddi
 */
public final class ChainedFunctionFactory extends FunctionFactory {
    private final Collection<FunctionFactory> factories;

    public static FunctionFactory of(final Collection<FunctionFactory> factories) {
        return new ChainedFunctionFactory(factories);
    }

    public static FunctionFactory of(final FunctionFactory first, final FunctionFactory second, final FunctionFactory... rest) {
        List<FunctionFactory> factories = new ArrayList<FunctionFactory>(2 + rest.length);

        factories.add(first);
        factories.add(second);

        factories.addAll(Arrays.asList(rest));

        return new ChainedFunctionFactory(factories);
    }

    private ChainedFunctionFactory(Collection<FunctionFactory> factories) {
        this.factories = factories;
    }

    @Override
    public void preVisit(String id, List<String> arguments, CompilerContext context) throws ParseException {
        for(FunctionFactory factory : factories)
            factory.preVisit(id, arguments, context);
    }

    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        TreeNode r;

        for(FunctionFactory factory : factories) {
            r = factory.instance(id, arguments, context);

            if(r != null)
                // We got a match
                return r;
        }

        // Token is unparsed
        return null;
    }
}
