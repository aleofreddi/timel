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
package net.vleo.timel.impl.type;

import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.impl.expression.utils.Predicate;

/**
 * Commodity functions for TreeNodes.
 *
 * @author Andrea Leofreddi
 */
public class TreeNodes {
    private TreeNodes() {
        throw new AssertionError();
    }

    public static <T extends TreeNode> Predicate<TreeNode> isInstanceOfPredicate(final Class<T> clazz) {
        return new Predicate<TreeNode>() {
            @Override
            public boolean apply(TreeNode input) {
                return input.getClass() == clazz;
            }

            @Override
            public String toString() {
                return "Is(" + clazz.getSimpleName() + ")";
            }
        };
    }
}
