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
package net.vleo.timel.impl.expression.evaluate.function.eval;

import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.impl.expression.compile.DebugFactory;
import net.vleo.timel.iterator.CoalesceIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.time.Interval;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Coalesce function.
 *
 * @author Andrea Leofreddi
 */
public class CoalesceImpl<V> extends AbstractValueFunction<V> {
    public static final String TOKEN = "Coalesce";

    private List<ValueNode<V>> expressions;

    public CoalesceImpl(List<ValueNode<V>> arguments) throws ParseException {
        super(TOKEN, arguments.get(0).getType(), arguments);

        this.expressions = arguments;
    }

    @Override
    public UpscalableIterator<V> evaluate(Interval interval, ExecutorContext context) {
        DebugFactory.DebugBuilder debug = DebugFactory.of(this, context);

        ArrayList<UpscalableIterator<V>> iterators = new ArrayList<UpscalableIterator<V>>(arguments.size());

        // Get all the iterators
        for(ValueNode<V> expression : expressions)
            iterators.add(expression.evaluate(interval, context));

        return new UpscalerWrapperIterator<V>(
                getType().getUpscaler(),
                debug.build("coalesce", interval, new CoalesceIterator<V>(iterators))
        );
    }
}
