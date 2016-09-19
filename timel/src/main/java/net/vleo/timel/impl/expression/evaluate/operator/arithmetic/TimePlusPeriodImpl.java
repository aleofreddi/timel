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
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.impl.expression.compile.PlusFactory;
import net.vleo.timel.impl.expression.evaluate.constant.StringConstantNode;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.iterator.IntersectIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.TimeType;
import net.vleo.timel.type.Types;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;

/**
 * Date + Period implementation.
 *
 * @author Andrea Leofreddi
 */
public class TimePlusPeriodImpl extends AbstractValueFunction<Long> {
    private ValueNode<Long> tNode;

    private ValueNode<String> uNode;

    private ValueNode<String> timeZoneIdNode;

    public TimePlusPeriodImpl(ValueNode<Long> tNode, ValueNode<String> uNode) {
        super(PlusFactory.TOKEN, Types.getTimeType(), tNode, uNode);

        this.tNode = tNode;
        this.uNode = uNode;
        this.timeZoneIdNode = new StringConstantNode(DateTimeZone.UTC.getID());
    }

    public TimePlusPeriodImpl(ValueNode<Long> tNode, ValueNode<String> uNode, ValueNode<String> timeZoneIdNode) {
        super(PlusFactory.TOKEN, Types.getTimeType(), tNode, uNode, timeZoneIdNode);

        this.tNode = tNode;
        this.uNode = uNode;
        this.timeZoneIdNode = timeZoneIdNode;
    }

    @Override
    public UpscalableIterator<Long> evaluate(Interval interval, ExecutorContext context) {
        return new UpscalerWrapperIterator<Long>(
                getType().getUpscaler(),
                new AdapterTimeIterator<Object[], Long>(
                        new IntersectIterator(
                                tNode.evaluate(interval, context),
                                uNode.evaluate(interval, context),
                                timeZoneIdNode.evaluate(interval, context)
                        )
                ) {
                    @Override
                    protected Sample<Long> adapt(Sample<Object[]> sample) {
                        Object[] values = sample.getValue();

                        long d = (Long) values[0];

                        String p = (String) values[1];

                        Period u = new Period(p);

                        String timeZoneId = (String) values[2];

                        DateTimeZone timeZone = DateTimeZone.forID(timeZoneId);

                        long result = new DateTime(d).withZone(timeZone).plus(u).getMillis();

                        return sample.copyWithValue(result);
                    }
                }
        );
    }
}
