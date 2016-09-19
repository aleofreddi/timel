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

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.ValueType;

import java.util.Arrays;
import java.util.List;

/**
 * Common superclass for {@link ValueNode} functions.
 *
 * @author Andrea Leofreddi
 */
public abstract class AbstractValueFunction<V> extends AbstractFunction implements ValueNode<V> {
    protected AbstractValueFunction(String id, ValueType<V> returnType, TreeNode... arguments) {
        this(id, returnType, Arrays.asList(arguments));
    }

    protected <T extends TreeNode> AbstractValueFunction(String id, ValueType<V> returnType, List<T> arguments) {
        super(id, returnType, arguments);
    }

    @Override
    public void execute(Interval interval, ExecutorContext context) {
        TimeIterator<V> iter = evaluate(interval, context);

        while(iter.hasNext())
            iter.next();
    }

    @Override
    public ValueType<V> getType() {
        return (ValueType<V>) super.getType();
    }

    /**
     * Default implementation of {@link ValueNode#isConstant()} which returns false.
     */
    @Override
    public boolean isConstant() {
        return false;
    }
}
