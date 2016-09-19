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
import net.vleo.timel.executor.Sample;
import net.vleo.timel.executor.variable.Variable;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.impl.expression.evaluate.variable.AbstractVariableNode;
import net.vleo.timel.iterator.*;
import net.vleo.timel.time.Interval;

/**
 * Retrieve the last value before a given date.
 *
 * @author Andrea Leofreddi
 */
public class PreviousValue<V> extends AbstractValueFunction<V> {
    public static final String TOKEN = "PreviousValue";

    private final String variableId;

    private final ValueNode<Long> beforeNode;

    public PreviousValue(ValueNode<V> valueNode, ValueNode<Long> beforeNode) throws ParseException {
        super(TOKEN, valueNode.getType(), valueNode, beforeNode);

        if(!(valueNode instanceof AbstractVariableNode))
            throw new ParseException(TOKEN + " requires a variable argument");

        this.variableId = ((AbstractVariableNode<?>) valueNode).getId();
        this.beforeNode = beforeNode;
    }

    @Override
    public UpscalableIterator<V> evaluate(Interval interval, final ExecutorContext context) {
        final Variable<V> variable = context.getVariable(variableId);

        return new UpscalerWrapperIterator<V>(
                getType().getUpscaler(),
                // (1) For each before node  value
                new AdapterTimeIterator<Sample<V>, V>(
                        new PreviousIterator<V>(variable, beforeNode, interval, context)
                ) {
                    @Override
                    protected Sample<V> adapt(Sample<Sample<V>> sample) {
                        return sample.copyWithValue(sample.getValue().getValue());
                    }
                }
        );
    }
}
