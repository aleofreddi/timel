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

import java.util.List;

/**
 * Abstract class for function factories. Function factories can be used to
 * enhance the language adding new functions or symbols.
 *
 * @author Andrea Leofreddi
 */
public abstract class FunctionFactory {
    /**
     * Visit the node before compiling it. Previsit can be used when the children parsing depends on the parent.
     *
     * @param id        The function id
     * @param arguments The unparsed arguments
     * @param context   Compiler context
     */
    public void preVisit(
            String id,
            List<String> arguments,
            CompilerContext context
    ) throws ParseException {
        // Default implementation does nothing
    }

    /**
     * Compile a function and return its compiled treenode.
     *
     * @param id        The function id
     * @param arguments The parse arguments
     * @param context   Compiler context
     * @return The compiled tree node
     * @throws ParseException When parsing fails
     */
    public abstract TreeNode instance(
            String id,
            List<TreeNode> arguments,
            CompilerContext context
    ) throws ParseException;
}
