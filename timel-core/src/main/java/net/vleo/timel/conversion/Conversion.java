package net.vleo.timel.conversion;

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

import net.vleo.timel.type.Type;

import java.util.Optional;

/**
 * Abstract type conversion.
 *
 * @author Andrea Leofreddi
 */
public interface Conversion<S, T> {
    /**
     * Helps the compiler to resolve the return type for a possible conversion. Note that this method is always called with a concrete source type (either
     * a non-template, or a specialised one) and a target template type which can be either a non-template or an unbounded template.
     * <p>
     * This method is invoked by the compiler due to one of the following conditions:
     *
     * <ul>
     * <li>The source type is a specialized template, so the compiler needs to ensure that it can be effectively casted</li>
     * <li>The target type is a unbounded template, so the compiler needs to resolve a specialisation for it</li>
     * </ul>
     *
     * <p>
     * The default implementation will handle the aforementioned cases as follows:
     *
     * <ul>
     * <li>When the source is a specialised template and the target is an unbounded template, specialise the target using the same parameters as the source</li>
     * <li>When the source is a non-template, and the target is an unbounded template, fail (return empty)</li>
     * <li>No match in any other case</li>
     * </ul>
     *
     * @param source The source type. It is ensured to be a concrete type
     * @param target The target type. Could be either a non-template or an unbounced template
     * @return The specialized return type, or empty when no match
     */
    default Optional<Type<? extends T>> resolveReturnType(Type<? extends S> source, Type<? extends T> target) {
        assert source.isConcrete();
        if(source.isSpecializedTemplate()) {
            if(target.isUnboundTemplate())
                // Copy the source parametrization into the target
                return Optional.of(target.specialize(source.getParameters().toArray()));
        } else if(target.isUnboundTemplate())
            // When source is concrete and target is not, we can't do anything
            return Optional.empty();

        // Else we accept the target (which is a basic type)
        assert target.isConcrete();
        return Optional.of(target);
    }

    /**
     * Applies the conversion
     *
     * @param value Source value
     * @return Converted value
     */
    T apply(S value);
}
