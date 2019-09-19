package net.vleo.timel.impl.intermediate;

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
import net.vleo.timel.conversion.Conversion;
import net.vleo.timel.function.FunctionRegistry;
import net.vleo.timel.impl.intermediate.tree.Cast;
import net.vleo.timel.impl.intermediate.tree.Constant;
import net.vleo.timel.impl.intermediate.tree.VariableReader;
import net.vleo.timel.impl.intermediate.tree.VariableWriter;
import net.vleo.timel.impl.parser.tree.*;
import net.vleo.timel.type.ConversionResult;
import net.vleo.timel.type.IntegerType;
import net.vleo.timel.type.Type;
import net.vleo.timel.type.TypeSystem;
import net.vleo.timel.variable.VariableRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrea Leofreddi
 */
@ExtendWith(MockitoExtension.class)
class SyntaxTreeAdapterTest {
    private static final SourceReference SOURCE_REFERENCE = new SourceReference(0, 1, 1, 0);
    @Mock
    private VariableRegistry variableRegistry;
    @Mock
    private FunctionRegistry functionRegistry;
    @Mock
    private TypeSystem typeSystem;
    @Mock
    private net.vleo.timel.variable.Variable<Object> variable;
    @Mock
    private Type<Object> type;

    @Test
    void shouldInstanceNewVariableWhenAssignment() throws ParseException {
        SyntaxTreeAdapter adapter = new SyntaxTreeAdapter(variableRegistry, null);
        when(variableRegistry.newVariable(eq("a"), eq(new IntegerType())))
                .thenReturn(variable);

        val parseTree = new Assignment(SOURCE_REFERENCE, new Variable(SOURCE_REFERENCE, "a"), new IntegerConstant(SOURCE_REFERENCE, 1));
        val actual = adapter.visit(parseTree);

        verify(variableRegistry).newVariable(eq("a"), eq(new IntegerType()));

        assertThat(actual, instanceOf(VariableWriter.class));
        VariableWriter actualWriter = (VariableWriter) actual;
        assertThat(actualWriter.getValue(), instanceOf(Constant.class));
        assertThat(actualWriter.getVariable(), sameInstance(variable));
    }

    @Test
    void shouldResolveVariableToVariableWriterWhenAssigned() throws ParseException {
        SyntaxTreeAdapter adapter = new SyntaxTreeAdapter(variableRegistry, null);

        val parseTree = new CompilationUnit(SOURCE_REFERENCE, Arrays.asList(
                new Assignment(SOURCE_REFERENCE, new Variable(SOURCE_REFERENCE, "a"), new IntegerConstant(SOURCE_REFERENCE, 1)),
                new Variable(SOURCE_REFERENCE, "a")
        ));

        val actual = adapter.visit(parseTree);

        assertThat(actual, instanceOf(net.vleo.timel.impl.intermediate.tree.CompilationUnit.class));
        assertThat(actual.getChildren().size(), is(2));

        val actualVariable = actual.getChildren().get(1);
        assertThat(actualVariable, instanceOf(VariableWriter.class));
    }

    @Test
    void shouldResolveVariableToVariableReaderWhenProvided() throws ParseException {
        when(variableRegistry.getVariable("a"))
                .thenReturn(variable);
        SyntaxTreeAdapter adapter = new SyntaxTreeAdapter(variableRegistry, null);

        val parseTree = new CompilationUnit(SOURCE_REFERENCE, Collections.singletonList(
                new Variable(SOURCE_REFERENCE, "a")
        ));

        val actual = adapter.visit(parseTree);

        assertThat(actual, instanceOf(net.vleo.timel.impl.intermediate.tree.CompilationUnit.class));
        assertThat(actual.getChildren().size(), is(1));

        val actualVariable = actual.getChildren().get(0);
        assertThat(actualVariable, instanceOf(VariableReader.class));
    }

    @Test
    void shouldThrowParseExceptionWhenDoubleDeclaration() {
        SyntaxTreeAdapter adapter = new SyntaxTreeAdapter(variableRegistry, null);

        val parseTree = new CompilationUnit(SOURCE_REFERENCE, Arrays.asList(
                new Assignment(SOURCE_REFERENCE, new Variable(SOURCE_REFERENCE, "a"), new IntegerConstant(SOURCE_REFERENCE, 1)),
                new Assignment(SOURCE_REFERENCE, new Variable(SOURCE_REFERENCE, "a"), new IntegerConstant(SOURCE_REFERENCE, 1))
        ));
        ParseException actual = assertThrows(ParseException.class, () -> adapter.visit(parseTree));

        assertThat(actual.getMessage(), containsString("already declared"));
        assertThat(actual.getSourceReference(), sameInstance(SOURCE_REFERENCE));
    }

    @Test
    void shouldThrowParseExceptionWhenInvalidVariable() {
        when(variableRegistry.newVariable(Mockito.eq("a"), Mockito.any(Type.class)))
                .thenThrow(new IllegalArgumentException());
        SyntaxTreeAdapter adapter = new SyntaxTreeAdapter(variableRegistry, null);

        val parseTree = new CompilationUnit(SOURCE_REFERENCE, Collections.singletonList(
                new Assignment(SOURCE_REFERENCE, new Variable(SOURCE_REFERENCE, "a"), new IntegerConstant(SOURCE_REFERENCE, 1))
        ));
        ParseException actual = assertThrows(ParseException.class, () -> adapter.visit(parseTree));

        assertThat(actual.getMessage(), containsString("Invalid variable"));
        assertThat(actual.getSourceReference(), sameInstance(SOURCE_REFERENCE));
    }

    @Test
    void shouldThrowParseExceptionWhenUndefinedVariable() {
        SyntaxTreeAdapter adapter = new SyntaxTreeAdapter(variableRegistry, null);

        val parseTree = new CompilationUnit(SOURCE_REFERENCE, Collections.singletonList(
                new Variable(SOURCE_REFERENCE, "a")
        ));
        ParseException actual = assertThrows(ParseException.class, () -> adapter.visit(parseTree));

        assertThat(actual.getMessage(), containsString("Undefined variable"));
        assertThat(actual.getSourceReference(), sameInstance(SOURCE_REFERENCE));
    }

    @Test
    void shouldThrowParseExceptionWhenInvalidType() {
        when(functionRegistry.getTypeSystem())
                .thenReturn(typeSystem);
        IllegalArgumentException expected = new IllegalArgumentException("");
        when(typeSystem.parse(anyString(), anyList()))
                .thenThrow(expected);
        SyntaxTreeAdapter adapter = new SyntaxTreeAdapter(variableRegistry, functionRegistry);

        val parseTree = new TypeSpecifier(SOURCE_REFERENCE, "Integer", Collections.singletonList(new IntegerConstant(null, 1)));
        ParseException actual = assertThrows(ParseException.class, () -> adapter.visit(parseTree));

        assertThat(actual.getCause(), sameInstance(expected));
        assertThat(actual.getSourceReference(), sameInstance(SOURCE_REFERENCE));
    }

    @Test
    void shouldResolveCastWhenExplicitCast() throws ParseException {
        List<Conversion<Object, Object>> conversionList = new ArrayList<>();
        ConversionResult conversionResult = new ConversionResult(conversionList, type);

        when(functionRegistry.getTypeSystem())
                .thenReturn(typeSystem);
        when(typeSystem.parse(eq("Integer"), anyList()))
                .thenReturn(type);
        when(typeSystem.getConcretePath(eq(false), Mockito.any(Type.class), Mockito.any(Type.class)))
                .thenReturn(conversionResult);

        SyntaxTreeAdapter adapter = new SyntaxTreeAdapter(null, functionRegistry);

        val parseTree = new ExplicitCast(SOURCE_REFERENCE, new TypeSpecifier(SOURCE_REFERENCE, "Integer", Collections.emptyList()), new IntegerConstant(SOURCE_REFERENCE, 1));
        val actual = adapter.visit(parseTree);

        assertThat(actual, instanceOf(Cast.class));
        Cast actualCast = (Cast) actual;
        assertThat(actualCast.getType(), sameInstance(type));
        assertThat(actualCast.getConversions(), sameInstance(conversionList));
        assertThat(actualCast.getInput(), instanceOf(Constant.class));
    }

    @Test
    void shouldThrowParseExceptionWhenInvalidExplicitCast() {
        when(functionRegistry.getTypeSystem())
                .thenReturn(typeSystem);
        when(typeSystem.parse(eq("Integer"), anyList()))
                .thenReturn(type);
        when(typeSystem.getConcretePath(eq(false), Mockito.any(Type.class), Mockito.any(Type.class)))
                .thenReturn(null);

        SyntaxTreeAdapter adapter = new SyntaxTreeAdapter(null, functionRegistry);

        val parseTree = new ExplicitCast(SOURCE_REFERENCE, new TypeSpecifier(SOURCE_REFERENCE, "Integer", Collections.emptyList()), new IntegerConstant(SOURCE_REFERENCE, 1));
        ParseException actual = assertThrows(ParseException.class, () -> adapter.visit(parseTree));

        assertThat(actual.getMessage(), containsString("Cannot convert"));
        assertThat(actual.getSourceReference(), sameInstance(SOURCE_REFERENCE));
    }

    @Test
    void shouldThrowParseExceptionWhenInvalidFunction() throws ParseException {
        when(functionRegistry.lookup(Mockito.isNull(), Mockito.anyString(), Mockito.anyList()))
                .thenThrow(new ParseException(""));

        SyntaxTreeAdapter adapter = new SyntaxTreeAdapter(null, functionRegistry);

        val parseTree = new FunctionCall(SOURCE_REFERENCE, "unknown", Collections.emptyList());
        ParseException actual = assertThrows(ParseException.class, () -> adapter.visit(parseTree));

        assertThat(actual.getSourceReference(), sameInstance(SOURCE_REFERENCE));
    }
}
