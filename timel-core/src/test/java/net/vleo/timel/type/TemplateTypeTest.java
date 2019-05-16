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
import net.vleo.timel.ConfigurationException;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Leofreddi
 */
class TemplateTypeTest {
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

    protected static class UnconstructableType extends TemplateType<Object> {
        public UnconstructableType(int i) {
        }

        public UnconstructableType() {
            throw new IllegalArgumentException();
        }
    }

    @NoArgsConstructor
    protected static class UnspecializableType extends TemplateType<Object> {
        public UnspecializableType(Object p) {
            throw new IllegalArgumentException();
        }
    }

    @Test
    void shouldToStringTemplate() {
        val actual = new TemplateTestType().toString();

        assertThat(actual, is("Template"));
    }

    @Test
    void shouldToStringSpecialization() {
        val actual = new TemplateTestType(1).toString();

        assertThat(actual, is("Template<1>"));
    }

    @Test
    void shouldThrowConfigurationErrorWhenFailToSpecialize() {
        val type = new UnspecializableType();

        assertThrows(ConfigurationException.class, () -> type.specialize(42));
    }

    @Test
    void shouldThrowConfigurationErrorWhenFailToUnbound() {
        val type = new UnconstructableType(42);

        assertThrows(ConfigurationException.class, type::template);
    }
}
