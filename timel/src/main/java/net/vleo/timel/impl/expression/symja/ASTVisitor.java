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

import org.matheclipse.parser.client.ast.FloatNode;
import org.matheclipse.parser.client.ast.FractionNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.ast.IntegerNode;
import org.matheclipse.parser.client.ast.StringNode;
import org.matheclipse.parser.client.ast.SymbolNode;

/**
 * Visitor interface to visit a Symja AST.
 *
 * @param <O> Type of the return value (output) of the visit.
 * @param <I> Type of the input data used by the visit. If none, use Void.
 * @author Andrea Leofreddi
 */
public interface ASTVisitor<I, O> {
    O visit(FunctionNode node, I input);

    O visit(SymbolNode node, I input);

    O visit(StringNode node, I input);

    O visit(IntegerNode node, I input);

    O visit(FloatNode node, I input);

    O visit(FractionNode node, I input);
}
