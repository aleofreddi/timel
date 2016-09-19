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
package net.vleo.timel.time;

import net.vleo.timel.executor.Sample;
import net.vleo.timel.iterator.BufferedTimeIterator;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Andrea Leofreddi
 */
public class SampleMapBackwardIterator<T> extends BufferedTimeIterator<T> {
    private final SampleMap<T> map;

    private final Interval interval;

    private int version;

    private Iterator<Map.Entry<Interval, T>> iterator;

    private long position;

    public SampleMapBackwardIterator(SampleMap<T> map, Interval interval) {
        this.map = map;

        this.version = map.getVersion();

        this.interval = interval;

        this.position = interval.getEnd();
    }

    @Override
    protected Sample<T> concreteNext() {
        // If iterator is invalid fetch a new one from current position
        if(iterator == null || version != map.getVersion()) {
            Interval ceiling = map.ceilingKey(Interval.of(position, position));

            SampleMap<T> headMap;

            if(ceiling != null)
                headMap = map.headMap(ceiling, true);
            else
                headMap = map;

            iterator = headMap.descendingMap().entrySet().iterator();
        }

        for(;;) {
            if(!iterator.hasNext())
                return null;

            Map.Entry<Interval, T> entry = iterator.next();

            Interval sampleInterval = entry.getKey();

            position = sampleInterval.getStart();

            // We reached end of stream
            if(sampleInterval.isBefore(interval))
                return null;

            else if(sampleInterval.overlaps(interval))
                return Sample.of(sampleInterval, entry.getValue());

            // Else we are after the requested interval, let the iterator fast rewind
        }
    }
}
