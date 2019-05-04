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

import lombok.val;
import net.vleo.timel.annotation.Parameter;
import net.vleo.timel.annotation.Prototype;
import net.vleo.timel.annotation.Returns;
import net.vleo.timel.type.IntegralFloatType;
import net.vleo.timel.type.Type;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Integral float sub implementation.
 *
 * @author Andrea Leofreddi
 */
@Prototype(
        name = "-",
        returns = @Returns(type = IntegralFloatType.class),
        parameters = {
                @Parameter(type = IntegralFloatType.class),
                @Parameter(type = IntegralFloatType.class)
        }
)
public class SubIntegralFloatOperator extends SubFloatOperator {
    @Override
    public Optional<Type> resolveReturnType(Type proposed, Map<String, Type> variables, Type... argumentTypes) {
        val specialisations = Arrays.stream(argumentTypes)
                .map(Type::getParameters)
                .collect(Collectors.toSet());

        return specialisations.size() == 1 ? Optional.of(argumentTypes[0]) : Optional.empty();
    }
}
