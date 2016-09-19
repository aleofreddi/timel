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

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.impl.expression.compile.DebugFactory;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.impl.expression.evaluate.function.resample.ResampleFactory;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.iterator.NestedLoopTimeIterator;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.NumericDoubleType;
import net.vleo.timel.type.NumericType;
import net.vleo.timel.type.Types;

/**
 * Implementation of the eval function.
 *
 * @author Andrea Leofreddi
 */
public class EvalImpl extends AbstractValueFunction<Double> {
    public static final String TOKEN = "Eval";

    private final ValueNode<Double> valueNode;

    private final ValueNode<Interval> intervalNode;

    public EvalImpl(ValueNode<Double> valueNode, ValueNode<Interval> interval) {
        super(TOKEN, Types.getNumericDoubleType(), valueNode, interval);

        this.valueNode = valueNode;
        this.intervalNode = interval;
    }

    @Override
    public UpscalableIterator<Double> evaluate(final Interval interval, final ExecutorContext context) {
        final DebugFactory.DebugBuilder debug = DebugFactory.of(this, context);

        return new UpscalerWrapperIterator<Double>(
                getType().getUpscaler(),

                // (1) For each interval in intervalNode
                debug.build("forEachInterval", interval, new NestedLoopTimeIterator<Interval, Double>(
                        intervalNode.evaluate(
                                interval,
                                context
                        )
                ) {
                    @Override
                    protected TimeIterator<Double> nestedIterator(Sample<Interval> sample) {
                        final Interval evalInterval = sample.getValue(), intoInterval = sample.getInterval();

                        // (2) Evaluate the inner value using evalInterval and adapt the output to the outer intoInterval
                        return debug.build("adaptIntoInterval", interval, new AdapterTimeIterator<Double, Double>(
                                debug.build("innerDownscale", evalInterval,
                                        // (3) Scalarize the content
                                        ResampleFactory.getDownscaler(
                                                valueNode.getType(),
                                                // (4) Evaluate valueNode in evalInterval
                                                valueNode.evaluate(evalInterval, context),
                                                evalInterval
                                        )
                                )
                        ) {
                            @Override
                            protected Sample<Double> adapt(Sample<Double> sample) {
                                return sample.copyWithInterval(intoInterval);
                            }
                        });
                    }
                })
        );
    }
}
