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
package net.vleo.timel.impl.expression.compile;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.impl.iterator.DebugTimeIterator;
import net.vleo.timel.impl.iterator.DebugUpscalableTimeIterator;

/**
 * @author Andrea Leofreddi
 */
public class DebugFactory {
    public interface DebugBuilder {
        <V> TimeIterator<V> build(String id, Interval interval, TimeIterator<V> iterator);

        <V> UpscalableIterator<V> build(String id, Interval interval, UpscalableIterator<V> iterator);
    }

    public static DebugBuilder of(final ValueNode<?> valueNode, ExecutorContext context) {
        return noDebugBuilder(valueNode, context);
        //return debugBuilder(valueNode, context);
    }

    public static DebugBuilder debugBuilder(final ValueNode<?> valueNode, ExecutorContext context) {
        return new DebugBuilder() {
            @Override
            public <V> TimeIterator<V> build(String id, Interval interval, TimeIterator<V> iterator) {
                return new DebugTimeIterator<V>(valueNode, id, interval, iterator);
            }

            @Override
            public <V> UpscalableIterator<V> build(String id, Interval interval, UpscalableIterator<V> iterator) {
                return new DebugUpscalableTimeIterator<V>(valueNode, id, interval, iterator);
            }
        };
    }

    public static DebugBuilder noDebugBuilder(final ValueNode<?> valueNode, ExecutorContext context) {
        return new DebugBuilder() {
            @Override
            public <V> TimeIterator<V> build(String id, Interval interval, TimeIterator<V> iterator) {
                return iterator;
            }

            @Override
            public <V> UpscalableIterator<V> build(String id, Interval interval, UpscalableIterator<V> iterator) {
                return iterator;
            }
        };
    }
}
