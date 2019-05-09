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
import net.vleo.timel.cast.AbstractTypeConversion;
import net.vleo.timel.impl.intermediate.SyntaxTreeVisitor;
import net.vleo.timel.impl.parser.tree.AbstractParseTree;
import net.vleo.timel.type.Type;

import java.util.Collections;
import java.util.List;

/**
 * A cast node.
 *
 * @author Andrea Leofreddi
 */
@Getter
public class Cast extends AbstractSyntaxTree {
    private final AbstractSyntaxTree input;
    private final List<AbstractTypeConversion> conversions;

    public Cast(AbstractParseTree reference, AbstractSyntaxTree input, Type type, List<AbstractTypeConversion> conversions) {
        super(reference, type, Collections.singletonList(input));
        this.input = input;
        this.conversions = conversions;
    }

    @Override
    public <T> T accept(SyntaxTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
