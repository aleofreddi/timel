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

import lombok.EqualsAndHashCode;
import lombok.Value;
import net.vleo.timel.impl.intermediate.SyntaxTreeVisitor;
import net.vleo.timel.impl.parser.tree.AbstractParseTree;
import net.vleo.timel.type.Type;
import net.vleo.timel.variable.Variable;

import java.util.Collections;

/**
 * A write reference to a variable.
 *
 * @author Andrea Leofreddi
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class VariableWriter extends AbstractSyntaxTree {
    private final Variable<?> variable;
    private final AbstractSyntaxTree value;

    public VariableWriter(AbstractParseTree reference, Type type, Variable<?> variable, AbstractSyntaxTree value) {
        super(reference, type, Collections.singletonList(value));
        this.variable = variable;
        this.value = value;
    }

    @Override
    public <T> T accept(SyntaxTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
