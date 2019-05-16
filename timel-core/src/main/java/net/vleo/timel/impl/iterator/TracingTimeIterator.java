package net.vleo.timel.impl.iterator;

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

import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.Sample;

import java.util.NoSuchElementException;

/**
 * A passthrough tracing {@link TimeIterator}. This class is useful to debug unexpected behaviors.
 *
 * @author Andrea Leofreddi
 */
public class TracingTimeIterator<V> implements TimeIterator<V> {
    protected final Object reference;
    protected final String id;
    protected final Interval interval;
    protected final TimeIterator<V> delegate;

    public TracingTimeIterator(Object reference, String id, Interval interval, TimeIterator<V> delegate) {
        this.reference = reference;
        this.id = id;
        this.interval = interval;
        this.delegate = delegate;
    }

    @Override
    public Sample<V> next() throws NoSuchElementException {
        return DebugContexts.get().apply(
                reference,
                id,
                interval,
                "next",
                delegate::next
        );
    }

    @Override
    public Sample<V> peekNext() throws NoSuchElementException {
        return DebugContexts.get().apply(
                reference,
                id,
                interval,
                "peekNext",
                delegate::peekNext
        );
    }

    @Override
    public boolean hasNext() {
        return DebugContexts.get().apply(
                reference,
                id,
                interval,
                "hasNext",
                delegate::hasNext
        );
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
