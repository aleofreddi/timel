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

import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.time.Interval;

import java.util.concurrent.Callable;

/**
 * Context to support the evaluation debugging.
 *
 * @author Andrea Leofreddi
 */
public class DebugContext {
    private int depth = 0;

    private String getIndent() {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < depth; i++)
            sb.append("    ");

        return sb.toString();
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean dump(String method) {
        return "next".equals(method) || "hasNext".equals(method);
    }

    public <T> T apply(ValueNode<?> valueNode, String id, Interval interval, String method, Callable<T> callable) {
        if(dump(method))
            System.out.println(getIndent() + valueNode.toString() + " " + id + "." + method + " for " + interval + " ?");

        depth++;

        T value;

        try {
            value = callable.call();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        depth--;

        if(dump(method))
            System.out.println(getIndent() + valueNode.toString() + " " + id + "." + method + " for " + interval + " -> " + value);

        return value;
    }
}
