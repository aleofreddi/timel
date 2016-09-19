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
import net.vleo.timel.executor.ExecutionException;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.executor.variable.Variable;
import net.vleo.timel.impl.expression.compile.DebugFactory;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.iterator.ChopUpscalableIterator;
import net.vleo.timel.time.Interval;

/**
 * A variable that is bound to an existing backend node.
 * <p/>
 * When being evaluated might load data from the backend (if not already available).
 *
 * @author Andrea Leofreddi
 */
public class BoundVariableNode<V> extends AbstractVariableNode<V> {
    private final ValueNode<V> backendNode;

    public BoundVariableNode(String id, ValueNode<V> backendNode) {
        super(id, backendNode.getType());

        this.backendNode = backendNode;
    }

    public ValueNode<V> getBackendNode() {
        return backendNode;
    }

    @Override
    public UpscalableIterator<V> evaluate(Interval interval, ExecutorContext context) {
        final DebugFactory.DebugBuilder debug = DebugFactory.of(this, context);

        String id = getId();

        Variable<V> variable = context.instanceVariable(id, getType());

        if(variable == null)
            throw new ExecutionException("Variable " + id + " is missing in the evaluation context");

        return debug.build("variable", interval,
                new ChopUpscalableIterator<V>(
                        new UpscalerWrapperIterator<V>(
                                getType().getUpscaler(),
                                new FilterNullTimeIterator<V>(
                                        new AdapterTimeIterator<VariablePayload<V>, V>( /* Upscalable */
                                                new GapEvaluatorTimeIterator<V>(
                                                        variable,
                                                        backendNode.evaluate(
                                                                interval,
                                                                context
                                                        ),
                                                        interval,
                                                        context
                                                )
                                        ) {
                                            @Override
                                            protected Sample<V> adapt(Sample<VariablePayload<V>> sample) {
                                                return Sample.of(
                                                        sample.getInterval(),
                                                        sample.getValue().getPayload()
                                                );
                                            }
                                        }
                                )
                        ),
                        interval
                )
        );
    }
}
