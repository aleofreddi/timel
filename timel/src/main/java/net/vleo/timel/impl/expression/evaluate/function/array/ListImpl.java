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
package net.vleo.timel.impl.expression.evaluate.function.array;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.compiler.tree.ArrayNode;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.IntersectIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.ArrayType;

import java.util.ArrayList;
import java.util.List;

/**
 * List function implementation. This function returns a tuple wrapping the given expressions.
 *
 * @author Andrea Leofreddi
 */
public class ListImpl extends AbstractValueFunction<Object[]> implements ArrayNode {
    public static final String TOKEN = "List";

    private List<ValueNode<?>> elements;

    public ListImpl(ArrayType type, List<ValueNode<?>> elements) {
        super(TOKEN, type, elements);

        this.elements = elements;
    }

    @Override
    public List<ValueNode<?>> getElements() {
        return elements;
    }

    @Override
    public UpscalableIterator<Object[]> evaluate(Interval interval, ExecutorContext context) {
        List<UpscalableIterator<?>> iterators = new ArrayList<UpscalableIterator<?>>(elements.size());

        for(ValueNode<?> element : elements)
            iterators.add(element.evaluate(interval, context));

        return new IntersectIterator(iterators);
    }

    @Override
    public boolean isConstant() {
        for(ValueNode<?> element : elements)
            if(!element.isConstant())
                return false;

        return true;
    }

    @Override
    public String toCanonicalExpression() {
        StringBuilder sb = new StringBuilder();

        for(TreeNode expression : elements) {
            if(sb.length() == 0)
                sb.append("{ ");
            else
                sb.append(", ");

            sb.append(expression.toCanonicalExpression());
        }

        sb.append(" }");

        return sb.toString();
    }

    @Override
    public String treeDump() {
        StringBuilder sb = new StringBuilder();

        for(TreeNode expression : elements) {
            if(sb.length() == 0)
                sb.append("{ ");
            else
                sb.append(", ");

            sb.append(expression.treeDump());
        }

        sb.append(" }");

        return sb.toString();
    }
}
