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
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.StatementNode;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.executor.ExecutionException;
import net.vleo.timel.impl.expression.evaluate.constant.NumericConstantNode;
import net.vleo.timel.impl.expression.evaluate.constant.StringConstantNode;
import net.vleo.timel.impl.expression.evaluate.variable.AbstractVariableNode;
import net.vleo.timel.impl.expression.evaluate.variable.UnknownVariableNode;
import net.vleo.timel.impl.expression.symja.ASTNodes;
import net.vleo.timel.impl.expression.symja.ASTVisitor;
import org.matheclipse.parser.client.ast.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A visitor which transform a parsed {@link ASTNode} tree into an {@link TreeNode} tree.
 *
 * @author Andrea Leofreddi
 */
public class CompilerVisitor implements ASTVisitor<CompilerContext, TreeNode> {
    /**
     * Adapt an {@link ASTNode} into an {@link StatementNode}.
     *
     * @param tree The expression tree to adapt
     * @return The adpated {@link TreeNode}
     * @throws ParseException
     */
    public TreeNode adapt(ASTNode tree, CompilerContext libsContext) throws ParseException {
        TreeNode compiled = ASTNodes.visit(tree, this, libsContext);

        return compiled;
    }

    /**
     * Factory method to instance numeric constant nodes, eventually embedded by
     * an downscaler.
     *
     * @param value
     * @return
     */
    private TreeNode newConstantNode(double value) {
        return new NumericConstantNode(value);
    }

    /**
     * Factory method to instance string constant nodes
     *
     * @param value
     * @return
     */
    private TreeNode newConstantNode(String value) {
        return new StringConstantNode(value);
    }

    /**
     * Factory method to instance a variable node using the context resolver.
     *
     * @param symbol
     * @param context
     * @return
     * @throws ParseException
     */
    private TreeNode newSymbolNode(String symbol, CompilerContext context) throws ParseException {
        CompilerContextImpl c = (CompilerContextImpl) context;

        return c.getFunctionFactory().instance(symbol, Collections.<TreeNode>emptyList(), context);
    }

    /**
     * Factory method to instance a function node.
     *
     * @param function
     * @param arguments
     * @return
     * @throws ParseException
     * @throws ExecutionException
     */
    private TreeNode newFunctionNode(String function, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        CompilerContextImpl c = (CompilerContextImpl) context;

        return c.getFunctionFactory().instance(function, arguments, context);
    }

    /**
     * Evaluate a channel (variable)
     */
    @Override
    public TreeNode visit(SymbolNode node, CompilerContext context) {
        CompilerContextImpl c = (CompilerContextImpl) context;

        try {
            String id = node.getString();

            TreeNode parsed = newFunctionNode(node.getString(), Collections.<TreeNode>emptyList(), context);

            // If parsing is successful return the parsed node
            if(parsed != null)
                return parsed;

            // If there's no argument, check in known variables
            AbstractVariableNode<?> adapter = c.getVariableAdapter(id);

            if(adapter != null)
                // Variable is already known: return it
                return adapter;

            // Return an unknown variable node
            return new UnknownVariableNode(id);
        } catch(ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Evaluate an integer constant
     */
    @Override
    public TreeNode visit(IntegerNode node, CompilerContext context) {
        return newConstantNode(node.doubleValue());
    }

    /**
     * Evaluate an float constant
     */
    @Override
    public TreeNode visit(FloatNode node, CompilerContext context) {
        return newConstantNode(node.doubleValue());
    }

    /**
     * Evaluate an fraction constant
     */
    @Override
    public TreeNode visit(FractionNode node, CompilerContext context) {
        return newConstantNode(node.doubleValue());
    }

    /**
     * Evaluate a string constant
     */
    @Override
    public TreeNode visit(StringNode node, CompilerContext input) {
        return newConstantNode(node.getString());
    }

    /**
     * Adapt a function node.
     *
     * @throws ParseException
     */
    @Override
    public TreeNode visit(FunctionNode node, CompilerContext context) {
        CompilerContextImpl c = (CompilerContextImpl) context;

        try {
            String id = node.get(0).getString();

            // Previsit the node
            ArrayList<String> unparsedArguments = new ArrayList<String>();

            for(int i = 1; i < node.size(); i++)
                unparsedArguments.add(node.get(i).toString());

            c.getFunctionFactory().preVisit(id, unparsedArguments, context);

            // Compile children
            c.getSourceTreeStack().push(node);

            ArrayList<TreeNode> arguments = new ArrayList<TreeNode>();

            for(int i = 1; i < node.size(); i++)
                arguments.add(ASTNodes.visit(node.get(i), this, context));

            c.getSourceTreeStack().pop();

            if(node.get(0) instanceof PatternNode)
                throw new AssertionError("Patterns are not accepted as function identifier. See Symja's documentation");

            if(!(node.get(0) instanceof SymbolNode))
                throw new AssertionError("Only symbols are accepted as function identifier, but was given " + node.getClass());

            TreeNode parsed = newFunctionNode(id, arguments, context);

            // If parsing is successful return the parsed node
            if(parsed != null)
                return parsed;

            // Otherwise raise an error

            // Raise an error because the id is unknown
            throw new ParseException("Unknown " + (arguments.isEmpty() ? "variable" : "function") + " " + id);
        } catch(ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
