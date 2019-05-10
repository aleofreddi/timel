package net.vleo.timel.type;

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

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

/**
 * A abstract superclass for template types, that is types that are defined by a class and a list of parameters.
 *
 * @author Andrea Leofreddi
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
public abstract class TemplateType<T> extends Type<T> {
    @Getter
    private final List<Object> parameters;

    protected TemplateType(Object... parameters) {
        this.parameters = Arrays.asList(parameters);
    }

    protected TemplateType() {
        this(Collections.emptyList());
    }

    public boolean isSpecializedTemplate() {
        return !parameters.isEmpty();
    }

    public boolean isUnboundTemplate() {
        return true;
    }

    public TemplateType template() {
        try {
            return getClass().getDeclaredConstructor().newInstance();
        } catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Failed to instance template intermediate for " + this, e);
        }
    }

    public TemplateType<T> specialize(Object... parameters) {
        Class[] parameterTypes = Arrays.stream(parameters)
                .map(Object::getClass)
                .toArray(Class[]::new);

        Constructor constructor;
        try {
            constructor = getClass().getDeclaredConstructor(parameterTypes);
        } catch(NoSuchMethodException e) {
            throw new RuntimeException(e); // FIXME
        }

        try {
            return (TemplateType<T>) constructor.newInstance(parameters);
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e); // FIXME
        }
    }

    @Override
    public String toString() {
        if(parameters != null)
            return getName() +
                    parameters.stream()
                            .map(Objects::toString)
                            .collect(joining(", ", "<", ">"));

        return getName();
    }
}
