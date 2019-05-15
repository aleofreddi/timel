package net.vleo.timel.variable;

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

import lombok.Getter;
import lombok.Setter;
import net.vleo.timel.ParseException;
import net.vleo.timel.tuple.Pair;
import net.vleo.timel.type.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * Variable registry. This class is responsible to hold the association between a variable and its implementation details.
 *
 * @author Andrea Leofreddi
 */
public class VariableRegistry {
    private final Map<String, Pair<Type, Variable<?>>> variables = new HashMap<>();

    @Getter
    @Setter
    private VariableFactory variableFactory = null;

    @SuppressWarnings("unchecked")
    public <V> Variable<V> getVariable(String id) {
        Pair<Type, Variable<?>> result = variables.get(id);
        if(result == null)
            return null;
        return (Variable<V>) result.getSecond();
    }

    public Type getType(String id) {
        Pair<Type, Variable<?>> result = variables.get(id);
        if(result == null)
            return null;
        return result.getFirst();
    }

    public <V> Variable<V> newVariable(String id, Type type) throws ParseException {
        Pair<Type, Variable<?>> result = variables.get(id);

        if(result != null)
            throw new ParseException("Variable " + id + " is already defined");

        Variable<?> variable = variableFactory.newVariable(id, type);
        variables.put(id, new Pair<>(type, variable));

        return (Variable<V>) variable;
    }

    public <V> Variable<V> addVariable(String id, Type type, Variable<V> variable) {
        variables.put(id, new Pair<>(type, variable));

        return variable;
    }
}
