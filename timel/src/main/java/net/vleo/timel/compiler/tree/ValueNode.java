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

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.ValueType;

/**
 * An {@link TreeNode} able to evaluate time-variant expressions.
 *
 * Time variant expression are expressions which to a various values v1..n for
 * various time intervals.
 *
 * <pre>
 * f(X) &rarr; v1 | t0 &lt;= t &lt; t1, v2 | t1 &lt;= t &lt; t2 ... vN | tN &lt;= t &lt; tN+1
 * </pre>
 *
 * Each time aware expression can be queried to obtain the highest granularity of
 * its data, allowing the system to elaborate the expression tree without information loss
 * from source operands.
 *
 * @author Andrea Leofreddi
 */
public interface ValueNode<V> extends StatementNode {
    /**
     * Retrieve the evaluation interval set for this node.
     *
     * IT is mandatory for the implementor to implement evaluateIntervals as a <strong>pure</strong> function,
     * that is a call to evaluateIntervals has no side-effects.
     *
     * @param interval The interval period to evaluate
     * @param context
     * @return A set containing the intervals needed to compute this node
     */
    UpscalableIterator<V> evaluate(Interval interval, ExecutorContext context);

    /**
     * Test if the node refers to a constant, that is the evaluated value is a time invariant.
     *
     * @return True iff node refers to a constant
     */
    boolean isConstant();

    /**
     * See {#TreeNode.getType}.
     *
     * @return Node data type.
     */
    @Override
    ValueType<V> getType();
}
