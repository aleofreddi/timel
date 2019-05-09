package net.vleo.timel.impl.parser.tree;

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

import lombok.Data;
import net.vleo.timel.impl.parser.ParserTreeVisitor;

import java.util.Collections;

/**
 * Variable declaration.
 *
 * @author Andrea Leofreddi
 */
@Data
public class Declaration extends AbstractParseTree {
    private final String identifier;

    public Declaration(String identifier, AbstractParseTree value) {
        super(Collections.singletonList(value));
        this.identifier = identifier;
    }

    @Override
    public <T> T accept(ParserTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
