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
package net.vleo.timel.impl.expression.symja;

import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FloatNode;
import org.matheclipse.parser.client.ast.FractionNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.IntegerNode;
import org.matheclipse.parser.client.ast.StringNode;
import org.matheclipse.parser.client.ast.SymbolNode;

/**
 * Static class holding various commodity methods for Symja AST handling.
 *
 * @author Andrea Leofreddi
 */
public class ASTNodes {
    private ASTNodes() {
        throw new AssertionError();
    }

    /**
     * Convenience method to traverse a Symja AST with a given visitor.
     *
     * @param node
     * @param visitor
     * @param input
     * @param <O>     Type of the return value (output) of the visit.
     * @param <I>     Type of the input data used by the visit. If none, use Void.
     * @return Result of the visit.
     */
    public static <I, O> O visit(ASTNode node, ASTVisitor<I, O> visitor, I input) {
        if(node == null)
            return null;

        //ComplexNode, DfpNode, DoubleNode, , , PatternNode, ,

        if(node instanceof FunctionNode)
            return visitor.visit((FunctionNode) node, input);

        if(node instanceof SymbolNode)
            return visitor.visit((SymbolNode) node, input);

        if(node instanceof StringNode)
            return visitor.visit((StringNode) node, input);

        if(node instanceof IntegerNode)
            return visitor.visit((IntegerNode) node, input);

        if(node instanceof FloatNode)
            return visitor.visit((FloatNode) node, input);

        if(node instanceof FractionNode)
            return visitor.visit((FractionNode) node, input);

        throw new AssertionError("Unknown node type " + node.getClass());
    }
}
