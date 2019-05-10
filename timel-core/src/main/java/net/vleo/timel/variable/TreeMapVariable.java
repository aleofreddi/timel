package net.vleo.timel.variable;

/*-
 * #%L
 * TimEL core
 * %%
 * Copyright (C) 2015 - 2019 Andrea Leofreddi
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.IntervalMaps;
import net.vleo.timel.time.Sample;
import net.vleo.timel.variable.Variable;

import java.util.TreeMap;

/**
 * A variable backed by a TreeMap.
 *
 * @author Andrea Leofreddi
 */
public class TreeMapVariable<V> implements Variable<V> {
    private final TreeMap<Interval, V> values;

    public TreeMapVariable() {
        values = new TreeMap<>(IntervalMaps.getIntervalEndComparator());
    }

    public TreeMap<Interval, V> getTreeMap() {
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
