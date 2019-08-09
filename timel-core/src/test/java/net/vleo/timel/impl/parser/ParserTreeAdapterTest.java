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
import net.vleo.timel.grammar.TimELLexer;
import net.vleo.timel.impl.parser.tree.StringConstant;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrea Leofreddi
 */
@ExtendWith(MockitoExtension.class)
class ParserTreeAdapterTest {
    @Mock
    private TerminalNode terminalNode;
    @Mock
    private StringDecoder stringDecoder;
    @InjectMocks
    private ParserTreeAdapter parserTreeAdapter;

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "source string"
    })
    void visitTerminalShouldStripQuotesAndDecodeStringLiterals(String source) {
        when(terminalNode.getSymbol()).thenReturn(new CommonToken(TimELLexer.StringLiteral));
        when(terminalNode.getText()).thenReturn('"' + source + '"');
        when(stringDecoder.decode(anyString())).thenReturn("§" + source + "§");

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

        assertThrows(AssertionError.class, () -> parserTreeAdapter.visitTerminal(terminalNode));
    }
}
