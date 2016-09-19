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
package net.vleo.timel.impl.evaluate;

import net.vleo.timel.executor.ExecutorContextFactory;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.variable.VariableFactory;
import net.vleo.timel.executor.ExecutorBuilder;
import net.vleo.timel.executor.variable.Variable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea Leofreddi
 */
public class AbstractExecutorBuilderImpl<T extends ExecutorBuilder<T>> implements ExecutorBuilder<T> {
    protected final HashMap<String, Variable<?>> variables = new HashMap<String, Variable<?>>();

    protected VariableFactory variableFactory;

    protected ExecutorContextFactory executorContextFactory;

    protected ExecutorContext getEvaluationContext() {
        ExecutorContext context;

        // Instance the evaluation context
        if(executorContextFactory != null)
            context = executorContextFactory.newExecutorContext();
        else
            context = new ExecutorContext();

        // Register all the variables
        for(Map.Entry<String, Variable<?>> entry : variables.entrySet())
            context.putVariable(entry.getKey(), entry.getValue());

        // Register the autobinder
        if(variableFactory != null)
            context.setVariableFactory(variableFactory);

        return context;
    }

    @Override
    public T autoBind(VariableFactory variableFactory) {
        this.variableFactory = variableFactory;

        return (T) this;
    }

    @Override
    public T withOption(String key, Object value) {
        throw new IllegalArgumentException("Unknown option " + key);

        //return this;
    }

    @Override
    public T contextFactory(ExecutorContextFactory executorContextFactory) {
        this.executorContextFactory = executorContextFactory;

        return (T) this;
    }

    @Override
    public T bindVariable(String id, Variable<?> variable) {
        variables.put(id, variable);

        return (T) this;
    }
}
