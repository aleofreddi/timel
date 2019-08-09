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
import net.vleo.timel.impl.parser.tree.IntegerConstant;
import net.vleo.timel.impl.parser.tree.StringConstant;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrea Leofreddi
 */
@ExtendWith(MockitoExtension.class)
class ParserTreeAdapterTest {
    private static final Interval SOURCE_INTERVAL = new Interval(1, 2);
    private static final Token SOURCE_TOKEN = new CommonToken(1, "token");

    @Mock
    private TerminalNode terminalNode;
    @Mock
    private StringDecoder stringDecoder;
    @Mock
    private TokenStream tokenStream;
    @InjectMocks
    private ParserTreeAdapter parserTreeAdapter;

    @ParameterizedTest
    @CsvSource({
            "0,0",
            "1,1",
            Integer.MAX_VALUE + "," + Integer.MAX_VALUE
    })
    void visitTerminalShouldDecodeIntegers(String source, int expected) {
        when(terminalNode.getSymbol()).thenReturn(new CommonToken(TimELLexer.IntegerConstant));
        when(terminalNode.getText()).thenReturn(source);

        val actual = parserTreeAdapter.visitTerminal(terminalNode);

        assertThat(actual, instanceOf(IntegerConstant.class));
        assertThat(((IntegerConstant) actual).getValue(), is(expected));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "" + 0x80000000L,
            "9999999999",
    })
    void visitTerminalShouldThrowUncheckedParseExceptionWhenInvalidInteger(String source) {
        when(terminalNode.getSymbol()).thenReturn(new CommonToken(TimELLexer.IntegerConstant));
        when(terminalNode.getText()).thenReturn(source);
        when(terminalNode.getSourceInterval()).thenReturn(SOURCE_INTERVAL);
        when(tokenStream.get(anyInt())).thenReturn(SOURCE_TOKEN);

        val actual = assertThrows(UncheckedParseException.class, () -> parserTreeAdapter.visitTerminal(terminalNode));

        assertThat(actual.getCause(), instanceOf(ParseException.class));
    }

    @ParameterizedTest
    @CsvSource({
            "00,0",
            "01,1",
            "017777777777," + Integer.MAX_VALUE
    })
    void visitTerminalShouldDecodeOctalIntegers(String source, int expected) {
        when(terminalNode.getSymbol()).thenReturn(new CommonToken(TimELLexer.IntegerConstant));
        when(terminalNode.getText()).thenReturn(source);

        val actual = parserTreeAdapter.visitTerminal(terminalNode);

        assertThat(actual, instanceOf(IntegerConstant.class));
        assertThat(((IntegerConstant) actual).getValue(), is(expected));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "020000000000",
            "077777777777",
            "0111111111111",
    })
    void visitTerminalShouldThrowUncheckedParseExceptionWhenInvalidOctalInteger(String source) {
        when(terminalNode.getSymbol()).thenReturn(new CommonToken(TimELLexer.IntegerConstant));
        when(terminalNode.getText()).thenReturn(source);
        when(terminalNode.getSourceInterval()).thenReturn(SOURCE_INTERVAL);
        when(tokenStream.get(anyInt())).thenReturn(SOURCE_TOKEN);

        val actual = assertThrows(UncheckedParseException.class, () -> parserTreeAdapter.visitTerminal(terminalNode));

        assertThat(actual.getCause(), instanceOf(ParseException.class));
    }

    @ParameterizedTest
    @CsvSource({
            "0x0,0",
            "0x1,1",
            "0x7fffffff," + Integer.MAX_VALUE,
    })
    void visitTerminalShouldDecodeHexIntegers(String source, int expected) {
        when(terminalNode.getSymbol()).thenReturn(new CommonToken(TimELLexer.IntegerConstant));
        when(terminalNode.getText()).thenReturn(source);

        val actual = parserTreeAdapter.visitTerminal(terminalNode);

        assertThat(actual, instanceOf(IntegerConstant.class));
        assertThat(((IntegerConstant) actual).getValue(), is(expected));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0x80000000",
            "0xffffffff",
            "0x111111111",
    })
    void visitTerminalShouldThrowUncheckedParseExceptionWhenInvalidHexInteger(String source) {
        when(terminalNode.getSymbol()).thenReturn(new CommonToken(TimELLexer.IntegerConstant));
        when(terminalNode.getText()).thenReturn(source);
        when(terminalNode.getSourceInterval()).thenReturn(SOURCE_INTERVAL);
        when(tokenStream.get(anyInt())).thenReturn(SOURCE_TOKEN);

        val actual = assertThrows(UncheckedParseException.class, () -> parserTreeAdapter.visitTerminal(terminalNode));

        assertThat(actual.getCause(), instanceOf(ParseException.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "source string"
    })
    void visitTerminalShouldStripQuotesAndDecodeStringLiterals(String source) {
        when(terminalNode.getSymbol()).thenReturn(new CommonToken(TimELLexer.StringLiteral));
        when(terminalNode.getText()).thenReturn('"' + source + '"');
        when(stringDecoder.decode(anyString())).thenReturn("§" + source + "§");
        when(terminalNode.getSourceInterval()).thenReturn(SOURCE_INTERVAL);
        when(tokenStream.get(anyInt())).thenReturn(SOURCE_TOKEN);

        val actual = parserTreeAdapter.visitTerminal(terminalNode);

        verify(stringDecoder).decode(source);
        assertThat(actual, instanceOf(StringConstant.class));
        assertThat(((StringConstant) actual).getValue(), is("§" + source + "§"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "no quotes",
            "a",
            "aa",
            "\"aa",
            "aa\"",
    })
    void visitTerminalShouldThrowAssertionErrorWhenInvalidString(String value) {
        when(terminalNode.getSymbol()).thenReturn(new CommonToken(TimELLexer.StringLiteral));
        when(terminalNode.getText()).thenReturn(value);
        when(terminalNode.getSourceInterval()).thenReturn(SOURCE_INTERVAL);
        when(tokenStream.get(anyInt())).thenReturn(SOURCE_TOKEN);

        assertThrows(AssertionError.class, () -> parserTreeAdapter.visitTerminal(terminalNode));
    }
}
