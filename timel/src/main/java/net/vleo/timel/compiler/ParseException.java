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
package net.vleo.timel.compiler;

import net.vleo.timel.compiler.tree.TreeNode;

/**
 * A base exception representing parse exceptions.
 *
 * @author Andrea Leofreddi
 */
public class ParseException extends Exception {
    private static final long serialVersionUID = 109454679993455183L;

    public ParseException() {
    }

    public ParseException(String arg0) {
        super(arg0);
    }

    public ParseException(Throwable arg0) {
        super(arg0);
    }

    public ParseException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public ParseException(TreeNode treeNode, String message) {
        /// FIXME: would be nice to have the position here!
        super(treeNode.toString() + ": " + message);
    }
}
