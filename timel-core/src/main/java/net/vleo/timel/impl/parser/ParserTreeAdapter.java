
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

import lombok.RequiredArgsConstructor;
import net.vleo.timel.grammar.TimELBaseVisitor;
import net.vleo.timel.grammar.TimELLexer;
import net.vleo.timel.grammar.TimELParser;
import net.vleo.timel.impl.parser.tree.*;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;

/**
 * A ANTLR visitor that will adapt an ANTLR's tree into a {@link AbstractParseTree}.
 *
 * @author Andrea Leofreddi
 */
@RequiredArgsConstructor
class ParserTreeAdapter extends TimELBaseVisitor<AbstractParseTree> {
    private final StringDecoder stringDecoder;

    @Override
    public AbstractParseTree visitPrimaryExpression(TimELParser.PrimaryExpressionContext ctx) {
        if(ctx.getChildCount() == 1)
            return passthroughFirst(ctx);
        assertChildCount(ctx, 3);
        return passthroughFirst(ctx.getChild(1));
    }

    @Override
    public AbstractParseTree visitArray(TimELParser.ArrayContext ctx) {
        assertChildCount(ctx, 3, Integer.MAX_VALUE);
        List<AbstractParseTree> arguments = parseList(ctx.getChild(1));
        return new FunctionCall("Array", arguments);
    }

    @Override
    public AbstractParseTree visitPostfixExpression(TimELParser.PostfixExpressionContext ctx) {
        return passthroughFirst(ctx);
    }

    @Override
    public AbstractParseTree visitFunctionCall(TimELParser.FunctionCallContext ctx) {
        assertChildCount(ctx, 3, 4);
        String function = ctx.getChild(0).getText();
        List<AbstractParseTree> arguments = ctx.getChildCount() == 3 ?
                emptyList() : parseList(ctx.getChild(2));
        return new FunctionCall(function, arguments);
    }

    @Override
    public AbstractParseTree visitArgumentExpressionList(TimELParser.ArgumentExpressionListContext ctx) {
        return passthroughFirst(ctx);
    }

    @Override
    public AbstractParseTree visitAssignmentExpression(TimELParser.AssignmentExpressionContext ctx) {
        if(ctx.getChildCount() == 1)
            return passthroughFirst(ctx);
        assertChildCount(ctx, 3);
        return new Assignment((Variable) ctx.getChild(0).accept(this), ctx.getChild(2).accept(this));
    }

    @Override
    public AbstractParseTree visitUnaryExpression(TimELParser.UnaryExpressionContext ctx) {
        if(ctx.getChildCount() == 1)
            return passthroughFirst(ctx);
        if(ctx.getChildCount() == 4) {
            assert ((TerminalNode) ctx.getChild(0)).getSymbol().getType() == TimELLexer.TypeId;
            return new TypeId(ctx.getChild(2).accept(this));
        }
        assertChildCount(ctx, 2);
        return new FunctionCall(ctx.getChild(0).getText(), singletonList(ctx.getChild(1).accept(this)));
    }

    @Override
    public AbstractParseTree visitUnaryOperator(TimELParser.UnaryOperatorContext ctx) {
        return passthroughFirst(ctx);
    }

    @Override
    public AbstractParseTree visitCastExpression(TimELParser.CastExpressionContext ctx) {
        if(ctx.getChildCount() == 1)
            return passthroughFirst(ctx);
        assertChildCount(ctx, 4);
        return new ExplicitCast(ctx.getChild(1).accept(this), ctx.getChild(3).accept(this));
    }

    @Override
    public AbstractParseTree visitMultiplicativeExpression(TimELParser.MultiplicativeExpressionContext ctx) {
        if(ctx.getChildCount() == 1)
            return passthroughFirst(ctx);
        assertChildCount(ctx, 3);
        return new FunctionCall(ctx.getChild(1).getText(), asList(ctx.getChild(0).accept(this), ctx.getChild(2).accept(this)));
    }

    @Override
    public AbstractParseTree visitAdditiveExpression(TimELParser.AdditiveExpressionContext ctx) {
        if(ctx.getChildCount() == 1)
            return passthroughFirst(ctx);
        assertChildCount(ctx, 3);
        return new FunctionCall(ctx.getChild(1).getText(), asList(ctx.getChild(0).accept(this), ctx.getChild(2).accept(this)));
    }

    @Override
    public AbstractParseTree visitShiftExpression(TimELParser.ShiftExpressionContext ctx) {
        return passthroughFirst(ctx);
    }

    @Override
    public AbstractParseTree visitRelationalExpression(TimELParser.RelationalExpressionContext ctx) {
        if(ctx.getChildCount() == 1)
            return passthroughFirst(ctx);
        assertChildCount(ctx, 3);
        return new FunctionCall(ctx.getChild(1).getText(), asList(ctx.getChild(0).accept(this), ctx.getChild(2).accept(this)));
    }

    @Override
    public AbstractParseTree visitEqualityExpression(TimELParser.EqualityExpressionContext ctx) {
        return passthroughOrInfixBinaryFunctionCall(ctx);
    }

    @Override
    public AbstractParseTree visitAndExpression(TimELParser.AndExpressionContext ctx) {
        return passthroughFirst(ctx);
    }

    @Override
    public AbstractParseTree visitExclusiveOrExpression(TimELParser.ExclusiveOrExpressionContext ctx) {
        if(ctx.getChildCount() == 1)
            return passthroughFirst(ctx);
        assertChildCount(ctx, 3);
        return new FunctionCall(ctx.getChild(1).getText(), asList(ctx.getChild(0).accept(this), ctx.getChild(2).accept(this)));
    }

    @Override
    public AbstractParseTree visitInclusiveOrExpression(TimELParser.InclusiveOrExpressionContext ctx) {
        return passthroughFirst(ctx);
    }

    @Override
    public AbstractParseTree visitLogicalAndExpression(TimELParser.LogicalAndExpressionContext ctx) {
        if(ctx.getChildCount() == 1)
            return passthroughFirst(ctx);
        assertChildCount(ctx, 3);
        return new FunctionCall(ctx.getChild(1).getText(), asList(ctx.getChild(0).accept(this), ctx.getChild(2).accept(this)));
    }

    @Override
    public AbstractParseTree visitLogicalOrExpression(TimELParser.LogicalOrExpressionContext ctx) {
        if(ctx.getChildCount() == 1)
            return passthroughFirst(ctx);
        assertChildCount(ctx, 3);
        return new FunctionCall(ctx.getChild(1).getText(), asList(ctx.getChild(0).accept(this), ctx.getChild(2).accept(this)));
    }

    @Override
    public AbstractParseTree visitConditionalExpression(TimELParser.ConditionalExpressionContext ctx) {
        if(ctx.getChildCount() == 1)
            return passthroughFirst(ctx);
        assertChildCount(ctx, 5);
        return new FunctionCall("if", asList(ctx.getChild(0).accept(this), ctx.getChild(2).accept(this), ctx.getChild(4).accept(this)));
    }

    @Override
    public AbstractParseTree visitExpression(TimELParser.ExpressionContext ctx) {
        return passthroughFirst(ctx);
    }

    @Override
    public AbstractParseTree visitStatement(TimELParser.StatementContext ctx) {
        return passthroughFirst(ctx);
    }

    @Override
    public AbstractParseTree visitTypeSpecifier(TimELParser.TypeSpecifierContext ctx) {
        assertChildCount(ctx, 1, 4);
        String type = ctx.getChild(0).getText();
        List<AbstractParseTree> arguments = ctx.getChildCount() == 1 ? emptyList() : parseList(ctx.getChild(2));
        return new TypeSpecifier(type, arguments);
    }

    @Override
    public AbstractParseTree visitTemplateExpressionListOpen(TimELParser.TemplateExpressionListOpenContext ctx) {
        throw new AssertionError();
    }

    @Override
    public AbstractParseTree visitTypeSpecifierOpen(TimELParser.TypeSpecifierOpenContext ctx) {
        assertChildCount(ctx, 1, 4);
        String type = ctx.getChild(0).getText();
        List<AbstractParseTree> arguments = ctx.getChildCount() == 1 ? emptyList() : parseList(ctx.getChild(2));
        return new TypeSpecifier(type, arguments);
    }

    @Override
    public AbstractParseTree visitTemplateExpressionList(TimELParser.TemplateExpressionListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbstractParseTree visitTemplateArgument(TimELParser.TemplateArgumentContext ctx) {
        return passthroughFirst(ctx);
    }

    @Override
    public AbstractParseTree visitCompilationUnit(TimELParser.CompilationUnitContext ctx) {
        assertChildCount(ctx, 1, Integer.MAX_VALUE);
        LinkedList<AbstractParseTree> children = new LinkedList<>();
        for(int i = 0; i < ctx.getChildCount() && !isEof(ctx.getChild(i)); i += 2)
            children.add(ctx.getChild(i).accept(this));
        return new CompilationUnit(children);
    }

    @Override
    public AbstractParseTree visitExpressionUnit(TimELParser.ExpressionUnitContext ctx) {
        return passthroughFirst(ctx);
    }

    @Override
    public AbstractParseTree visitConstant(TimELParser.ConstantContext ctx) {
        return passthroughFirst(ctx);
    }

    @Override
    public AbstractParseTree visitTerminal(TerminalNode terminalNode) {
        switch(terminalNode.getSymbol().getType()) {
            case TimELLexer.IntegerConstant:
                return new IntegerConstant(Integer.parseInt(terminalNode.getText()));

            case TimELLexer.FloatingConstant:
                return parseFloatingConstant(terminalNode.getText());

            case TimELLexer.Zero:
                return new ZeroConstant();

            case TimELLexer.StringLiteral:
                return new StringConstant(decodeStringLiteral(terminalNode.getText()));

            case TimELLexer.Identifier:
                return new Variable(terminalNode.getText());

            default:
                throw new AssertionError("Unknown terminal token " + terminalNode.getSymbol().getType());
        }
    }

    @Override
    public AbstractParseTree visitErrorNode(ErrorNode errorNode) {
        throw new IllegalStateException("Parse tree contains an error node: " + errorNode);
    }

    private AbstractParseTree parseFloatingConstant(String text) {
        if(text.endsWith("f"))
            return new FloatConstant(Float.parseFloat(text.substring(0, text.length() - 1)));
        if(text.endsWith("d"))
            return new DoubleConstant(Double.parseDouble(text.substring(0, text.length() - 1)));

        return new DoubleConstant(Double.parseDouble(text));
    }

    private void assertChildCount(ParseTree ctx, int expectedMin, int expectedMax) {
        if(expectedMin > ctx.getChildCount())
            throw new AssertionError("Expected at least " + expectedMin + " child(ren) for node " + ctx + ", but got " + ctx.getChildCount() + " at `" + ctx.getText() + "`");
        if(expectedMax < ctx.getChildCount())
            throw new AssertionError("Expected at most " + expectedMax + " child(ren) for node " + ctx + ", but got " + ctx.getChildCount() + " at `" + ctx.getText() + "`");
    }

    private void assertChildCount(ParseTree ctx, int expected) {
        if(expected != ctx.getChildCount())
            throw new AssertionError("Expected " + expected + " child(ren) for node " + ctx + ", but got " + ctx.getChildCount() + " at `" + ctx.getText() + "`");
    }

    private List<ParseTree> getChildren(ParseTree ctx, int skip) {
        return IntStream.range(skip, ctx.getChildCount())
                .boxed()
                .map(ctx::getChild)
                .collect(toList());
    }

    private boolean isEof(ParseTree ctx) {
        if(!(ctx instanceof TerminalNode))
            return false;

        return ((TerminalNode) ctx).getSymbol().getType() == TimELLexer.EOF;
    }

    private AbstractParseTree passthroughFirst(ParseTree ctx) {
        if(ctx.getChildCount() != 1)
            throw new AssertionError("Expected a single child node, but got " + ctx.getChildCount() + " children at `" + ctx.getText() + "`");
        return ctx.getChild(0).accept(this);
    }

    private List<AbstractParseTree> parseList(ParseTree argumentList) {
        LinkedList<AbstractParseTree> arguments = new LinkedList<>();
        while(argumentList.getChildCount() == 3) {
            arguments.addFirst(argumentList.getChild(2).accept(this));
            argumentList = argumentList.getChild(0);
        }
        arguments.addFirst(passthroughFirst(argumentList));
        return arguments;
    }

    private AbstractParseTree passthroughOrInfixBinaryFunctionCall(ParseTree ctx) {
        if(ctx.getChildCount() == 1)
            return passthroughFirst(ctx);
        assertChildCount(ctx, 3);
        return new FunctionCall(ctx.getChild(1).getText(), asList(ctx.getChild(0).accept(this), ctx.getChild(2).accept(this)));
    }

    private String decodeStringLiteral(String text) {
        if(text.length() < 2)
            throw new AssertionError("Unexpected invalid string token");
        if(text.charAt(0) != '"' || text.charAt(text.length() - 1) != '"')
            throw new AssertionError("Unexpected string format");

        return stringDecoder.decode(text.substring(1, text.length() - 1));
    }
}
