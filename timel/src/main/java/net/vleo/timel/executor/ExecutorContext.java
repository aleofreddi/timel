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
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.ValueType;

import java.util.HashMap;
import java.util.Map;

/**
 * An object keeping the context of an execution.
 *
 * @author Andrea Leofreddi
 */
public class ExecutorContext {
    private final Map<String, Variable<?>> variables = new HashMap<String, Variable<?>>();

    private VariableFactory variableFactory = null;

    private Interval interval;

    public VariableFactory getVariableFactory() {
        return variableFactory;
    }

    public void setVariableFactory(VariableFactory variableFactory) {
        this.variableFactory = variableFactory;
    }

    public void putVariable(String id, Variable<?> variable) {
        variables.put(id, variable);
    }

    @SuppressWarnings("unchecked")
    public <V> Variable<V> getVariable(String id) {
        return (Variable<V>) variables.get(id);
    }

    public <V> Variable<V> instanceVariable(String id, ValueType<V> type) {
        @SuppressWarnings("unchecked")
        Variable<V> variable = (Variable<V>) variables.get(id);

        if(variable != null)
            return variable;

        if(variableFactory == null)
            throw new ExecutionException("Variable " + id + " is missing in the evaluation context");

        variable = variableFactory.newVariable(id, type);

        variables.put(id, variable);

        return variable;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }
}
