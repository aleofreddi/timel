package net.vleo.timel.impl.intermediate.tree;

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

import lombok.Getter;
import net.vleo.timel.function.Function;
import net.vleo.timel.impl.intermediate.SyntaxTreeVisitor;
import net.vleo.timel.impl.parser.tree.AbstractParseTree;
import net.vleo.timel.type.Type;

import java.util.List;

/**
 * A function call node.
 *
 * @author Andrea Leofreddi
 */
@Getter
public class FunctionCall extends AbstractSyntaxTree {
    private final Function<Object> function;
    private final List<AbstractSyntaxTree> arguments;

    public FunctionCall(AbstractParseTree reference, Function<Object> function, List<AbstractSyntaxTree> arguments, Type returnType) {
        super(reference, returnType, arguments);
        this.function = function;
        this.arguments = arguments;
    }

    @Override
    public <T> T accept(SyntaxTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
