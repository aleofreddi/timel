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
import net.vleo.timel.impl.parser.tree.AbstractParseTree;
import net.vleo.timel.impl.parser.tree.SourceReference;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * TimEL parser facade.
 *
 * @author Andrea Leofreddi
 */
public class Parser {
    private static class ThrowingErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int char_, String message, RecognitionException e) throws ParseCancellationException {
            int index = offendingSymbol instanceof Token ? ((Token) offendingSymbol).getStartIndex() : recognizer.getInputStream().index();
            throw new ParseCancellationException(new ParseException(new SourceReference(index, 0, line, char_), message, e));
        }
    }

    private static final ThrowingErrorListener THROWING_ERROR_LISTENER = new ThrowingErrorListener();

    public AbstractParseTree parse(String source) throws ParseException {
        try {
            ANTLRInputStream input = new ANTLRInputStream(source);
            TimELLexer lexer = new TimELLexer(input);
            lexer.addErrorListener(THROWING_ERROR_LISTENER);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            TimELParser parser = new TimELParser(tokens);
            parser.addErrorListener(THROWING_ERROR_LISTENER);
            ParseTree tree = parser.compilationUnit();
            return tree.accept(new ParserTreeAdapter(tokens, new StringDecoder()));
        } catch(UncheckedParseException e) {
            throw (ParseException) e.getCause();
        } catch(ParseCancellationException e) {
            val nested = e.getCause();
            if(nested instanceof ParseException)
                throw (ParseException) nested;
            throw new ParseException(e.getMessage(), e);
        }
    }
}
