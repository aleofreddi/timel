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
package net.vleo.timel.impl.expression.evaluate.function.resample;

import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.impl.expression.compile.DebugFactory;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.impl.expression.evaluate.constant.StartImpl;
import net.vleo.timel.impl.expression.evaluate.constant.StopImpl;
import net.vleo.timel.impl.expression.evaluate.function.time.IntervalImpl;
import net.vleo.timel.iterator.NestedLoopTimeIterator;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.ValueType;

/**
 * An abstract superclass for downscalers.
 *
 * @author Andrea Leofreddi
 */
public abstract class SimpleDownscalerImpl<V> extends AbstractValueFunction<V> {
    private final ValueNode<V> valueNode;

    private final ValueNode<Interval> periodicityNode;

    public SimpleDownscalerImpl(
            String token,
            ValueType<V> returnType,
            ValueNode<V> valueNode,
            ValueNode<Interval> periodicityNode
    ) throws ParseException {
        super(token, returnType, valueNode, periodicityNode);

        this.valueNode = valueNode;
        this.periodicityNode = periodicityNode;
    }

    public SimpleDownscalerImpl(
            String token,
            ValueType<V> returnType,
            ValueNode<V> valueNode
    ) throws ParseException {
        super(token, returnType, valueNode);

        this.valueNode = valueNode;
        this.periodicityNode = new IntervalImpl(
                new StartImpl(),
                new StopImpl()
        );
    }

    protected abstract TimeIterator<V> getDownscaler(Interval interval, ExecutorContext context);

    @Override
    public UpscalableIterator<V> evaluate(Interval interval, final ExecutorContext context) {
        final DebugFactory.DebugBuilder debug = DebugFactory.of(this, context);

        return new UpscalerWrapperIterator<V>(
                getType().getUpscaler(),

                // (1) For each period in intervalNode
                debug.build("forEachPeriodicity", interval, new NestedLoopTimeIterator<Interval, V>(
                        periodicityNode.evaluate(
                                interval,
                                context
                        )
                ) {
                    @Override
                    protected TimeIterator<V> nestedIterator(Sample<Interval> value) {
                        Interval periodInterval = value.getValue();

                        // (2) Downscale value for the given period
                        return debug.build("innerDownscale", periodInterval,
                                getDownscaler(periodInterval, context)
                        );
                    }
                })
        );
    }
}
