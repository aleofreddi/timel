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
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.Types;

/**
 * Retrieve the last stop before a given date.
 *
 * @author Andrea Leofreddi
 */
public class PreviousStop extends AbstractValueFunction<Long> {
    public static final String TOKEN = "PreviousStop";

    private final String variableId;

    private final ValueNode<Long> beforeNode;

    public PreviousStop(ValueNode<?> valueNode, ValueNode<Long> beforeNode) throws ParseException {
        super(TOKEN, Types.getTimeType(), valueNode, beforeNode);

        if(!(valueNode instanceof AbstractVariableNode))
            throw new ParseException(TOKEN + " requires a variable argument");

        this.variableId = ((AbstractVariableNode<?>) valueNode).getId();
        this.beforeNode = beforeNode;
    }

    @Override
    public UpscalableIterator<Long> evaluate(Interval interval, final ExecutorContext context) {
        final Variable<Object> variable = context.getVariable(variableId);

        return new UpscalerWrapperIterator<Long>(
                getType().getUpscaler(),
                // (1) For each before node  value
                new AdapterTimeIterator<Sample<Object>, Long>(
                        new PreviousIterator<Object>(variable, beforeNode, interval, context)
                ) {
                    @Override
                    protected Sample<Long> adapt(Sample<Sample<Object>> sample) {
                        return sample.copyWithValue(sample.getValue().getInterval().getEnd());
                    }
                }
        );
    }
}
