package net.vleo.timel.impl.parser;

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

import net.vleo.timel.impl.parser.tree.AbstractParseTree;

/**
 * Dump a {@link AbstractParseTree} into a string.
 *
 * @author Andrea Leofreddi
 */
public class ParserTreeDumper {
    public String dump(AbstractParseTree tree) {
        StringBuilder sb = new StringBuilder();
        dump(sb, tree, 0);
        return sb.toString();
    }

    private void dump(StringBuilder sb, AbstractParseTree node, int indent) {
        sb.append("  ");
        for(int i = 0; i < indent; i++)
            sb.append("  ");

        sb.append(node.toString()).append(" (").append(node.getClass()).append(')').append('\n');

        for(AbstractParseTree child : node.getChildren())
            dump(sb, child, indent + 1);
    }
}
