
package net.vleo.timel.impl.target;

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
import net.vleo.timel.impl.intermediate.SyntaxTreeVisitor;
import net.vleo.timel.impl.intermediate.tree.*;
import net.vleo.timel.impl.target.tree.AbstractTargetTree;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.variable.Variable;

import java.util.stream.Collectors;

/**
 * A visitor that will adapt an {@link AbstractSyntaxTree} into a {@link AbstractTargetTree}.
 *
 * @author Andrea Leofreddi
 */
public class TargetTreeAdapter implements SyntaxTreeVisitor<AbstractTargetTree> {
    @Override
    public AbstractTargetTree visit(Constant constant) {
        return new net.vleo.timel.impl.target.tree.Constant(
                constant,
                (Upscaler<Object>) constant.getType().getUpscaler(),
                constant.getValue()
        );
    }

    @Override
    public AbstractTargetTree visit(Cast cast) {
        return new net.vleo.timel.impl.target.tree.Cast(
                cast,
                cast.getConversions(),
                cast.getInput().accept(this)
        );
    }

    @Override
    public AbstractTargetTree visit(CompilationUnit compilationUnit) {
        val children = compilationUnit.getChildren().stream()
                .map(child -> child.accept(this))
                .collect(Collectors.toList());

        return new net.vleo.timel.impl.target.tree.CompilationUnit(
                compilationUnit,
                children
        );
    }

    @Override
    public AbstractTargetTree visit(FunctionCall functionCall) {
        val arguments = functionCall.getArguments().stream()
                .map(argument -> argument.accept(this))
                .collect(Collectors.toList());

        return new net.vleo.timel.impl.target.tree.FunctionCall(
                functionCall,
                functionCall.getFunction(),
                arguments
        );
    }

    @Override
    public AbstractTargetTree visit(VariableWriter variableWriter) {
        return new net.vleo.timel.impl.target.tree.VariableWriter(
                variableWriter,
                (Variable<Object>) variableWriter.getVariable(),
                variableWriter.getType(),
                variableWriter.getValue().accept(this)
        );
    }

    @Override
    public AbstractTargetTree visit(VariableReader variableReader) {
        return new net.vleo.timel.impl.target.tree.VariableReader(
                variableReader,
                (Variable<Object>) variableReader.getVariable(),
                variableReader.getType()
        );
    }
}
