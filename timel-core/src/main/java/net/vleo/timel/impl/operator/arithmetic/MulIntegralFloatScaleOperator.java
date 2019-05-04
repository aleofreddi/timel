package net.vleo.timel.impl.operator.arithmetic;

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

import net.vleo.timel.annotation.*;
import net.vleo.timel.type.FloatType;
import net.vleo.timel.type.IntegralFloatType;

/**
 * Integral float mul implementation (lhs or rhs is integral).
 *
 * @author Andrea Leofreddi
 */
@Prototypes({
        @Prototype(
                name = "*",
                returns = @Returns(variable = "T"),
                parameters = {
                        @Parameter(variable = "T"),
                        @Parameter(type = FloatType.class)
                },
                constraints = {
                        @Constraint(variable = "T", template = IntegralFloatType.class)
                }
        ),
        @Prototype(
                name = "*",
                returns = @Returns(variable = "T"),
                parameters = {
                        @Parameter(type = FloatType.class),
                        @Parameter(variable = "T")
                },
                constraints = {
                        @Constraint(variable = "T", template = IntegralFloatType.class)
                }
        )
})
public class MulIntegralFloatScaleOperator extends MulFloatOperator {
}
