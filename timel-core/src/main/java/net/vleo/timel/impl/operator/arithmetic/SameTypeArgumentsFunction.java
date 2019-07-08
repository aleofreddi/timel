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
import net.vleo.timel.function.Function;
import net.vleo.timel.type.Type;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A function that resolves return type from its same type arguments.
 *
 * @param <T> Return value Java type
 * @author Andrea Leofreddi
 */
public interface SameTypeArgumentsFunction<T> extends Function<T> {
    @Override
    default Optional<Type> resolveReturnType(Type proposed, Map<String, Type> variables, Type... argumentTypes) {
        val specialisations = Arrays.stream(argumentTypes)
                .map(Type::getParameters)
                .collect(Collectors.toSet());

        return specialisations.size() == 1 ? Optional.of(argumentTypes[0]) : Optional.empty();
    }
}
