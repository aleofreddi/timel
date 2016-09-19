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

import java.util.NoSuchElementException;

/**
 * Iterator that will return a single sample.
 *
 * @author Andrea Leofreddi
 */
public final class SingletonTimeIterator<V> implements TimeIterator<V> {
    private Sample<V> value;

    public SingletonTimeIterator(Sample<V> value) {
        this.value = value;
    }

    @Override
    public Sample<V> next() throws NoSuchElementException {
        Sample<V> v = value;

        value = null;

        return v;
    }

    @Override
    public Sample<V> peekNext() throws NoSuchElementException {
        return value;
    }

    @Override
    public boolean hasNext() {
        return value != null;
    }
}
