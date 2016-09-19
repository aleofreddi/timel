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
package net.vleo.timel.impl.expression.evaluate.constant;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.UpscalerWrapperIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.IntervalMaps;
import net.vleo.timel.type.ValueType;
import net.vleo.timel.iterator.UpscalableIterator;

import java.util.SortedMap;

/**
 * Base class for constant leaf nodes.
 *
 * @param <V> Type of the value held
 *
 * @author Andrea Leofreddi
 */
public class SimpleConstantNode<V> extends AbstractValueFunction<V> implements ValueNode<V> {
    protected V value;

    protected SimpleConstantNode(String id, ValueType<V> type, V value) {
        super(id, type);

        this.value = value;
    }

    protected SimpleConstantNode(ValueType<V> type, V value) {
        this(value.toString(), type, value);
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public UpscalableIterator<V> evaluate(Interval interval, ExecutorContext context) {
        SortedMap<Interval, V> map = IntervalMaps.of(
                Sample.of(
                        interval,
                        value
                )
        );

        return new UpscalerWrapperIterator<V>(
                getType().getUpscaler(),
                IntervalMaps.iterator(map)
        );
    }

    @Override
    public boolean isConstant() {
        return true;
    }
}
