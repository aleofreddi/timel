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
package net.vleo.timel.impl.expression.utils;

import java.util.Iterator;

/**
 * Replacement of Guava's Joiner class.
 *
 * This is to avoid dependency on Guava.
 *
 * @author Andrea Leofreddi
 */
public class Joiner {
    private String separator;

    public static Joiner on(String separator) {
        Joiner j = new Joiner();

        j.separator = separator;

        return j;
    }

    public String join(Iterable<?> parts) {
        StringBuilder sb = new StringBuilder();

        for(Iterator<?> i = parts.iterator(); i.hasNext(); ) {
            Object v = i.next();

            if(sb.length() != 0)
                sb.append(separator);

            sb.append(v);
        }

        return sb.toString();
    }

    public String join(Object[] parts) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < parts.length; i++) {
            Object v = (parts[i] != null ? parts[i].toString() : "null");

            if(sb.length() != 0)
                sb.append(separator);

            sb.append(v);
        }

        return sb.toString();
    }

    public String join(Object first, Object second, Object... rest) {
        Object[] values = new Object[rest.length + 2];

        values[0] = first;
        values[1] = second;

        for(int i = 0; i < rest.length; i++)
            values[i + 2] = rest[i];

        return join(values);
    }

    private Joiner() {

    }
}
