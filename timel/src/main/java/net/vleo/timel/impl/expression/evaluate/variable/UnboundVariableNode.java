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
package net.vleo.timel.impl.expression.evaluate.variable;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.variable.Variable;
import net.vleo.timel.impl.expression.compile.DebugFactory;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.ChopUpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.ValueType;

/**
 * An unbound read-only variable.
 *
 * @author Andrea Leofreddi
 */
public class UnboundVariableNode<V> extends AbstractVariableNode<V> {
    public UnboundVariableNode(String id, ValueType<V> type) {
        super(id, type);
    }

    @Override
    public UpscalableIterator<V> evaluate(Interval interval, ExecutorContext context) {
        final DebugFactory.DebugBuilder debug = DebugFactory.of(this, context);

        String id = getId();

        Variable<V> variable = context.instanceVariable(id, getType());

        return debug.build("variable", interval,
                new ChopUpscalableIterator<V>(
                        new UpscalerWrapperIterator<V>(
                                getType().getUpscaler(),
                                new FilterNullTimeIterator<V>(
                                        variable.readForward(
                                                interval,
                                                context
                                        )
                                )
                        ),
                        interval
                )
        );
    }
}
