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
package net.vleo.timel.impl.expression.evaluate.operator.logical;


import net.vleo.timel.compiler.tree.ValueNode;

/**
 * And connective implementation
 *
 * @author Andrea Leofreddi
 */
public class AndImpl extends AbstractTrivalentBinaryLogicalOperator {
    public static final String TOKEN = "And";

    protected AndImpl(ValueNode<Double> tNode, ValueNode<Double> uNode) {
        super(TOKEN, tNode, uNode);
    }

    @Override
    protected Boolean apply(Boolean t, Boolean u) {
        if(t != null && !t || u != null && !u)
            return false;

        if(t == null || u == null)
            return null;

        return true;
    }
}

