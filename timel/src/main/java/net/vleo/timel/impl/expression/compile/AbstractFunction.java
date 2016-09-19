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
import net.vleo.timel.compiler.tree.SimpleTreeNode;
import net.vleo.timel.type.Type;

import java.util.Arrays;
import java.util.List;

/**
 * Common superclass for functions.
 *
 * @author Andrea Leofreddi
 */
public abstract class AbstractFunction extends SimpleTreeNode {
    private final String id;

    protected final List<TreeNode> arguments;

    protected AbstractFunction(String id, Type returnType, TreeNode... arguments) {
        this(id, returnType, Arrays.asList(arguments));
    }

    protected <T extends TreeNode> AbstractFunction(String id, Type returnType, List<T> arguments) {
        super(returnType);

        this.id = id;
        this.arguments = (List<TreeNode>) arguments;
    }

    public String getId() {
        return id;
    }

    @Override
    public List<TreeNode> getChildren() {
        return arguments;
    }

    /**
     * Assemble the (prefixed) representation of the expression tree.
     */
    @Override
    public String toCanonicalExpression() {
        StringBuilder sb = new StringBuilder();

        sb.append(id);

        if(!arguments.isEmpty()) {
            sb.append("[");

            for(int i = 0; i < arguments.size(); i++) {
                if(i > 0)
                    sb.append(", ");

                sb.append(arguments.get(i).toCanonicalExpression());
            }

            sb.append("]");
        }

        return sb.toString();
    }

    @Override
    public String treeDump() {
        StringBuilder sb = new StringBuilder();

        sb.append(id);

        if(!arguments.isEmpty()) {
            sb.append("[");

            for(int i = 0; i < arguments.size(); i++) {
                if(i > 0)
                    sb.append(", ");

                sb.append(arguments.get(i).treeDump());
            }

            sb.append("]");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toCanonicalExpression();
    }
}
