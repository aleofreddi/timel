package net.vleo.timel.impl.operator.relational;

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

import net.vleo.timel.annotation.FunctionPrototype;
import net.vleo.timel.annotation.Parameter;
import net.vleo.timel.annotation.Returns;
import net.vleo.timel.type.BooleanType;

/**
 * Inequality operator implementation.
 *
 * @author Andrea Leofreddi
 */
@FunctionPrototype(
        name = "!=",
        returns = @Returns(type = BooleanType.class),
        parameters = {
                @Parameter(variable = "T"),
                @Parameter(variable = "T")
        }
)
public class InequalOperator extends AbstractBinaryPredicate<Object> {
    @Override
    protected boolean apply(Object t, Object u) {
        return !t.equals(u);
    }
}
