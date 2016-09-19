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
import net.vleo.timel.impl.downscaler.MaxDownscaler;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;

/**
 * Extract the maximum of a value over time.
 *
 * @author Andrea Leofreddi
 */
public class MaxImpl<V extends Comparable<V>> extends SimpleDownscalerImpl<V> {
    public static final String TOKEN = "Max";

    private final ValueNode<V> valueNode;

    public MaxImpl(ValueNode<V> valueNode, ValueNode<Interval> periodicityNode) throws ParseException {
        super(TOKEN, valueNode.getType(), valueNode, periodicityNode);

        this.valueNode = valueNode;
    }

    public MaxImpl(ValueNode<V> valueNode) throws ParseException {
        super(TOKEN, valueNode.getType(), valueNode);

        this.valueNode = valueNode;
    }

    @Override
    protected TimeIterator<V> getDownscaler(Interval interval, ExecutorContext context) {
        return new MaxDownscaler<V>(
                valueNode.evaluate(interval, context),
                interval
        );
    }
}
