package net.vleo.timel.impl.intermediate;

import lombok.val;
import net.vleo.timel.ParseException;
import net.vleo.timel.conversion.Conversion;
import net.vleo.timel.function.FunctionRegistry;
import net.vleo.timel.impl.intermediate.tree.Cast;
import net.vleo.timel.impl.intermediate.tree.Constant;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Andrea Leofreddi
 */
@ExtendWith(MockitoExtension.class)
class SyntaxTreeAdapterTest {
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

        val parseTree = new Assignment(new Variable("a"), new IntegerConstant(1));
        val actual = adapter.visit(parseTree);

        verify(variableRegistry).newVariable(eq("a"), eq(new IntegerType()));

        assertThat(actual, instanceOf(VariableWriter.class));
        VariableWriter actualWriter = (VariableWriter) actual;
        assertThat(actualWriter.getValue(), instanceOf(Constant.class));
        assertThat(actualWriter.getVariable(), sameInstance(variable));
    }

    @Test
    void shouldThrowWrappedParseExceptionWhenDoubleDeclaration() {
        when(variableRegistry.getVariable("a"))
                .thenReturn(variable);
        SyntaxTreeAdapter adapter = new SyntaxTreeAdapter(variableRegistry, null);

        val parseTree = new CompilationUnit(Arrays.asList(
                new Assignment(new Variable("a"), new IntegerConstant(1)),
                new Assignment(new Variable("a"), new IntegerConstant(1))
        ));

        RuntimeException actual = assertThrows(RuntimeException.class, () -> adapter.visit(parseTree));

        assertThat(actual.getCause(), instanceOf(ParseException.class));
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

        val parseTree = new ExplicitCast(new TypeSpecifier("Integer", Collections.emptyList()), new IntegerConstant(1));
        val actual = adapter.visit(parseTree);

        assertThat(actual, instanceOf(Cast.class));
        Cast actualCast = (Cast) actual;
        assertThat(actualCast.getType(), sameInstance(type));
        assertThat(actualCast.getConversions(), sameInstance(conversionList));
        assertThat(actualCast.getInput(), instanceOf(Constant.class));
    }

    @Test
    void shouldThrowWrapperParseExceptionWhenInvalidExplicitCast() throws ParseException {
        when(functionRegistry.getTypeSystem())
                .thenReturn(typeSystem);
        when(typeSystem.parse(eq("Integer"), anyList()))
                .thenReturn(type);
        when(typeSystem.getConcretePath(eq(false), Mockito.any(Type.class), Mockito.any(Type.class)))
                .thenReturn(null);

        SyntaxTreeAdapter adapter = new SyntaxTreeAdapter(null, functionRegistry);

        val parseTree = new ExplicitCast(new TypeSpecifier("Integer", Collections.emptyList()), new IntegerConstant(1));
        RuntimeException actual = assertThrows(RuntimeException.class, () -> adapter.visit(parseTree));

        assertThat(actual.getCause(), instanceOf(ParseException.class));
    }
}