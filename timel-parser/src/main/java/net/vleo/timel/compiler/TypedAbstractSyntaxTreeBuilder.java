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

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TypedAbstractSyntaxTreeBuilder implements AbstractSyntaxTreeVisitor<AlternativeProducer<TypedAbstractSyntaxTree<?>>> {
    private TypeConverter typeConverter = new TypeConverter();

    private FunctionRegistry functionRegistry = new FunctionRegistry();

    @Override
    public AlternativeProducer<TypedAbstractSyntaxTree<?>> visit(DeclarationNode declarationNode) {
        return null;
    }

    @Override
    public AlternativeProducer<TypedAbstractSyntaxTree<?>> visit(FunctionCallNode functionCallNode) {
        String function = functionCallNode.getFunction();

        List<AlternativeProducer<TypedAbstractSyntaxTree<?>>> typedChildren = functionCallNode.getChildren().stream()
                .map(node -> node.accept(this))
                .collect(toList());

        AlternativeProducer<TypedAbstractSyntaxTree<?>> returnType = functionRegistry.match(function, typedChildren);

        return returnType;
    }

    @Override
    public AlternativeProducer<TypedAbstractSyntaxTree<?>> visit(IntegerConstant integerConstant) {
        return constant(integerConstant, new IntegerType());
    }

    @Override
    public AlternativeProducer<TypedAbstractSyntaxTree<?>> visit(FloatConstant floatConstant) {
        return constant(floatConstant, new FloatType());
    }

    @Override
    public AlternativeProducer<TypedAbstractSyntaxTree<?>> visit(ZeroConstant zeroConstant) {
        return constant(zeroConstant, new ZeroType());
    }

    @SuppressWarnings("unchecked")
    private AlternativeProducer<TypedAbstractSyntaxTree<?>> from(Iterator<?> itor) {
        return new AlternativeProducer(itor);
    }

    private <T extends TypedAbstractSyntaxTree<?>> AlternativeProducer<TypedAbstractSyntaxTree<?>> constant(AbstractSyntaxTree node, Type type) {
        return from(
                typeConverter.implicitConversionsFrom(type).entrySet()
                        .stream()
                        .map(entry -> new TypedAbstractSyntaxTree<>(
                                node,
                                entry.getKey(),
                                entry.getValue()
                        ))
                        .sorted(Comparator.comparing(Weighted::getWeight))
                        .iterator()
        );
    }
}
