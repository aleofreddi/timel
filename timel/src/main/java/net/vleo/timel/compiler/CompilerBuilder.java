/*
 * Copyright 2014-2016 Andrea Leofreddi
 *
 * This file is part of TimEL.
 *
 * TimEL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TimEL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TimEL.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.vleo.timel.compiler;

import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.type.ValueType;

/**
 * Builder for compiler.
 *
 * @author Andrea Leofreddi
 */
@SuppressWarnings("unused")
public interface CompilerBuilder {
    /**
     * Enable the compiler option key with the given value.
     *
     * @param key  Option name
     * @param value  Option value
     * @return The builder
     */
    CompilerBuilder withOption(String key, Object value);

    /**
     * Enable the selection of a specific type to represent numeric values.
     *
     * By default the double type is selected.
     *
     * @param clazz  Numeric class to use
     * @return The builder
     */
    //CompilerBuilder withNumericType(Class<? extends Number> clazz);

    /**
     * Select a function factory to compile the source. Use this method
     * to provide custom factories which enable to user to add custom
     * functions or symbols.
     *
     * @param factory  The function factory to use when compiling
     * @return The builder
     */
    CompilerBuilder withFunctionFactory(FunctionFactory factory);

    /**
     * Define an external variable
     *
     * @param variable  Variable id
     * @param type  Variable type
     * @return The builder
     */
    CompilerBuilder declareVariable(String variable, ValueType<?> type);

    /**
     * Compile the program into an expression returning clazz.
     *
     * @param type  The expected result type
     * @return The compiled expression
     */
    <T> Expression<T> compileExpression(ValueType<T> type) throws ParseException;

    /**
     * Compile the program.
     *
     * @return The compiled program
     */
    Program compileProgram() throws ParseException;
}
