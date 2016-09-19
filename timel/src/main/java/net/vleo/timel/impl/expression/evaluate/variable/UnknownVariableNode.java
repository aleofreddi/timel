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
package net.vleo.timel.impl.expression.evaluate.variable;

import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.SimpleTreeNode;
import net.vleo.timel.impl.type.UnknownVariableType;

import java.util.Collections;
import java.util.List;

/**
 * A node representing an unassigned variable.
 *
 * This class is used as placeholder by the compiler when it encounters
 * a new unknown variable, and is later replaced by Assign operators
 * with a concrete implementation.
 *
 * @author Andrea Leofreddi
 */
public class UnknownVariableNode extends SimpleTreeNode {
    private final String id;

    public UnknownVariableNode(String id) {
        super(UnknownVariableType.get());

        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toCanonicalExpression() {
        return id;
    }

    @Override
    public String treeDump() {
        return id;
    }

    @Override
    public List<TreeNode> getChildren() {
        return Collections.emptyList();
    }
}
