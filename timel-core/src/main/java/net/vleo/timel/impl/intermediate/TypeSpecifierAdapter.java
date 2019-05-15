
package net.vleo.timel.impl.intermediate;

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
import net.vleo.timel.ParseException;
import net.vleo.timel.impl.parser.ParserTreeVisitor;
import net.vleo.timel.impl.parser.tree.CompilationUnit;
import net.vleo.timel.impl.parser.tree.FunctionCall;
import net.vleo.timel.impl.parser.tree.*;
import net.vleo.timel.type.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An adapter to visit {@link TypeSpecifier}s and convert them into {@link Type}s using a provided {@link TypeSystem}.
 *
 * @author Andrea Leofreddi
 */
@RequiredArgsConstructor
class TypeSpecifierAdapter implements ParserTreeVisitor<Object> {
    private final TypeSystem typeSystem;

    @Override
    public Object visit(Assignment assignment) {
        return null;
    }

    @Override
    public Object visit(CompilationUnit compilationUnit) {
        return null;
    }

    @Override
    public Object visit(Declaration node) {
        return null;
    }

    @Override
    public Object visit(DoubleConstant node) {
        return null;
    }

    @Override
    public Object visit(ExplicitCast explicitCast) {
        return null;
    }

    @Override
    public Object visit(FloatConstant node) {
        return null;
    }

    @Override
    public Object visit(FunctionCall node) {
        return null;
    }

    @Override
    public Object visit(IntegerConstant node) {
        return null;
    }

    @Override
    public Object visit(StringConstant node) {
        return null;
    }

    @Override
    public Object visit(TypeId typeId) {
        return null;
    }

    @Override
    public Object visit(TypeSpecifier typeSpecifier) {
        String typeId = typeSpecifier.getType();

        List<Object> arguments = typeSpecifier.getTemplateArguments().stream()
                .map(argument -> argument.accept(this))
                .collect(Collectors.toList());

        try {
            return typeSystem.parse(typeId, arguments);
        } catch(ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object visit(Variable variable) {
        return null;
    }

    @Override
    public Object visit(ZeroConstant node) {
        return null;
    }
}
