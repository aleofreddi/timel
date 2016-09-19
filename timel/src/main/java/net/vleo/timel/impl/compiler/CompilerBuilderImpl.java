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
package net.vleo.timel.impl.compiler;

import net.vleo.timel.compiler.CompilerBuilder;
import net.vleo.timel.compiler.Expression;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.Program;
import net.vleo.timel.compiler.factory.ChainedFunctionFactory;
import net.vleo.timel.compiler.factory.DefaultFunctionFactory;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.tree.StatementNode;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.evaluate.ExpressionImpl;
import net.vleo.timel.impl.evaluate.ProgramImpl;
import net.vleo.timel.impl.expression.compile.CompilerContextImpl;
import net.vleo.timel.impl.expression.compile.CompilerVisitor;
import net.vleo.timel.impl.expression.evaluate.variable.UnboundVariableNode;
import net.vleo.timel.type.Types;
import net.vleo.timel.type.ValueType;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of compiler builder.
 *
 * @author Andrea Leofreddi
 */
public class CompilerBuilderImpl implements CompilerBuilder {
    private final String source;

    private FunctionFactory functionFactory = DefaultFunctionFactory.get();

    private HashMap<String, ValueType<?>> declaredVariables = new HashMap<String, ValueType<?>>();

    private TreeNode compile() throws ParseException {
        CompilerContextImpl context = new CompilerContextImpl();

        context.setFunctionFactory(functionFactory);

        for(Map.Entry<String, ValueType<?>> entry : declaredVariables.entrySet()) {
            String id = entry.getKey();

            context.putVariableAdapter(
                    id,
                    new UnboundVariableNode<Object>(
                            id,
                            (ValueType<Object>) entry.getValue()
                    )
            );
        }

        // Parse the source using Symja's parser
        ASTNode obj = new Parser().parse(source);

        return new CompilerVisitor().adapt(
                obj,
                context
        );
    }

    public CompilerBuilderImpl(String source) {
        this.source = source;
    }

    @Override
    public CompilerBuilder withOption(String key, Object value) {
        throw new IllegalArgumentException("Unknown option " + key);

        //return this;
    }

    @Override
    public CompilerBuilder withFunctionFactory(FunctionFactory functionFactory) {
        this.functionFactory = functionFactory;

        return this;
    }

    @Override
    public CompilerBuilder declareVariable(String id, ValueType<?> type) {
        declaredVariables.put(id, type);

        return this;
    }

    @Override
    public <T> Expression<T> compileExpression(ValueType<T> type) throws ParseException {
        TreeNode compiled = compile();

        if(!Types.getValueType().isAssignableFrom(compiled.getType()))
            throw new ParseException(source + " is not a valid expression");

        if(!type.isAssignableFrom(compiled.getType()))
            throw new ParseException(source + " return type is " + compiled.getType() + " while expected " + type);

        return new ExpressionImpl<T>((ValueNode<T>) compiled);
    }

    @Override
    public Program compileProgram() throws ParseException {
        TreeNode compiled = compile();

        if(!Types.getStatementType().isAssignableFrom(compiled.getType()))
            throw new ParseException(source + " is not a valid program");

        return new ProgramImpl((StatementNode) compiled);
    }
}
