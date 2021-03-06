package net.vleo.timel.annotation;

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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.util.Collections.*;

/**
 * An annotation to declare a TimEL function.
 *
 * @author Andrea Leofreddi
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionPrototype {
    String name();

    Returns returns() default @Returns;

    Parameter[] parameters();

    Constraint[] constraints() default {};

    /**
     * A placeholder value to be used as empty value for variable names.
     */
    String NULL_VARIABLE = "";

    /**
     * A placeholder class to be used as empty value for types.
     *
     * @author Andrea Leofreddi
     */
    class NilType extends Type {
        NilType() {
            super(emptyList());
            throw new AssertionError();
        }
    }
}
