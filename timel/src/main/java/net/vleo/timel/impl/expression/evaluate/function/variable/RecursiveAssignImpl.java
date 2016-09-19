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
package net.vleo.timel.impl.expression.evaluate.function.variable;

import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.impl.expression.evaluate.variable.AbstractVariableNode;
import net.vleo.timel.impl.expression.evaluate.variable.BoundVariableNode;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;

/**
 * Recursive assignment operator (set function). This is a variant of Set which allows the use of the recursion in the definition.
 *
 * @author Andrea Leofreddi
 */
public class RecursiveAssignImpl<V> extends AbstractValueFunction<V> {
    public static final String TOKEN = "RecSet";

    private AbstractVariableNode<V> variableNode;

    public RecursiveAssignImpl(BoundVariableNode<V> variableNode) throws ParseException {
        super(TOKEN, variableNode.getType(), variableNode, variableNode.getBackendNode());

        this.variableNode = variableNode;
    }

    @Override
    public UpscalableIterator<V> evaluate(Interval interval, ExecutorContext context) {
        return variableNode.evaluate(interval, context);
    }

    @Override
    public boolean isConstant() {
        return variableNode.isConstant();
    }
}
