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
package net.vleo.timel.impl.time.periodicity;

import org.joda.time.DateTime;
import org.joda.time.Period;

/**
 * @author Andrea Leofreddi
 */
public class TranslatedPeriodicity implements Periodicity {
    private final Periodicity delegate;

    private final Period phase;

    public TranslatedPeriodicity(Periodicity delegate, Period phase) {
        this.delegate = delegate;
        this.phase = phase;
    }

    private DateTime shift(DateTime t) {
        return t.plus(phase);
    }

    private DateTime unshift(DateTime t) {
        return t.minus(phase);
    }

    @Override
    public DateTime ceil(DateTime timestamp) {
        return shift(delegate.ceil(unshift(timestamp)));
    }

    @Override
    public DateTime floor(DateTime timestamp) {
        return shift(delegate.floor(unshift(timestamp)));
    }

    @Override
    public DateTime next(DateTime timestamp) {
        return shift(delegate.next(unshift(timestamp)));
    }

    @Override
    public DateTime previous(DateTime timestamp) {
        return shift(delegate.previous(unshift(timestamp)));
    }

    @Override
    public boolean matches(DateTime timestamp) {
        return delegate.matches(unshift(timestamp));
    }
}
