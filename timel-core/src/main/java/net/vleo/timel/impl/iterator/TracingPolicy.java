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

import net.vleo.timel.time.Interval;

import java.util.concurrent.Callable;

/**
 * A policy to trace calls.
 *
 * @author Andrea Leofreddi
 */
public interface TracingPolicy {
    <T> T apply(Object node, String id, Interval interval, String method, Callable<T> callable);

    /**
     * Invoked when the trace session is done. Can be used to flush data if needed.
     */
    void close();
}
