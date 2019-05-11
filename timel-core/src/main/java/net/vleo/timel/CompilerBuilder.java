package net.vleo.timel;

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

import net.vleo.timel.function.Function;
import net.vleo.timel.type.Type;
import net.vleo.timel.variable.Variable;
import net.vleo.timel.variable.VariableFactory;

/**
 * Builder for compiler.
 *
 * @author Andrea Leofreddi
 */
@SuppressWarnings("unused")
public interface CompilerBuilder {
    /**
     * Enable a compiler option with the given value.
     *
     * @param key   Option name
     * @param value Option value
     * @return Compiler builder
     */
    CompilerBuilder withOption(String key, Object value);

    /**
     * Declare a {@link Function} so that it's accessible from the expression.
     *
     * @param function Function instance.
     * @return Compiler builder
     */
    CompilerBuilder withFunction(Function<?> function);

    /**
     * Adds a {@link VariableFactory} so that the compiler can instance new variables when needed.
     *
     * @param variableFactory Variable factory
     * @return Compiler builder
     */
    CompilerBuilder withVariableFactory(VariableFactory variableFactory);

    /**
     * Declare a {@link Variable} so that it's accessible from the expression.
     *
     * @param id       Variable id
     * @param type     Variable intermediate
     * @param variable Variable implementation
     * @return Compiler builder
     */
    CompilerBuilder withVariable(String id, Type type, Variable<?> variable);

    /**
     * Compile the expression with an expected result type.
     *
     * @param type Expected return type
     * @return The compiled expression
     * @throws ParseException When the parsing fails or the expression cannot be converted to type.
     */
    <T> Expression<T> compile(Type<T> type) throws ParseException;

    /**
     * Compile the expression.
     *
     * @return The compiled expression
     */
    Expression<?> compile() throws ParseException;
}
