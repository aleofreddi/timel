package net.vleo.timel.conversion;

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
import net.vleo.timel.type.TemplateType;
import net.vleo.timel.type.Type;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Andrea Leofreddi
 */
class ConversionTest {
    @NoArgsConstructor
    protected static class TemplateTestType extends TemplateType<Object> {
        public TemplateTestType(Integer p) {
            super(Collections.singletonList(p));
        }
    }

    @NoArgsConstructor
    protected static class ConcreteTestType extends Type<Object> {
    }

    @ParameterizedTest
    @CsvSource({
            "1,true,1",
            ",false,",
            "1,false,"
    })
    void shouldResolveReturnType(Integer sourceParam, boolean targetUnboundedTemplate, Integer expectedParam) {
        Conversion<Object, Object> conversion = value -> null;

        val actual = conversion.resolveReturnType(
                sourceParam != null ? new TemplateTestType(sourceParam) : new ConcreteTestType(),
                targetUnboundedTemplate ? new TemplateTestType() : new ConcreteTestType()
        );

        assertThat(actual.isPresent(), is(expectedParam != null));

        if(expectedParam != null)
            assertThat(actual.get().getParameters().get(0), is(expectedParam));
    }
}
