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
package net.vleo.timel.impl.expression.evaluate.function.variable;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.tree.MetaTypeNode;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.CompilerContextImpl;
import net.vleo.timel.impl.expression.compile.CompilerVisitor;
import net.vleo.timel.impl.expression.compile.FunctionArgumentParser;
import net.vleo.timel.impl.expression.evaluate.constant.SimpleConstantNode;
import net.vleo.timel.impl.expression.evaluate.constant.StringConstantNode;
import net.vleo.timel.impl.expression.evaluate.variable.AbstractVariableNode;
import net.vleo.timel.impl.expression.evaluate.variable.BoundVariableNode;
import net.vleo.timel.impl.expression.evaluate.variable.UnboundVariableNode;
import net.vleo.timel.impl.expression.evaluate.variable.UnknownVariableNode;
import net.vleo.timel.impl.type.TreeNodes;
import net.vleo.timel.impl.type.UnknownVariableType;
import net.vleo.timel.impl.type.Utils;
import net.vleo.timel.type.MetaType;
import net.vleo.timel.type.Type;
import net.vleo.timel.type.Types;
import net.vleo.timel.type.ValueType;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

import java.util.List;

/**
 * Assignment operator (set function).
 *
 * @author Andrea Leofreddi
 */
public class VariableFactory extends FunctionFactory {
    public static VariableFactory INSTANCE = new VariableFactory();

    @Override
    public void preVisit(String id, List<String> arguments, CompilerContext context) throws ParseException {
        if(RecursiveAssignImpl.TOKEN.equals(id)) {
            // We expect at least 2 arguments
            if(arguments.size() < 2)
                return;

            // We predefine the UnboundVariable to hold any reference to the yet not defined variable
            String variableTypeSrc = arguments.get(0), variableIdSrc = arguments.get(1);

            String variableId;

            ValueType<Object> variableType;

            CompilerContextImpl compilerContextImpl = ((CompilerContextImpl) context);

            // Parse the variable type
            {
                ASTNode obj = new Parser().parse(variableTypeSrc);

                TreeNode tree = new CompilerVisitor().adapt(obj, compilerContextImpl);

                if(!tree.getType().equals(Types.getMetaType()))
                    throw new ParseException(RecursiveAssignImpl.TOKEN + ": expected a type as 1st argument, got " + tree.getType());

                Type nestedType = ((MetaTypeNode) tree).getNestedType();

                if(!Types.getValueType().isAssignableFrom(nestedType))
                    throw new ParseException(RecursiveAssignImpl.TOKEN + ": expected a value type as 1st argument, got " + nestedType);

                variableType = (ValueType<Object>) nestedType;
            }

            // Parse the variable id
            {
                ASTNode obj = new Parser().parse(variableIdSrc);

                TreeNode tree = new CompilerVisitor().adapt(obj, compilerContextImpl);

                if(!tree.getType().equals(UnknownVariableType.get()))
                    throw new ParseException(RecursiveAssignImpl.TOKEN + ": expected a variable name as 2nd argument, got " + tree.getType());

                variableId = ((UnknownVariableNode) tree).getId();
            }

            compilerContextImpl.putVariableAdapter(
                    variableId,
                    new UnboundVariableNode<Object>(
                            variableId,
                            variableType
                    )
            );
        }
    }

    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(AssignImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(AssignImpl.TOKEN, arguments);

            Type symbolType = parser.next().absType();

            if(!UnknownVariableType.get().isAssignableFrom(symbolType)) {
                // Variable already defined: retrieve the variable name and throw an intelligible error
                AbstractVariableNode<?> variableNode = parser.current().chop();

                throw new ParseException("Variable " + variableNode.getId() + " already defined");
            }

            UnknownVariableNode idNode = parser.current().chop();

            String variableId = idNode.getId();

            ValueNode<Object> valueNode = parser.next().withType(Utils.isValuePredicate()).last().chop();

            // Setup a variable adapter to bind the variable and its value
            BoundVariableNode<Object> variableNode = new BoundVariableNode<Object>(
                    variableId,
                    valueNode
            );

            // Store the new variable
            CompilerContextImpl c = (CompilerContextImpl) context;

            c.putVariableAdapter(variableId, variableNode);

            return new AssignImpl<Object>(variableNode);
        } else if(RecursiveAssignImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(RecursiveAssignImpl.TOKEN, arguments);

            MetaTypeNode typeNode = parser.next().withType(Utils.isMetaTypePredicate()).chop();

            UnboundVariableNode<Object> unboundVariableNode = parser.next().withNode(TreeNodes.isInstanceOfPredicate(UnboundVariableNode.class)).chop();

            String variableId = unboundVariableNode.getId();

            ValueNode<Object> valueNode = parser.next().withType(Utils.isValuePredicate()).last().chop();

            // Ensure type is consistent with what has been declared
            if(!typeNode.getNestedType().equals(valueNode.getType()))
                throw new ParseException("Variable " + variableId + " declared as " + typeNode.getNestedType() + " but assigned to a " + valueNode.getType() + " value");

            // Setup a variable adapter to bind the variable and its value
            BoundVariableNode<Object> variableNode = new BoundVariableNode<Object>(
                    variableId,
                    valueNode
            );

            // Store the new variable
            CompilerContextImpl c = (CompilerContextImpl) context;

            c.putVariableAdapter(variableId, variableNode);

            return new AssignImpl<Object>(variableNode);
        }

        return null;
    }
}
