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
package net.vleo.timel.impl.expression.evaluate.function.conditional;

import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.impl.expression.evaluate.constant.SimpleConstantNode;
import net.vleo.timel.impl.upscaler.SameUpscaler;
import net.vleo.timel.iterator.*;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.Types;
import net.vleo.timel.type.ValueType;

import java.util.*;

/**
 * Case operator implementation.
 *
 * @author Andrea Leofreddi
 */
public class CaseImpl<V> extends AbstractValueFunction<V> {
    public static final String TOKEN = "Case";

    private static final ValueNode<Double> TRUE_CONDITION = new SimpleConstantNode<Double>(Types.getNumericDoubleType(), 1.0) { };

    private final List<ValueNode<Double>> conditionNodes = new ArrayList<ValueNode<Double>>();

    private final List<ValueNode<V>> values = new ArrayList<ValueNode<V>>();

    private static <V> ValueType<V> extractType(List<ValueNode<?>> conditionValuePairs) {
        if(conditionValuePairs.size() < 2)
            throw new IllegalArgumentException(TOKEN + " requires at least a condition-value pair");

        return (ValueType<V>) conditionValuePairs.get(conditionValuePairs.size() >= 2 ? 1 : 0).getType();
    }

    public CaseImpl(List<ValueNode<?>> conditionValuePairs) {
        super(CaseImpl.TOKEN, (ValueType<V>) extractType(conditionValuePairs), conditionValuePairs);

        int size = conditionValuePairs.size();

        // Condition-Value pairs
        for(int i = 0; i < ((size % 2) == 1 ? size - 1 : size); i += 2) {
            ValueNode<Double> condition = (ValueNode<Double>) conditionValuePairs.get(i);

            ValueNode<V> value = (ValueNode<V>) conditionValuePairs.get(i + 1);

            conditionNodes.add(condition);
            values.add(value);
        }

        // Add the last "else" clause
        if(size % 2 == 1) {
            ValueNode<V> value = (ValueNode<V>) conditionValuePairs.get(size - 1);

            conditionNodes.add(TRUE_CONDITION);
            values.add(value);
        }
    }

    @Override
    public UpscalableIterator<V> evaluate(final Interval interval, final ExecutorContext context) {
        CoalesceIterator<Integer> coalesce;

        //
        // Assemble the coalesce-set as all the conditionNodes transformed as follows:
        //
        // true -> #condition index
        // false or undef -> undef
        //
        // In such a way we can use coalesce's output as pointer to the right value
        //
        {
            final List<UpscalableIterator<Integer>> coalesceArgs = new LinkedList<UpscalableIterator<Integer>>();

            int position = 0;

            for(ValueNode<Double> conditionNode : conditionNodes) {
                final int current = position;

                coalesceArgs.add(
                        new UpscalerWrapperIterator<Integer>(
                                SameUpscaler.<Integer>get(),
                                new SplitAdapterTimeIterator<Double, Integer>(
                                        conditionNode.evaluate(interval, context)
                                ) {
                                    @Override
                                    protected List<Sample<Integer>> adapt(Sample<Double> sample) {
                                        Double v = sample.getValue();

                                        if(v.equals(1.0))
                                            return Arrays.asList(sample.copyWithValue(current));

                                        // Undefine false values
                                        return Collections.emptyList();
                                    }
                                }
                        )
                );

                position++;
            }

            coalesce = new CoalesceIterator<Integer>(coalesceArgs);
        }

        return new UpscalerWrapperIterator<V>(
                getType().getUpscaler(),
                // (1) For each condition coalesce
                new NestedLoopTimeIterator<Integer, V>(
                        coalesce
                ) {
                    // (2) Get its position, and evaluate the relative value node for the given interva
                    @Override
                    protected TimeIterator<V> nestedIterator(Sample<Integer> value) {
                        Interval interval = value.getInterval();

                        int position = value.getValue();

                        return values.get(position).evaluate(interval, context);
                    }
                }
        );
    }
}

