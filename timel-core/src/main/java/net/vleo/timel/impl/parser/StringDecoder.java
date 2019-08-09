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

/**
 * String escape sequence decoder.
 *
 * @author Andrea Leofreddi
 */
public class StringDecoder {
    private final static Character EOF = null;

    enum State {
        TEXT, ESCAPE, OCTAL, HEXADECIMAL, UNIVERSAL4, UNIVERSAL8
    }

    public String decode(String string) {
        State state = State.TEXT;
        StringBuilder output = new StringBuilder(), encoded = new StringBuilder();

        for(int i = 0; i < string.length(); ) {
            char current = string.charAt(i);

            switch(state) {
                case TEXT:
                    if(current == '\\')
                        state = State.ESCAPE;
                    else
                        output.append(current);
                    break;

                case ESCAPE:
                    if(isOctal(current)) {
                        encoded.append(current);
                        state = State.OCTAL;
                    } else if(current == 'x' || current == 'X')
                        state = State.HEXADECIMAL;
                    else if(current == 'u')
                        state = State.UNIVERSAL4;
                    else if(current == 'U')
                        state = State.UNIVERSAL8;
                    else {
                        state = State.TEXT;
                        if(current == '\'' || current == '"' || current == '\\' || current == '?') {
                            output.append(current);
                        } else if(current == 'a') {
                            output.append((char) 0x7);
                        } else if(current == 'b') {
                            output.append('\b');
                        } else if(current == 'f') {
                            output.append('\f');
                        } else if(current == 'n') {
                            output.append('\n');
                        } else if(current == 'r') {
                            output.append('\r');
                        } else if(current == 't') {
                            output.append('\t');
                        } else if(current == 'v') {
                            output.append((char) 0xb);
                        } else
                            throw new IllegalArgumentException("Unexpected escape sequence");
                    }
                    break;

                case OCTAL:
                    if(encoded.length() < 3) {
                        if(isOctal(current))
                            encoded.append(current);
                        else {
                            output.append(decodeOctal(encoded.toString()));
                            encoded = new StringBuilder();
                            state = State.TEXT;
                            continue;
                        }
                    } else {
                        output.append(decodeOctal(encoded.toString()));
                        encoded = new StringBuilder();
                        state = State.TEXT;
                        continue;
                    }
                    break;

                case HEXADECIMAL:
                    if(encoded.length() == 0) {
                        if(isHex(current))
                            encoded.append(current);
                        else
                            throw new IllegalArgumentException("Invalid hexadecimal escape sequence");
                    } else if(encoded.length() < 2) {
                        if(isHex(current))
                            encoded.append(current);
                        else {
                            output.append(decodeHex(encoded.toString()));
                            encoded = new StringBuilder();
                            state = State.TEXT;
                            continue;
                        }
                    } else {
                        output.append(decodeHex(encoded.toString()));
                        encoded = new StringBuilder();
                        state = State.TEXT;
                        continue;
                    }
                    break;

                case UNIVERSAL4:
                case UNIVERSAL8:
                    if(isHex(current))
                        encoded.append(current);
                    else
                        throw new IllegalArgumentException("Invalid unicode escape sequence");

                    if(encoded.length() == 4) {
                        output.append(decodeHex(encoded.toString()));
                        encoded = new StringBuilder();
                        state = state == State.UNIVERSAL8 ? State.UNIVERSAL4 : State.TEXT;
                    }
                    break;
            }

            i++;
        }

        switch(state) {
            case TEXT:
                break;

            case ESCAPE:
            case UNIVERSAL4:
            case UNIVERSAL8:
                throw new IllegalArgumentException("Invalid unicode escape sequence");

            case OCTAL:
                output.append(decodeOctal(encoded.toString()));
                break;

            case HEXADECIMAL:
                if(encoded.length() == 0)
                    throw new IllegalArgumentException("Invalid hexadecimal escape sequence");
                output.append(decodeHex(encoded.toString()));
                break;
        }
        return output.toString();
    }

    private char decodeOctal(String octEncoded) {
        return (char) Integer.parseInt(octEncoded, 8);
    }

    private char decodeHex(String hexEncoded) {
        return (char) Integer.parseInt(hexEncoded, 16);
    }

    private boolean isOctal(char character) {
        return character >= '0' && character <= '7';
    }

    private boolean isHex(char character) {
        return (character >= '0' && character <= '9')
                || (character >= 'a' && character <= 'f')
                || (character >= 'A' && character <= 'F');
    }
}
