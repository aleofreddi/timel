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

import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.StatementNode;
import net.vleo.timel.impl.type.Utils;

import java.util.List;

/**
 * Common superclass for {@link StatementNode} functions.
 *
 * @author Andrea Leofreddi
 */
public abstract class AbstractStatementFunction extends AbstractFunction implements StatementNode {
    protected AbstractStatementFunction(String function, TreeNode... arguments) {
        super(function, Utils.getStatement(), arguments);
    }

    protected AbstractStatementFunction(String function, List<TreeNode> arguments) {
        super(function, Utils.getStatement(), arguments);
    }
}
