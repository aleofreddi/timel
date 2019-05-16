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

import lombok.NoArgsConstructor;
import lombok.val;
import net.vleo.timel.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Leofreddi
 */
class TypeSystemTest {
    private static final Set<Type<?>> TEST_TYPES = Stream.of(new TemplateTestType(), new ConcreteTestType()).collect(Collectors.toSet());

    @NoArgsConstructor
    protected static class TemplateTestType extends TemplateType<Object> {
        public TemplateTestType(Integer p) {
            super(Collections.singletonList(p));
        }

        @Override
        public String getName() {
            return "Template";
        }
    }

    @NoArgsConstructor
    protected static class ConcreteTestType extends Type<Object> {
        @Override
        public String getName() {
            return "Concrete";
        }
    }

    @ParameterizedTest
    @CsvSource({
            "Template,1,true,true",
            "Concrete,,false,false",
    })
    void shouldParseType(String typeId, Integer parameter, boolean expectedTemplate, boolean expectedSpecializedTemplate) throws ParseException {
        TypeSystem typeSystem = new TypeSystem(Collections.emptySet(), TEST_TYPES);

        val actual = typeSystem.parse(typeId, parameter == null ? Collections.emptyList() : Collections.singletonList(parameter));

        assertThat(actual.isUnboundTemplate() || actual.isSpecializedTemplate(), is(expectedTemplate));
    }

    @Test
    void shouldThrowParseExceptionWhenUnknownType() {
        TypeSystem typeSystem = new TypeSystem(Collections.emptySet(), TEST_TYPES);

        assertThrows(ParseException.class, () -> typeSystem.parse("?", Collections.emptyList()));
    }

    @Test
    void shouldThrowParseExceptionWhenConcreteTypeAndParameter() {
        TypeSystem typeSystem = new TypeSystem(Collections.emptySet(), TEST_TYPES);

        assertThrows(ParseException.class, () -> typeSystem.parse("Concrete", Collections.singletonList(1)));
    }

    @Test
    void shouldThrowParseExceptionWhenUnboundedTemplate() {
        TypeSystem typeSystem = new TypeSystem(Collections.emptySet(), TEST_TYPES);

        assertThrows(ParseException.class, () -> typeSystem.parse("Template", Collections.emptyList()));
    }
}
