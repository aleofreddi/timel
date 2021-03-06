
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
import lombok.val;
import net.vleo.timel.ParseException;
import net.vleo.timel.conversion.Conversion;
import net.vleo.timel.function.FunctionRegistry;
import net.vleo.timel.impl.intermediate.tree.*;
import net.vleo.timel.impl.parser.ParserTreeVisitor;
import net.vleo.timel.impl.parser.tree.CompilationUnit;
import net.vleo.timel.impl.parser.tree.FunctionCall;
import net.vleo.timel.impl.parser.tree.*;
import net.vleo.timel.impl.sneaky.ScopedSneakyThrower;
import net.vleo.timel.type.*;
import net.vleo.timel.variable.VariableRegistry;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An adapter that builds a {@link AbstractSyntaxTree} from an {@link AbstractParseTree}.
 * <p>
 * This class will take care of type deduction, as well as variable scoping.
 *
 * @author Andrea Leofreddi
 */
@RequiredArgsConstructor
public class SyntaxTreeAdapter implements ParserTreeVisitor<AbstractSyntaxTree, ParseException> {
    private final VariableRegistry variableRegistry;
    private final FunctionRegistry functionRegistry;

    private final Set<String> newVariables = new HashSet<>();
    private final Map<String, VariableWriter> variableWriterMap = new HashMap<>();

    @Override
    public AbstractSyntaxTree visit(Assignment assignment) throws ParseException {
        return withParseExceptionReference(assignment.getSourceReference(), () -> {
            String id = assignment.getVariable().getId();
            AbstractSyntaxTree rhs = assignment.getValue().accept(this);

            if(variableWriterMap.get(id) != null || newVariables.contains(id))
                throw new ParseException("Variable " + id + " already declared");

            net.vleo.timel.variable.Variable<?> registryVariable = variableRegistry.getVariable(id);

            if(registryVariable == null) {
                try {
                    registryVariable = variableRegistry.newVariable(id, rhs.getType());
                } catch(IllegalArgumentException e) {
                    throw new ParseException("Invalid variable", e);
                }
            } else {
                // CHECK TYPE HERE! FIXME
            }

            val variableWriter = new VariableWriter(
                    assignment,
                    rhs.getType(),
                    registryVariable,
                    rhs
            );

            variableWriterMap.put(id, variableWriter);
            return variableWriter;
        });
    }

    @Override
    public AbstractSyntaxTree visit(FunctionCall functionCallNode) throws ParseException {
        return withParseExceptionReference(functionCallNode.getSourceReference(), () -> {
            String function = functionCallNode.getFunction();

            List<AbstractSyntaxTree> children = functionCallNode.getChildren().stream()
                    .map(new ScopedSneakyThrower<ParseException>().unchecked(parserTree -> parserTree.accept(this)))
                    .collect(Collectors.toList());

            try {
                return functionRegistry.lookup(functionCallNode, function, children);
            } catch(IllegalArgumentException e) {
                throw new ParseException("Failed to lookup function " + function + ": " + e.getMessage());
            }
        });
    }

    @Override
    public AbstractSyntaxTree visit(IntegerConstant integerConstant) {
        return constant(integerConstant, new IntegerType());
    }

    @Override
    public AbstractSyntaxTree visit(FloatConstant floatConstant) {
        return constant(floatConstant, new FloatType());
    }

    @Override
    public AbstractSyntaxTree visit(CompilationUnit compilationUnit) throws ParseException {
        return withParseExceptionReference(compilationUnit.getSourceReference(), () -> {
            newVariables.clear();
            return new net.vleo.timel.impl.intermediate.tree.CompilationUnit(
                    compilationUnit,
                    compilationUnit.getChildren().stream()
                            .map(new ScopedSneakyThrower<ParseException>().unchecked(child -> child.accept(this)))
                            .collect(Collectors.toList())
            );
        });
    }

    @Override
    public AbstractSyntaxTree visit(DoubleConstant doubleConstant) {
        return constant(doubleConstant, new DoubleType());
    }

    @Override
    public AbstractSyntaxTree visit(ExplicitCast explicitCast) throws ParseException {
        return withParseExceptionReference(explicitCast.getSourceReference(), () -> {
            Type targetType = explicitCast.getType().accept(this).getType();

            AbstractSyntaxTree value = explicitCast.getValue().accept(this);
            Type sourceType = value.getType();
            val conversionResult = functionRegistry.getTypeSystem().getConcretePath(false, sourceType, targetType);

            if(conversionResult == null || !targetType.equals(conversionResult.getResultType()))
                throw new ParseException("Cannot convert " + sourceType + " to " + targetType);

            List<Conversion<Object, Object>> conversions = conversionResult.getConversions();

            return new Cast(
                    explicitCast,
                    value,
                    targetType,
                    conversions
            );
        });
    }

    @Override
    public AbstractSyntaxTree visit(StringConstant stringConstant) {
        return constant(stringConstant, new StringType());
    }

    @Override
    public AbstractSyntaxTree visit(TypeId typeId) throws ParseException {
        Type type = typeId.getChildren().get(0).accept(this).getType();

        return new Constant(typeId, new StringType(), type.toString());
    }

    @Override
    public AbstractSyntaxTree visit(TypeSpecifier typeSpecifier) throws ParseException {
        return withParseExceptionReference(typeSpecifier.getSourceReference(), () -> {
            Type<?> type = (Type<?>) new TypeSpecifierAdapter(functionRegistry.getTypeSystem()).visit(typeSpecifier);

            // We return an anonymous AbstractSyntaxTree to carry the type. It will be unwrapped by the parent call
            return new AbstractSyntaxTree(typeSpecifier, type, Collections.emptyList()) {
                @Override
                public <T> T accept(SyntaxTreeVisitor<T> visitor) {
                    return null;
                }
            };
        });
    }

    @Override
    public AbstractSyntaxTree visit(Variable variable) throws ParseException {
        return withParseExceptionReference(variable.getSourceReference(), () -> {
            String id = variable.getId();
            VariableWriter variableWriter = variableWriterMap.get(id);

            if(variableWriter == null || newVariables.contains(id)) {
                val externalVariable = variableRegistry.getVariable(id);

                if(externalVariable == null)
                    throw new ParseException("Undefined variable " + id);

                return new VariableReader(
                        variable,
                        variableRegistry.getType(id),
                        externalVariable
                );
            }

            return variableWriter;
        });
    }

    @Override
    public AbstractSyntaxTree visit(ZeroConstant zeroConstant) {
        return constant(zeroConstant, new ZeroType());
    }

    private AbstractSyntaxTree constant(AbstractConstant constant, Type type) {
        return new Constant(constant, type);
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T, E extends Exception> {
        T get() throws E;
    }

    private <R> R withParseExceptionReference(SourceReference sourceReference, ThrowingSupplier<R, ParseException> function) throws ParseException {
        try {
            return function.get();
        } catch(ParseException e) {
            if(e.getSourceReference() == null)
                e.setSourceReference(sourceReference);
            throw e;
        }
    }
}
