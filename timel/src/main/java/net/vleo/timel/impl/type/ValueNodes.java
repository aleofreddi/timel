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
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.utils.Predicate;

/**
 * Commodity functions for ValueNodes.
 *
 * @author Andrea Leofreddi
 */
public class ValueNodes {
    private ValueNodes() {
        throw new AssertionError();
    }

     public static Predicate<TreeNode> isConstantPredicate() {
        return new Predicate<TreeNode>() {
            @Override
            public boolean apply(TreeNode input) {
                return ((ValueNode<?>) input).isConstant();
            }

            @Override
            public String toString() {
                return "Constant Value";
            }
        };
    }
}
