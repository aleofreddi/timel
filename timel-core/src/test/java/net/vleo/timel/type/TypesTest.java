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

import lombok.val;
import net.vleo.timel.ConfigurationException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Leofreddi
 */
class TypesTest {
    public static class TestType extends Type<Void> {
    }

    private static class ErrorTestType extends Type<Void> {
    }

    @Test
    void shouldInstantiateType() {
        val actual = Types.instance(TestType.class);

        assertThat(actual, is(instanceOf(TestType.class)));
    }

    @Test
    void shouldThrowConfigurationErrorOnFailure() {
        ConfigurationException actual = assertThrows(ConfigurationException.class, () -> Types.instance(ErrorTestType.class));
    }
}
