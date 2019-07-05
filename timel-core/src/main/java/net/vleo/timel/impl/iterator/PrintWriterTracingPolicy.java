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

import lombok.RequiredArgsConstructor;
import net.vleo.timel.time.Interval;

import java.io.PrintWriter;
import java.util.concurrent.Callable;

/**
 * Tracing policy that will log to an output stream.
 *
 * @author Andrea Leofreddi
 */
@RequiredArgsConstructor
public class PrintWriterTracingPolicy implements TracingPolicy {
    private final PrintWriter writer;
    private int depth = 0;

    @Override
    public <T> T apply(Object node, String id, Interval interval, String method, Callable<T> callable) {
        if(dump(method))
            writer.println(getIndent() + node.toString() + " " + id + "." + method + " for " + interval + " ?");

        depth++;

        T value;

        try {
            value = callable.call();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        depth--;

        if(dump(method))
            writer.println(getIndent() + node.toString() + " " + id + "." + method + " for " + interval + " -> " + value);

        return value;
    }

    @Override
    public void close() {
        writer.close();
    }

    private String getIndent() {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < depth; i++)
            sb.append("    ");

        return sb.toString();
    }

    private boolean dump(String method) {
        return "next".equals(method) || "hasNext".equals(method);
    }
}
