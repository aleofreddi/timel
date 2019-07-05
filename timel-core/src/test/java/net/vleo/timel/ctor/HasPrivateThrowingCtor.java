package net.vleo.timel.ctor;

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

import lombok.var;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * A hamcrest matcher to ensure a class has only a private, {@link AssertionError} throwing constructor.
 *
 * @author Andrea Leofreddi
 */
public class HasPrivateThrowingCtor extends TypeSafeMatcher<Class<?>> {
    public static HasPrivateThrowingCtor hasPrivateThrowingCtor() {
        return new HasPrivateThrowingCtor();
    }

    @Override
    protected boolean matchesSafely(Class<?> o) {
        var ctors = o.getDeclaredConstructors();
        return ctors.length == 1
                && Modifier.isPrivate(ctors[0].getModifiers())
                && throwsAssertionError(ctors[0]);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("not constructable (private throwing ctor)");
    }

    private boolean throwsAssertionError(Constructor<?> ctor) {
        try {
            ctor.setAccessible(true);
            ctor.newInstance();
            return false;
        } catch(InvocationTargetException e) {
            return e.getCause() instanceof AssertionError;
        } catch(InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
