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
package net.vleo.timel.impl.expression.compile;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.impl.expression.evaluate.variable.AbstractVariableNode;
import org.matheclipse.parser.client.ast.ASTNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * A class used by {@link CompilerVisitor} to hold the context while transforming the AST.
 *
 * @author Andrea Leofreddi
 */
public class CompilerContextImpl implements CompilerContext {
    private Map<String, AbstractVariableNode<?>> variables = new HashMap<String, AbstractVariableNode<?>>();

    private Stack<ASTNode> sourceTree = new Stack<ASTNode>();

    private FunctionFactory functionFactory;

    public CompilerContextImpl() {
    }

    public AbstractVariableNode<?> getVariableAdapter(String id) {
        return variables.get(id);
    }

    public void putVariableAdapter(String id, AbstractVariableNode<?> adapter) {
        variables.put(id, adapter);
    }

    public FunctionFactory getFunctionFactory() {
        return functionFactory;
    }

    public void setFunctionFactory(FunctionFactory functionFactory) {
        this.functionFactory = functionFactory;
    }

    public Stack<ASTNode> getSourceTreeStack() {
        return sourceTree;
    }
}
