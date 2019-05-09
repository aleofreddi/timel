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

import net.vleo.timel.ParseException;
import net.vleo.timel.impl.parser.tree.AbstractParseTree;
import net.vleo.timel.impl.parser.tree.CompilationUnit;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static java.util.Collections.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Leofreddi
 */
class ParserTest {
    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    @ParameterizedTest
    @CsvSource({
            "1+,no viable alternative",
            "+,no viable alternative"
    })
    void shouldThrowParseExceptionWhenInvalidSource(String source, String expectedMessage) {
        ParseException actual = assertThrows(ParseException.class, () -> new Parser().parse(source));

        assertThat(actual.getMessage(), containsString(expectedMessage));
    }

    @ParameterizedTest
    @CsvSource({
            "1,net.vleo.timel.impl.parser.tree.IntegerConstant,1",
            "2.0f,net.vleo.timel.impl.parser.tree.FloatConstant,2",
            "3.0,net.vleo.timel.impl.parser.tree.DoubleConstant,3",
            "\"test string\",net.vleo.timel.impl.parser.tree.StringConstant,\"test string\""
    })
    void shouldParseConstants(String source, String expectedType, String expectedValue) throws ParseException, ScriptException {
        AbstractParseTree actual = new Parser().parse(source);

        assertThat(actual, is(
                new CompilationUnit(singletonList(
                        (AbstractParseTree) engine.eval("new (Java.type('" + expectedType + "'))(" + expectedValue + ")")
                ))
        ));
    }
}

