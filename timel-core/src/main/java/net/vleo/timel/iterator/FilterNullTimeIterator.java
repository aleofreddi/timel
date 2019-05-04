package net.vleo.timel.iterator;

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

import net.vleo.timel.time.Sample;

/**
 * A TimeIterator that will filter out null payloads.
 *
 * @author Andrea Leofreddi
 */
public class FilterNullTimeIterator<V> extends BufferedTimeIterator<V> {
    private final TimeIterator<V> delegate;

    public FilterNullTimeIterator(TimeIterator<V> delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Sample<V> concreteNext() {
        while(delegate.hasNext() && delegate.peekNext().getValue() == null)
            delegate.next();

        if(delegate.hasNext())
            return delegate.next();

        return null;
    }
}
