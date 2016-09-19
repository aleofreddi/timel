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
package net.vleo.timel.impl.expression.evaluate.variable;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.executor.variable.Variable;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.IntervalMaps;

import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A variable backed by a TreeMap.
 *
 * @author Andrea Leofreddi
 */
public class TreeMapVariable<V> implements Variable<V> {
    private final TreeMap<Interval, V> values;

    public TreeMapVariable() {
        values = new TreeMap<Interval, V>(IntervalMaps.getIntervalEndComparator());
    }

    public TreeMap<Interval, V> getBackMap() {
        return values;
    }

    @Override
    public TimeIterator<V> readForward(Interval interval, ExecutorContext context) {
        return IntervalMaps.supremum(
                values,
                interval
        );
    }

    @Override
    public TimeIterator<V> readBackward(Interval interval, ExecutorContext context) {
        return IntervalMaps.supremumBackward(
                values,
                interval
        );
    }

    @Override
    public void write(Sample<V> sample, ExecutorContext context) {
        values.put(
                sample.getInterval(),
                sample.getValue()
        );
    }
}
