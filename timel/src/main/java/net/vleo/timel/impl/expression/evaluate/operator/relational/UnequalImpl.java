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
package net.vleo.timel.impl.expression.evaluate.operator.relational;


import net.vleo.timel.compiler.tree.ValueNode;

/**
 * Less operator implementation
 *
 * @author Andrea Leofreddi
 */
public class UnequalImpl<V extends Comparable<V>> extends AbstractBinaryPredicate<V, V> {
    public static final String TOKEN = "Unequal";

    public UnequalImpl(ValueNode<V> t, ValueNode<V> u) {
        super(UnequalImpl.TOKEN, t, u, new BinaryBooleanFunction<V, V>() {
            @Override
            public boolean apply(V t, V u) {
                return t.compareTo(u) != 0;
            }
        });
    }
}

