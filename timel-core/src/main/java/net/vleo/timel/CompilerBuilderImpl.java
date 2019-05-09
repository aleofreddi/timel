package net.vleo.timel;

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

import lombok.val;
import net.vleo.timel.cast.StandardConversions;
import net.vleo.timel.function.Function;
import net.vleo.timel.function.FunctionRegistry;
import net.vleo.timel.function.StandardFunctions;
import net.vleo.timel.impl.intermediate.SyntaxTreeAdapter;
import net.vleo.timel.impl.intermediate.SyntaxTreeDumper;
import net.vleo.timel.impl.intermediate.tree.AbstractSyntaxTree;
import net.vleo.timel.impl.parser.Parser;
import net.vleo.timel.impl.parser.ParserTreeDumper;
import net.vleo.timel.impl.parser.tree.AbstractParseTree;
import net.vleo.timel.impl.target.TargetTreeAdapter;
import net.vleo.timel.impl.target.TargetTreeDumper;
import net.vleo.timel.impl.target.tree.AbstractTargetTree;
import net.vleo.timel.tuple.Pair;
import net.vleo.timel.type.Type;
import net.vleo.timel.type.TypeSystem;
import net.vleo.timel.variable.Variable;
import net.vleo.timel.variable.VariableFactory;
import net.vleo.timel.variable.VariableRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Implementation of {@link CompilerBuilder}.
 *
 * @author Andrea Leofreddi
 */
class CompilerBuilderImpl implements CompilerBuilder {
    private final String source;
    private boolean dumpTrees = false;

    private VariableFactory variableFactory;
    private Map<String, Pair<Type, Variable<?>>> variables;
    private HashSet<Function<?>> functions;

    public CompilerBuilderImpl(String source) {
        this.source = source;
        this.variables = new HashMap<>();
        this.functions = new HashSet<>();
    }

    private CompilerBuilderImpl(CompilerBuilderImpl copy) {
        this.source = copy.source;
        this.variableFactory = copy.variableFactory;
        this.variables = new HashMap<>(copy.variables);
        this.functions = new HashSet<>(copy.functions);
    }

    @Override
    public CompilerBuilder withOption(String key, Object value) {
        throw new IllegalArgumentException("Unknown option " + key);
    }

    @Override
    public CompilerBuilder withFunction(Function<?> function) {
        CompilerBuilderImpl next = new CompilerBuilderImpl(this);
        next.functions.add(function);
        return next;
    }

    @Override
    public CompilerBuilder withVariableFactory(VariableFactory variableFactory) {
        CompilerBuilderImpl next = new CompilerBuilderImpl(this);
        next.variableFactory = variableFactory;
        return next;
    }

    @Override
    public CompilerBuilder withVariable(String id, Type type, Variable<?> variable) {
        CompilerBuilderImpl next = new CompilerBuilderImpl(this);
        next.variables.put(id, new Pair<>(type, variable));
        return next;
    }

    @Override
    public <T> Expression<T> compile(Type<T> expected) throws ParseException {
        val variableRegistry = setupVariableRegistry(variableFactory, variables);
        val functionRegistry = setupFunctionRegistry();

        val targetTree = compile("(" + expected.toString() + ")(" + source + ")", variableRegistry, functionRegistry);

        return new ExpressionImpl<>(targetTree);
    }

    @Override
    public Expression<?> compile() throws ParseException {
        val variableRegistry = setupVariableRegistry(variableFactory, variables);
        val functionRegistry = setupFunctionRegistry();

        val targetTree = compile(source, variableRegistry, functionRegistry);

        return new ExpressionImpl(targetTree);
    }

    private VariableRegistry setupVariableRegistry(VariableFactory variableFactory, Map<String, Pair<Type, Variable<?>>> variables) {
        VariableRegistry variableRegistry = new VariableRegistry();
        variableRegistry.setVariableFactory(variableFactory);

        for(Map.Entry<String, Pair<Type, Variable<?>>> entry : variables.entrySet())
            variableRegistry.addVariable(entry.getKey(), entry.getValue().getFirst(), entry.getValue().getSecond());

        return variableRegistry;
    }

    private FunctionRegistry setupFunctionRegistry() {
        final TypeSystem TYPE_SYSTEM = new TypeSystem(StandardConversions.STANDARD_COERCIONS, StandardConversions.STANDARD_CASTS);
        FunctionRegistry functionRegistry = new FunctionRegistry(TYPE_SYSTEM);
        functionRegistry.addAll(StandardFunctions.STANDARD_FUNCTIONS);

        return functionRegistry;
    }

    private AbstractTargetTree compile(String source, VariableRegistry variableRegistry, FunctionRegistry functionRegistry) throws ParseException {
        val parseTree = new Parser().parse(source);
        traceTree(parseTree);
        val intTree = parseTree.accept(new SyntaxTreeAdapter(variableRegistry, functionRegistry));
        traceTree(intTree);
        val targetTree = intTree.accept(new TargetTreeAdapter());
        traceTree(targetTree);

        return targetTree;
    }

    private void traceTree(AbstractParseTree parseTree) {
        if(dumpTrees)
            System.out.println("Parse {\n" + new ParserTreeDumper().dump(parseTree) + "}\n");
    }

    private void traceTree(AbstractSyntaxTree syntaxTree) {
        if(dumpTrees)
            System.out.println("Intermediate {\n" + new SyntaxTreeDumper().dump(syntaxTree) + "}\n");
    }

    private void traceTree(AbstractTargetTree targetTree) {
        if(dumpTrees)
            System.out.println("Target {\n" + new TargetTreeDumper().dump(targetTree) + "}\n");
    }
}
