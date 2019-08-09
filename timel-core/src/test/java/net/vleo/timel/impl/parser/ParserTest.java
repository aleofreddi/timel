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
import net.vleo.timel.grammar.TimELLexer;
import net.vleo.timel.grammar.TimELParser;
import net.vleo.timel.impl.parser.tree.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

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
            "1+,no viable alternative,2,1,2",
            "+,no viable alternative,1,1,1",
            "'  +',no viable alternative,3,1,3",
            "' \n +',no viable alternative,4,2,2",
    })
    void shouldThrowParseExceptionWhenInvalidSource(String source, String expectedMessage, int expectedOffset, int expectedLine, int expectedColumn) {
        ParseException actual = assertThrows(ParseException.class, () -> new Parser().parse(source));

        assertThat(actual.getSourceReference(), is(new SourceReference(expectedOffset, 0, expectedLine, expectedColumn)));
        assertThat(actual.getMessage(), containsString(expectedMessage));
    }

    @ParameterizedTest
    @CsvSource({
            "1,0,1,1,0,net.vleo.timel.impl.parser.tree.IntegerConstant,1",
            "2.0f,0,4,1,0,net.vleo.timel.impl.parser.tree.FloatConstant,2",
            "3.0,0,3,1,0,net.vleo.timel.impl.parser.tree.DoubleConstant,3",
            "\"test string\",0,13,1,0,net.vleo.timel.impl.parser.tree.StringConstant,\"test string\""
    })
    void shouldParseConstants(String source, int expectedOffset, int expectedLine, int expectedColumn, int expectedLength, String expectedType, String expectedValue) throws ParseException, ScriptException {
        AbstractParseTree actual = new Parser().parse(source);

        val bindings = new SimpleBindings();
        bindings.put("sourceReference", new SourceReference(expectedOffset, expectedLine, expectedColumn, expectedLength));

        assertThat(actual, is(
                new CompilationUnit(
                        new SourceReference(expectedOffset, expectedLine, expectedColumn, expectedLength),
                        singletonList(
                                (AbstractParseTree) engine.eval("new (Java.type('" + expectedType + "'))(sourceReference, " + expectedValue + ")", bindings)
                        )
                )
        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "(T<T,T<1,T<2.0>>>)1",
            "(T<T,T<1,T<2.0> >>)1",
            "(T<T,T<1,T<2.0>> >)1",
            "(T<T,T<1,T<2.0> > >)1",
    })
    void shouldParseNestedTypeTemplates(String source) throws ParseException {
        val parseTree = new Parser().parse(source);

        assertThat(parseTree, instanceOf(CompilationUnit.class));
        assertThat(navigate(parseTree, 0), instanceOf(ExplicitCast.class));
        assertThat(navigate(parseTree, 0, 0), instanceOf(TypeSpecifier.class));
        assertThat(navigate(parseTree, 0, 0, 0), instanceOf(TypeSpecifier.class));
        assertThat(navigate(parseTree, 0, 0, 1), instanceOf(TypeSpecifier.class));
        assertThat(navigate(parseTree, 0, 0, 1, 0), instanceOf(IntegerConstant.class));
        assertThat(navigate(parseTree, 0, 0, 1, 1), instanceOf(TypeSpecifier.class));
        assertThat(navigate(parseTree, 0, 0, 1, 1, 0), instanceOf(DoubleConstant.class));
    }

    @ParameterizedTest
    @CsvSource({
            "1,1",
            "1;,1",
            "1;2,2",
            "1;2;,2",
    })
    void shouldAcceptOptionalSemicolonClosedLastStatement(String source, int expectedUnits) throws ParseException {
        val actual = new Parser().parse(source);

        assertThat(actual, instanceOf(CompilationUnit.class));
        assertThat(actual.getChildren().size(), is(expectedUnits));

        for(int i = 0; i < expectedUnits; i++)
            assertThat(navigate(actual, i), instanceOf(IntegerConstant.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0xffffffff"
    })
    void parseShouldUnwrapUncheckedParseException(String source) throws ParseException {
        assertThrows(ParseException.class, () -> new Parser().parse(source));
    }

    private AbstractParseTree navigate(AbstractParseTree tree, int... path) {
        for(int i : path)
            tree = tree.getChildren().get(i);
        return tree;
    }
}
