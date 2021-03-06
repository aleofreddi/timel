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

import net.vleo.timel.annotation.Parameter;
import net.vleo.timel.annotation.FunctionPrototype;
import net.vleo.timel.type.DoubleType;
import net.vleo.timel.type.IntegralDoubleType;
import net.vleo.timel.type.Type;

import java.util.Map;
import java.util.Optional;

/**
 * Integral double div implementation (rhs and lhs are integrals).
 *
 * @author Andrea Leofreddi
 */
@FunctionPrototype(
        name = "/",
        parameters = {
                @Parameter(type = IntegralDoubleType.class),
                @Parameter(type = IntegralDoubleType.class)
        }
)
public class DivIntegralDoubleCombineOperator extends DivDoubleOperator {
    @Override
    public Optional<Type> resolveReturnType(Type proposed, Map<String, Type> variables, Type... argumentTypes) {
        int t = (int) argumentTypes[0].getParameters().get(0), u = (int) argumentTypes[1].getParameters().get(0);

        // This case transforms an integral into an average
        if(t - u == 0)
            return Optional.of(new DoubleType());

        return Optional.of(new IntegralDoubleType(t - u));
    }
}
