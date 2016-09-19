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
package net.vleo.timel.executor;

import net.vleo.timel.time.Interval;

/**
 * Executor builder for programs.
 *
 * @author Andrea Leofreddi
 */
public interface ProgramExecutorBuilder extends ExecutorBuilder<ProgramExecutorBuilder> {
     /**
     * Execute the program for the given interval.
     *
     * @param interval  The evaluation interval
     */
    void executeFor(Interval interval);
}
