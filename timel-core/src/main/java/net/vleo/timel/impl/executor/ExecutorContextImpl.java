package net.vleo.timel.impl.executor;

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

import lombok.Data;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.impl.iterator.TracingPolicy;
import net.vleo.timel.impl.iterator.TracingTimeIterator;
import net.vleo.timel.impl.iterator.TracingUpscalableTimeIterator;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;

/**
 * Executor context implementation.
 *
 * @author Andrea Leofreddi
 */
@Data
public class ExecutorContextImpl implements ExecutorContext {
    private final TracingPolicy tracingPolicy;

    @Override
    public <V> TimeIterator<V> debug(Object reference, String id, Interval interval, TimeIterator<V> delegate) {
        if(tracingPolicy != null)
            return new TracingTimeIterator<>(reference, id, interval, tracingPolicy, delegate);
        return delegate;
    }

    @Override
    public <V> UpscalableIterator<V> debug(Object reference, String id, Interval interval, UpscalableIterator<V> delegate) {
        if(tracingPolicy != null)
            return new TracingUpscalableTimeIterator<>(reference, id, interval, tracingPolicy, delegate);
        return delegate;
    }
}
