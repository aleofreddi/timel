package net.vleo.timel.impl.sneaky;

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

import java.util.function.Function;

/**
 * Sneaky throw implementation based on the neat article by 4comprehension.
 * <p>
 * To ensure the sneaked checked exception is not lost, we limit the sneaky throw to the scope of the ScopedSneakyThrower instance.
 *
 * @author Andrea Leofreddi
 * @see <a href="https://4comprehension.com/sneakily-throwing-exceptions-in-lambda-expressions-in-java">Sneaky throw</a>
 */
public class ScopedSneakyThrower<E extends Exception> {
    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    public ScopedSneakyThrower() throws E {
    }

    /**
     * Wraps a throwing function into a non-throwing one.
     * <p>
     * This method can be used to hide throwing lambda
     *
     * @param function Throwing function to uncheck
     * @param <T>      the type of the input to the function
     * @param <R>      the type of the result of the function
     * @return Sneaky throwing function
     */
    public <T, R> Function<T, R> unchecked(ThrowingFunction<T, R, E> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch(Exception ex) {
                rethrow(ex);
                throw new AssertionError();
            }
        };
    }

    @lombok.SneakyThrows
    private void rethrow(Exception t) {
        throw t;
    }
}
