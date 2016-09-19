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
package net.vleo.timel.impl.expression.evaluate.operator.arithmetic;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.iterator.IntersectIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.PeriodType;
import net.vleo.timel.impl.upscaler.SameUpscaler;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.type.Types;
import org.joda.time.Period;

import net.vleo.timel.impl.expression.compile.PlusFactory;
import net.vleo.timel.executor.ExecutionException;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;

/**
 * Period times Number implementation.
 *
 * @author Andrea Leofreddi
 */
public class PeriodScaleImpl extends AbstractValueFunction<String> {
    private ValueNode<String> tNode;

    private ValueNode<Double> uNode;

    public PeriodScaleImpl(ValueNode<String> tNode, ValueNode<Double> uNode) {
        super(PlusFactory.TOKEN, Types.getPeriodType(), tNode, uNode);

        this.tNode = tNode;
        this.uNode = uNode;
    }

    @Override
    public UpscalableIterator<String> evaluate(Interval interval, ExecutorContext context) {
        return new UpscalerWrapperIterator<String>(
                getType().getUpscaler(),
                new AdapterTimeIterator<Object[], String>(
                        new IntersectIterator(
                                tNode.evaluate(interval, context),
                                uNode.evaluate(interval, context)
                        )
                ) {
                    @Override
                    protected Sample<String> adapt(Sample<Object[]> sample) {
                        Object[] values = sample.getValue();

                        String periodStr = (String) values[0];

                        Period t = new Period(periodStr);

                        double u = (Double) values[1];

                        if(Math.floor(u) != u)
                            throw new ExecutionException("Period scaling isAssignableFrom allowed only with integer operands");

                        return sample.copyWithValue(
                                t.multipliedBy((int) u).toString()
                        );
                    }
                }
        );
    }
}
