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

import net.vleo.timel.FloatType;
import net.vleo.timel.IntegerType;
import net.vleo.timel.Type;
import net.vleo.timel.ZeroType;
import net.vleo.timel.conversion.TypeConverter;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TypedAbstractSyntaxTreeBuilderCopy implements AbstractSyntaxTreeVisitor<Collection<TypedAbstractSyntaxTree<?>>> {
    private TypeConverter typeConverter = new TypeConverter();

    private FunctionRegistry functionRegistry = new FunctionRegistry();

    @Override
    public Collection<TypedAbstractSyntaxTree<?>> visit(DeclarationNode declarationNode) {
        return null;
    }

    @Override
    public Collection<TypedAbstractSyntaxTree<?>> visit(FunctionCallNode functionCallNode) {
        String function = functionCallNode.getFunction();

        List<Collection<TypedAbstractSyntaxTree<?>>> typedChildren = functionCallNode.getChildren().stream()
                .map(node -> node.accept(this))
                .collect(toList());

        Collection<TypedAbstractSyntaxTree<?>> returnType = functionRegistry.match(function, typedChildren);

        return returnType;
    }

    @Override
    public Collection<TypedAbstractSyntaxTree<?>> visit(IntegerConstant integerConstant) {
        Type type = new IntegerType();

        return typeConverter.implicitConversionsFrom(type).entrySet()
                .stream()
                .map(entry -> new TypedAbstractSyntaxTree<>(
                        integerConstant,
                        entry.getKey(),
                        entry.getValue()
                ))
                .collect(toList());
    }

    @Override
    public Collection<TypedAbstractSyntaxTree<?>> visit(FloatConstant floatConstant) {
        Type type = new FloatType();

        return typeConverter.implicitConversionsFrom(type).entrySet()
                .stream()
                .map(entry -> new TypedAbstractSyntaxTree<>(
                        floatConstant,
                        entry.getKey(),
                        entry.getValue()
                ))
                .collect(toList());
    }

    @Override
    public Collection<TypedAbstractSyntaxTree<?>> visit(ZeroConstant zeroConstant) {
        Type type = new ZeroType();

        return typeConverter.implicitConversionsFrom(type).entrySet()
                .stream()
                .map(entry -> new TypedAbstractSyntaxTree<>(
                        zeroConstant,
                        entry.getKey(),
                        entry.getValue()
                ))
                .collect(toList());
    }
}
