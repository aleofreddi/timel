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

import net.vleo.timel.impl.parser.tree.*;

/**
 * A {@link AbstractParseTree} visitor.
 *
 * @author Andrea Leofreddi
 */
public interface ParserTreeVisitor<T> {
    default T visit(Assignment assignment) {
        return null;
    }

    default T visit(CompilationUnit compilationUnit) {
        return null;
    }

    default T visit(Declaration node) {
        return null;
    }

    default T visit(DoubleConstant node) {
        return null;
    }

    default T visit(ExplicitCast explicitCast) {
        return null;
    }

    default T visit(FloatConstant node) {
        return null;
    }

    default T visit(FunctionCall node) {
        return null;
    }

    default T visit(IntegerConstant node) {
        return null;
    }

    default T visit(StringConstant node) {
        return null;
    }

    default T visit(TypeId typeId) {
        return null;
    }

    default T visit(TypeSpecifier typeSpecifier) {
        return null;
    }

    default T visit(Variable variable) {
        return null;
    }

    default T visit(ZeroConstant node) {
        return null;
    }
}
