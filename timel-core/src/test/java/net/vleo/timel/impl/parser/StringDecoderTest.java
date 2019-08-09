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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Leofreddi
 */
class StringDecoderTest {
    @ParameterizedTest
    @CsvSource({
            "\\a,'\u0007'", "x\\a,'x\u0007'", "\\ax,'\u0007x'", "x\\ax,'x\u0007x'",
            "\\b,'\b'", "x\\b,'x\b'", "\\bx,'\bx'", "x\\bx,'x\bx'",
            "\\f,'\f'", "x\\f,'x\f'", "\\fx,'\fx'", "x\\fx,'x\fx'",
            "\\n,'\n'", "x\\n,'x\n'", "\\nx,'\nx'", "x\\nx,'x\nx'",
            "\\r,'\r'", "x\\r,'x\r'", "\\rx,'\rx'", "x\\rx,'x\rx'",
            "\\t,'\t'", "x\\t,'x\t'", "\\tx,'\tx'", "x\\tx,'x\tx'",
            "\\v,'\u000b'", "x\\v,'x\u000b'", "\\vx,'\u000bx'", "x\\vx,'x\u000bx'",
            /*"\\',\'",*/ "x\\',x'", /*"\\'x,'x",*/ "x\\'x,x'x", // it looks like junit won't handle this one correctly
            "\\\",'\"'", "x\\\",'x\"'", "\\\"x,'\"x'", "x\\\"x,'x\"x'",
            "\\\\,'\\'", "x\\\\,'x\\'", "\\\\x,'\\x'", "x\\\\x,'x\\x'",
            "\\?,'?'", "x\\?,'x?'", "\\?x,'?x'", "x\\?x,'x?x'",
    })
    void decodeShouldDecodeSimpleEscapeSequences(String source, String expected) {
        val actual = new StringDecoder().decode(source);

        assertThat(actual, is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "\\z",
            "z\\z",
            "z\\zz",
            "\\c",
            "\\!"
    })
    void decodeShouldThrowIllegalArgumentExceptionWhenInvalidSimpleEscape(String source) {
        assertThrows(IllegalArgumentException.class, () -> new StringDecoder().decode(source));
    }

    @ParameterizedTest
    @CsvSource({
            "\\",
            "z\\",
    })
    void decodeShouldThrowIllegalArgumentExceptionWhenIncompleteSimpleEscape(String source) {
        assertThrows(IllegalArgumentException.class, () -> new StringDecoder().decode(source));
    }

    @ParameterizedTest
    @CsvSource({
            "\\0,'\0'",
            "z\\0,'z\0'",
            "\\1,'\1'",
            "z\\1,'z\1'",
            "\\00,'\0'",
            "z\\00,'z\0'",
            "\\000,'\0'",
            "z\\000,'z\0'",
            "\\001,'\1'",
            "z\\001,'z\1'",
            "\\0010,'\0010'",
            "z\\0010,'z\0010'",
            "\\1\\2,'\1\2'",
            "z\\1\\2,'z\1\2'",
            "\\1\\2z,'\1\2z'",
    })
    void decodeShouldDecodeOctalEscapeSequences(String source, String expected) {
        val actual = new StringDecoder().decode(source);

        assertThat(actual, is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "\\X0,'\0'",
            "\\x0,'\0'",
            "'\\x0 ','\0 '",
            "\\x00,'\0'",
            "'\\x00 ','\0 '",
            "\\x61,a",
            "\\x61x,ax",
            "x\\x61,xa",
            "x\\x61x,xax",
            "x\\x61\\x62x,xabx",
            "\\x61\\x62x,abx",
            "x\\x61\\x62,xab",
            "x\\x61x\\x62x,xaxbx",
            "'\\x3f',?",
            "'\\X3f',?",
            "'\\x3F',?",
            "'\\X3F',?",
    })
    void decodeShouldDecodeHexEscapeSequences(String source, String expected) {
        val actual = new StringDecoder().decode(source);

        assertThat(actual, is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "\\x",
            "\\xz",
            "\\x0\\x",
            "\\x0\\xz",
    })
    void decodeShouldThrowIllegalArgumentExceptionWhenIncompleteHexEscapeSequence(String source) {
        assertThrows(IllegalArgumentException.class, () -> new StringDecoder().decode(source));
    }

    @ParameterizedTest
    @CsvSource({
            "\\u0000,'\0'",
            "'\\u0000 ','\0 '",
            "' \\u0000',' \0'",
            "' \\u0000 ',' \0 '",
            "'\\u03a9','\u03a9'",
            "'\\u03a9 ','\u03a9 '",
            "' \\u03a9',' \u03a9'",
            "' \\u03a9 ',' \u03a9 '",
            "'\\u03A9','\u03A9'",
    })
    void decodeShouldDecodeUniversal4EscapeSequences(String source, String expected) {
        val actual = new StringDecoder().decode(source);

        assertThat(actual, is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "\\u",
            "\\uz",
            "\\u0",
            "\\u0z",
            "\\u00",
            "\\u00z",
            "\\u000",
            "\\u000z",
    })
    void decodeShouldThrowIllegalArgumentExceptionWhenIncompleteUniversal4EscapeSequence(String source) {
        assertThrows(IllegalArgumentException.class, () -> new StringDecoder().decode(source));
    }

    @ParameterizedTest
    @CsvSource({
            "\\U00000000,'\0\0'",
            "'\\U00000000 ','\0\0 '",
            "' \\U00000000',' \0\0'",
            "' \\U00000000 ',' \0\0 '",
            "'\\Ud83dde0a','\ud83d\ude0a'",
            "'\\Ud83dde0a ','\ud83d\ude0a '",
            "' \\Ud83dde0a',' \ud83d\ude0a'",
            "' \\Ud83dde0a ',' \ud83d\ude0a '",
            "'\\UD83DDE0A','\ud83d\ude0a'",
    })
    void decodeShouldDecodeUniversal8EscapeSequences(String source, String expected) {
        val actual = new StringDecoder().decode(source);

        assertThat(actual, is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "\\U",
            "\\Uz",
            "\\U0",
            "\\U0z",
            "\\U00",
            "\\U00z",
            "\\U000",
            "\\U000z",
            "\\U0000",
            "\\U0000z",
            "\\U00000",
            "\\U00000z",
            "\\U000000",
            "\\U000000z",
            "\\U0000000",
            "\\U0000000z",
    })
    void decodeShouldThrowIllegalArgumentExceptionWhenIncompleteUniversal8EscapeSequence(String source) {
        assertThrows(IllegalArgumentException.class, () -> new StringDecoder().decode(source));
    }
}
