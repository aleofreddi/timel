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
import net.vleo.timel.compiler.tree.MetaTypeNode;
import net.vleo.timel.type.Type;
import net.vleo.timel.type.Types;

import java.util.List;

/**
 * Common superclass for {@link MetaTypeNode} functions.
 *
 * @author Andrea Leofreddi
 */
public abstract class AbstractMetaTypeFunction extends AbstractFunction implements MetaTypeNode {
    protected AbstractMetaTypeFunction(String function, TreeNode... arguments) {
        super(function, Types.getMetaType(), arguments);
    }

    protected <T extends TreeNode> AbstractMetaTypeFunction(String function, List<T> arguments) {
        super(function, Types.getMetaType(), arguments);
    }
}
