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
package net.vleo.timel.impl.iterator;

/**
 * A class to hold debug contexts.
 *
 * @author Andrea Leofreddi
 */
public class DebugContexts {
    private static ThreadLocal<DebugContext> localContext = new ThreadLocal<DebugContext>();

    public static DebugContext get() {
        DebugContext context = localContext.get();

        if(context == null) {
            context = new DebugContext();

            localContext.set(context);
        }

        return context;
    }
}
