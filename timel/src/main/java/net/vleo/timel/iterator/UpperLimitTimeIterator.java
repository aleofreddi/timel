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
package net.vleo.timel.iterator;

import net.vleo.timel.executor.Sample;

/**
 * An iterator that will end the iteration when an upper temporal limit is reached.
 *
 * @author Andrea Leofreddi
 */
public class UpperLimitTimeIterator<V> extends BufferedTimeIterator<V> {
    private final long limit;

    private final TimeIterator<V> delegate;

    public UpperLimitTimeIterator(long limit, TimeIterator<V> delegate) {
        this.limit = limit;
        this.delegate = delegate;
    }

    @Override
    protected Sample<V> concreteNext() {
        if(!delegate.hasNext())
            return null;

        Sample<V> value = delegate.next();

        if(value.getInterval().getStart() >= limit)
            // We are beyond the limit
            return null;

        return value;
    }
}
