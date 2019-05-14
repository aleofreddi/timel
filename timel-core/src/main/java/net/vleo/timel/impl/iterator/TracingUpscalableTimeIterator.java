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

import net.vleo.timel.impl.target.tree.AbstractTargetTree;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.Sample;

/**
 * A passthrough tracing {@link UpscalableIterator}. This class is useful to debug unexpected behaviors.
 *
 * @author Andrea Leofreddi
 */
public class TracingUpscalableTimeIterator<V> extends TracingTimeIterator<V> implements UpscalableIterator<V> {
    public TracingUpscalableTimeIterator(Object reference, String id, Interval interval, UpscalableIterator<V> delegate) {
        super(reference, id, interval, delegate);
    }

    @Override
    public Sample<V> peekUpscaleNext(final Interval interval) {
        return DebugContexts.get().apply(
                reference,
                id,
                this.interval,
                "peekUpscaleNext",
                () -> ((UpscalableIterator<V>) delegate).peekUpscaleNext(interval)
        );
    }
}
