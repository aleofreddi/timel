package net.vleo.timel.impl.parser;

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

import lombok.val;
import net.vleo.timel.ParseException;
import net.vleo.timel.impl.parser.tree.ZeroConstant;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Andrea Leofreddi
 */
class ParserTreeVisitorTest {
    @Test
    void shouldReturnNullForEachVisitMethod() {
        val visitor = new ParserTreeVisitor<Object, ParseException>() {
        };

        assertThat(
                Arrays.stream(ParserTreeVisitor.class.getDeclaredMethods())
                        .filter(method -> method.getName().equals("visit"))
                        .map(method -> invokeVisit(method, visitor))
                        .filter(Objects::nonNull)
                        .collect(toList()),
                empty()
        );
    }

    @Test
    void shouldSupportUncheckedExceptions() {
        val visitor = new ParserTreeVisitor<Object, RuntimeException>() {
        };

        visitor.visit(new ZeroConstant(null));
    }

    @Test
    void shouldSupportCheckedExceptions() throws IOException {
        val visitor = new ParserTreeVisitor<Object, IOException>() {
        };

        visitor.visit(new ZeroConstant(null));
    }

    private static Object invokeVisit(Method method, ParserTreeVisitor<Object, ParseException> visitor) {
        try {
            return method.invoke(visitor, new Object[] {null});
        } catch(IllegalAccessException | InvocationTargetException e) {
            fail("Unexpected throw exception", e);
            return null;
        }
    }
}
