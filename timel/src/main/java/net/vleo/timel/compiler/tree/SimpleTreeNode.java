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
package net.vleo.timel.compiler.tree;

import net.vleo.timel.type.Type;

/**
 * A simple abstract class for TreeNodes.
 *
 * This class will provide equals implementation in terms of the same canonical expression.
 *
 * @author Andrea Leofreddi
 */
public abstract class SimpleTreeNode implements TreeNode {
    private final Type type;

    public SimpleTreeNode(Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(!(obj instanceof TreeNode))
            return false;

        TreeNode node = (TreeNode) obj;

        return toCanonicalExpression().equals(node.toCanonicalExpression());
    }
}
