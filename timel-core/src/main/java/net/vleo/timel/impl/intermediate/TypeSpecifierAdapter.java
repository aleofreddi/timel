
package net.vleo.timel.impl.intermediate;

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

import lombok.RequiredArgsConstructor;
import net.vleo.timel.ParseException;
import net.vleo.timel.impl.parser.ParserTreeVisitor;
import net.vleo.timel.impl.parser.tree.TypeSpecifier;
import net.vleo.timel.impl.sneaky.ScopedSneakyThrower;
import net.vleo.timel.type.Type;
import net.vleo.timel.type.TypeSystem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An adapter to visit {@link TypeSpecifier}s and convert them into {@link Type}s using a provided {@link TypeSystem}.
 *
 * @author Andrea Leofreddi
 */
@RequiredArgsConstructor
class TypeSpecifierAdapter implements ParserTreeVisitor<Object, ParseException> {
    private final TypeSystem typeSystem;

    @Override
    public Object visit(TypeSpecifier typeSpecifier) throws ParseException {
        String typeId = typeSpecifier.getType();

        List<Object> arguments = typeSpecifier.getTemplateArguments().stream()
                .map(new ScopedSneakyThrower<ParseException>().unchecked(argument -> argument.accept(this)))
                .collect(Collectors.toList());

        try {
            return typeSystem.parse(typeId, arguments);
        } catch(IllegalArgumentException e) {
            throw new ParseException(typeSpecifier.getSourceReference(), "Failed to parse type " + typeId, e);
        }
    }
}
