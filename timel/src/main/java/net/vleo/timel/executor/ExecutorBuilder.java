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
package net.vleo.timel.executor;

import net.vleo.timel.executor.variable.Variable;
import net.vleo.timel.executor.variable.VariableFactory;

/**
 * Common interface for evaluators.
 *
 * @author Andrea Leofreddi
 */
@SuppressWarnings("unused")
public interface ExecutorBuilder<T extends ExecutorBuilder<T>> {
    /**
     * Enable the executor option key with the given value.
     *
     * @param key  Option name
     * @param value  Option value
     * @return The builder
     */
    T withOption(String key, Object value);

    /**
     * Use the given factory to instance executor context.
     *
     * By default the double type is selected.
     *
     * @param executorContextFactory  ExecutorContext factory
     * @return The builder
     */
    T contextFactory(ExecutorContextFactory executorContextFactory);

    /**
     * Bind a variable with a concrete instance.
     *
     * @param id  The id to bind
     * @param variable  The backend variable
     * @return The builder
     */
    T bindVariable(String id, Variable<?> variable);

    /**
     * Enable autobind mode. In this the mode the executor will
     * create a new variable backend instance for any unbound variable
     * using the given variable factory.
     *
     * @param variableFactory  The variable factory
     * @return The builder
     */
    T autoBind(VariableFactory variableFactory);
}
