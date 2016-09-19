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
package net.vleo.timel.impl.expression.evaluate.type;

import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.impl.expression.compile.AbstractMetaTypeFunction;
import net.vleo.timel.type.Type;

import java.util.List;

/**
 * Suplerclass for meta type functions.
 *
 * @author Andrea Leofreddi
 */
class AbstractMetaType extends AbstractMetaTypeFunction {
    private final Type nestedType;

    /**
     * Retrieve the nested type for the meta type node.
     *
     * @return The nested type
     */
    public Type getNestedType() {
        return nestedType;
    }

    protected AbstractMetaType(String id, Type nestedType, TreeNode... arguments) {
        super(id, arguments);

        this.nestedType = nestedType;
    }

    protected <T extends TreeNode> AbstractMetaType(String id, Type nestedType, List<T> arguments) {
        super(id, arguments);

        this.nestedType = nestedType;
    }
}
