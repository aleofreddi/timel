package net.vleo.timel.function;

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

import lombok.NoArgsConstructor;
import lombok.val;
import net.vleo.timel.ParseException;
import net.vleo.timel.annotation.*;
import net.vleo.timel.conversion.Conversion;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.impl.downscaler.Downscaler;
import net.vleo.timel.impl.intermediate.tree.AbstractSyntaxTree;
import net.vleo.timel.impl.intermediate.tree.Cast;
import net.vleo.timel.impl.parser.ParserTreeVisitor;
import net.vleo.timel.impl.parser.tree.AbstractParseTree;
import net.vleo.timel.impl.parser.tree.SourceReference;
import net.vleo.timel.impl.target.Evaluable;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Andrea Leofreddi
 */
@ExtendWith(MockitoExtension.class)
class FunctionRegistryTest {
    private static final SourceReference SOURCE_REFERENCE = new SourceReference(0, 1, 1, 0);
    private static final AbstractParseTree SOURCE_NODE = new AbstractParseTree(SOURCE_REFERENCE, emptyList()) {
        @Override
        public <T> T accept(ParserTreeVisitor<T, ParseException> visitor) {
            return null;
        }
    };

    @Spy
    private TypeSystem typeSystem = new TypeSystem(TestTypes.CONVERSIONS, Collections.emptySet());

    private FunctionRegistry functionRegistry;

    @BeforeEach
    private void setup() {
        functionRegistry = new FunctionRegistry(typeSystem);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenNoPrototype() {
        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.add(new TestFunctions.Fun_Invalid_NoPrototypeAnnotations()));

        assertThat(actual.getMessage(), containsString("should be annotated"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenBothPrototypeAnnotationsAreUsed() {
        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.add(new TestFunctions.Fun_Invalid_MultiplePrototypeAnnotations()));

        assertThat(actual.getMessage(), containsString("annotated with both"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenInvalidPrototypeVarArgs() {
        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.add(new TestFunctions.Fun_Invalid_NonTailVarArgs()));

        assertThat(actual.getMessage(), containsString("varArgs are only allowed"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAddingDuplicatePrototype() {
        functionRegistry.add(new TestFunctions.Fun_A2A());

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.add(new TestFunctions.Fun_A2A()));

        assertThat(actual.getMessage(), containsString("Duplicate"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUnknownName() {
        functionRegistry.add(new TestFunctions.Fun_A2A());

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.lookup(SOURCE_NODE, "???", mockArguments()));

        assertCannotResolve(actual);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenTooFewArguments() {
        functionRegistry.add(new TestFunctions.Fun_A2A());

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.lookup(SOURCE_NODE, "a->a", mockArguments()));

        assertCannotResolve(actual);
        assertThat(actual.getMessage(), containsString("Available signatures: a->a($A)"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenTooManyArguments() {
        functionRegistry.add(new TestFunctions.Fun_A2A());

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.lookup(SOURCE_NODE, "a->a", mockArguments(new TestTypes.Polygon(), new TestTypes.Polygon())));

        assertCannotResolve(actual);
        assertThat(actual.getMessage(), containsString("Available signatures: a->a($A)"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenMultipleMatches() {
        functionRegistry.add(new TestFunctions.Fun_Ambiguous());

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.lookup(SOURCE_NODE, "ambiguous", mockArguments(new ZeroType())));

        assertThat(actual.getMessage(), containsString("Ambiguous function call"));
        assertThat(actual.getMessage().chars().filter(c -> c == ',').count(), is(2L));
    }

    @Test
    void shouldMatchAndResolveReturnTypeFromVariable() {
        functionRegistry.add(new TestFunctions.Fun_A2A());

        val actual = functionRegistry.lookup(SOURCE_NODE, "a->a", mockArguments(new TestTypes.Polygon()));

        assertThat(actual.getType(), equalTo(new TestTypes.Polygon()));
    }

    @Test
    void shouldMatchAndResolveVariablesWhenSameType() {
        functionRegistry.add(new TestFunctions.Fun_AA2A());

        val actual = functionRegistry.lookup(SOURCE_NODE, "aa->a", mockArguments(
                new TestTypes.Polygon(),
                new TestTypes.Polygon()
        ));

        assertThat(actual.getType(), equalTo(new TestTypes.Polygon()));
    }

    @Test
    void shouldMatchAndResolveVariablesWhenConstraintMatches() {
        functionRegistry.add(new TestFunctions.Fun_CC2One());

        val actual = functionRegistry.lookup(SOURCE_NODE, "cc->1", mockArguments(
                new TestTypes.Color(),
                new TestTypes.Color()
        ));

        assertThat(actual.getType(), equalTo(new TestTypes.One()));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenConstraintDoesNotMatch() {
        functionRegistry.add(new TestFunctions.Fun_CC2One());

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.lookup(SOURCE_NODE, "cc->1", mockArguments(
                new TestTypes.Polygon(),
                new TestTypes.Polygon()
        )));

        assertCannotResolve(actual);
    }

    @ParameterizedTest
    @CsvSource({
            "Polygon,Square,Polygon",
            "Polygon,Polygon,Polygon",
            "Square,Square,Square",
    })
    void shouldMatchAndResolveVariablesWhenConvertibleTypes(
            @ConvertWith(TestTypes.TypeConverter.class) Type t,
            @ConvertWith(TestTypes.TypeConverter.class) Type u,
            @ConvertWith(TestTypes.TypeConverter.class) Type expected
    ) {
        functionRegistry.add(new TestFunctions.Fun_AA2A());

        val actual = functionRegistry.lookup(SOURCE_NODE, "aa->a", mockArguments(t, u));

        assertThat(actual.getType(), equalTo(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "Square,Polygon,Polygon,",
            "Polygon,Square,,Polygon",
            "Polygon,Polygon,,",
            "Square,Square,,",
    })
    void shouldMatchAndAddCastNodesWhenVariablesHaveConvertibleTypes(
            @ConvertWith(TestTypes.TypeConverter.class) Type t,
            @ConvertWith(TestTypes.TypeConverter.class) Type u,
            @ConvertWith(TestTypes.TypeConverter.class) Type tExpectedCast,
            @ConvertWith(TestTypes.TypeConverter.class) Type uExpectedCast
    ) {
        functionRegistry.add(new TestFunctions.Fun_AA2A());

        val actual = functionRegistry.lookup(SOURCE_NODE, "aa->a", mockArguments(t, u));

        assertThat(actual.getArguments(), hasSize(2));

        if(tExpectedCast == null)
            assertThat(actual.getArguments().get(0), not(instanceOf(Cast.class)));
        else {
            assertThat(actual.getArguments().get(0), instanceOf(Cast.class));
            val conversions = ((Cast) actual.getArguments().get(0)).getConversions();
            assertThat(
                    Types.instance(
                            (Class<? extends Type<Object>>) conversions
                                    .get(conversions.size() - 1)
                                    .getClass()
                                    .getDeclaredAnnotation(ConversionPrototype.class)
                                    .target()
                    ),
                    is(tExpectedCast)
            );
        }

        if(uExpectedCast == null)
            assertThat(actual.getArguments().get(1), not(instanceOf(Cast.class)));
        else {
            assertThat(actual.getArguments().get(1), instanceOf(Cast.class));
            val conversions = ((Cast) actual.getArguments().get(1)).getConversions();
            assertThat(
                    Types.instance(
                            (Class<? extends Type<Object>>) conversions
                                    .get(conversions.size() - 1)
                                    .getClass()
                                    .getDeclaredAnnotation(ConversionPrototype.class)
                                    .target()
                    ),
                    is(uExpectedCast)
            );
        }
    }

    @ParameterizedTest
    @CsvSource({
            "Two,One"
    })
    void shouldThrowIllegalArgumentExceptionWhenVariablesAreNotConvertible(
            @ConvertWith(TestTypes.TypeConverter.class) Type t,
            @ConvertWith(TestTypes.TypeConverter.class) Type u
    ) {
        functionRegistry.add(new TestFunctions.Fun_AA2A());

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.lookup(SOURCE_NODE, "aa->a", mockArguments(t, u)));

        assertCannotResolve(actual);
    }

    @ParameterizedTest
    @CsvSource({
            "Two,Two,Two"
    })
    void shouldMatchVarArgs(
            @ConvertWith(TestTypes.TypeConverter.class) Type t,
            @ConvertWith(TestTypes.TypeConverter.class) Type u,
            @ConvertWith(TestTypes.TypeConverter.class) Type expected
    ) {
        functionRegistry.add(new TestFunctions.Fun_AStar2A());

        val actual = functionRegistry.lookup(SOURCE_NODE, "a*->a", mockArguments(t, u));

        assertThat(actual.getType(), is(expected));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionEmptyVarArgsWhenNeededForReturn() {
        functionRegistry.add(new TestFunctions.Fun_AStar2A());

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.lookup(SOURCE_NODE, "a*->a", mockArguments()));

        assertCannotResolve(actual);
    }

    @Test
    void shouldMatchEmptyVarArgs() {
        functionRegistry.add(new TestFunctions.Fun_AStar2One());

        val actual = functionRegistry.lookup(SOURCE_NODE, "a*->1", mockArguments());

        assertThat(actual.getType(), is(new TestTypes.One()));
    }

    @ParameterizedTest
    @CsvSource({
            "One,Two"
    })
    void shouldThrowIllegalArgumentExceptionVarArgsWhenVariablesAreNotConvertible(
            @ConvertWith(TestTypes.TypeConverter.class) Type t,
            @ConvertWith(TestTypes.TypeConverter.class) Type u
    ) {
        functionRegistry.add(new TestFunctions.Fun_AStar2A());

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.lookup(SOURCE_NODE, "a*->a", mockArguments(t, u)));

        assertCannotResolve(actual);
    }

    @ParameterizedTest
    @CsvSource({
            "One,Two,,,,,Two",
            "One,Two,One,Two,,,Two",
            "One,Two,One,Two,One,Two,Two"
    })
    void shouldMatchVarArgGroups(
            @ConvertWith(TestTypes.TypeConverter.class) Type a,
            @ConvertWith(TestTypes.TypeConverter.class) Type b,
            @ConvertWith(TestTypes.TypeConverter.class) Type c,
            @ConvertWith(TestTypes.TypeConverter.class) Type d,
            @ConvertWith(TestTypes.TypeConverter.class) Type e,
            @ConvertWith(TestTypes.TypeConverter.class) Type f,
            @ConvertWith(TestTypes.TypeConverter.class) Type expected
    ) {
        functionRegistry.add(new TestFunctions.Fun_AB_Star2B());

        Type[] arguments = Stream.of(a, b, c, d, e, f)
                .filter(Objects::nonNull)
                .toArray(Type[]::new);

        val actual = functionRegistry.lookup(SOURCE_NODE, "(ab)*->b", mockArguments(arguments));

        assertThat(actual.getType(), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "One,,,,",
            "One,Two,One,,",
            "One,Two,One,Two,One",
    })
    void shouldThrowIllegalArgumentExceptionIncompleteVarArgGroups(
            @ConvertWith(TestTypes.TypeConverter.class) Type a,
            @ConvertWith(TestTypes.TypeConverter.class) Type b,
            @ConvertWith(TestTypes.TypeConverter.class) Type c,
            @ConvertWith(TestTypes.TypeConverter.class) Type d,
            @ConvertWith(TestTypes.TypeConverter.class) Type e
    ) {
        functionRegistry.add(new TestFunctions.Fun_AB_Star2B());

        Type[] arguments = Stream.of(a, b, c, d, e)
                .filter(Objects::nonNull)
                .toArray(Type[]::new);

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.lookup(SOURCE_NODE, "(ab)*->b", mockArguments(arguments)));

        assertCannotResolve(actual);
    }

    @ParameterizedTest
    @CsvSource({
            "One,Two,Two,One",
    })
    void shouldThrowIllegalArgumentExceptionIncompatibleVarArgGroups(
            @ConvertWith(TestTypes.TypeConverter.class) Type a,
            @ConvertWith(TestTypes.TypeConverter.class) Type b,
            @ConvertWith(TestTypes.TypeConverter.class) Type c,
            @ConvertWith(TestTypes.TypeConverter.class) Type d
    ) {
        functionRegistry.add(new TestFunctions.Fun_AB_Star2B());

        Type[] arguments = Stream.of(a, b, c, d)
                .filter(Objects::nonNull)
                .toArray(Type[]::new);

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.lookup(SOURCE_NODE, "(ab)*->b", mockArguments(arguments)));

        assertCannotResolve(actual);
    }

    @ParameterizedTest
    @CsvSource({
            "Color<Red>,Color<White>",
    })
    void shouldThrowIllegalArgumentExceptionIncompatibleTemplateSpecializations(
            @ConvertWith(TestTypes.TypeConverter.class) Type a,
            @ConvertWith(TestTypes.TypeConverter.class) Type b
    ) {
        functionRegistry.add(new TestFunctions.Fun_AStar2One());

        Type[] arguments = Stream.of(a, b)
                .filter(Objects::nonNull)
                .toArray(Type[]::new);

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.lookup(SOURCE_NODE, "a*->1", mockArguments(arguments)));

        assertCannotResolve(actual);
    }

    @ParameterizedTest
    @CsvSource({
            "Color<Red>,Color<Red>"
    })
    void shouldMatchWhenSameTemplateSpecializations(
            @ConvertWith(TestTypes.TypeConverter.class) Type a,
            @ConvertWith(TestTypes.TypeConverter.class) Type b
    ) {
        functionRegistry.add(new TestFunctions.Fun_AStar2One());

        Type[] arguments = Stream.of(a, b)
                .filter(Objects::nonNull)
                .toArray(Type[]::new);

        val actual = functionRegistry.lookup(SOURCE_NODE, "a*->1", mockArguments(arguments));

        assertThat(actual.getType(), is(new TestTypes.One()));
    }

    @ParameterizedTest
    @CsvSource({
            "Color<Red>,Color<Red>"
    })
    void shouldThrowIllegalArgumentExceptionWhenNoReturnIsSpecifiedAndProgrammaticReturnTypeResolutionFails(
            @ConvertWith(TestTypes.TypeConverter.class) Type a,
            @ConvertWith(TestTypes.TypeConverter.class) Type b
    ) {
        Function function = Mockito.spy(new TestFunctions.Fun_AStar2Unknown());
        functionRegistry.add(function);

        Type[] arguments = Stream.of(a, b)
                .filter(Objects::nonNull)
                .toArray(Type[]::new);

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.lookup(SOURCE_NODE, "a*->?", mockArguments(arguments)));
        verify(function).resolveReturnType(Mockito.isNull(), Mockito.anyMap(), Mockito.any());

        assertCannotResolve(actual);
    }

    @ParameterizedTest
    @CsvSource({
            "Color<Red>,Color<Red>"
    })
    void shouldUseProgrammaticReturnTypeResolutionWhenNoReturnIsSpecified(
            @ConvertWith(TestTypes.TypeConverter.class) Type a,
            @ConvertWith(TestTypes.TypeConverter.class) Type b
    ) {
        val expected = new TestTypes.Two();
        Function function = Mockito.mock(TestFunctions.Fun_AStar2Unknown.class);
        when(function.resolveReturnType(Mockito.isNull(), Mockito.anyMap(), Mockito.any()))
                .thenReturn(Optional.of(expected));
        when(function.specializeVariableTemplate(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenCallRealMethod();

        functionRegistry.add(function);

        Type[] arguments = Stream.of(a, b)
                .filter(Objects::nonNull)
                .toArray(Type[]::new);

        val actual = functionRegistry.lookup(SOURCE_NODE, "a*->?", mockArguments(arguments));
        verify(function).resolveReturnType(Mockito.isNull(), Mockito.anyMap(), Mockito.any());

        assertThat(actual.getType(), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "Color<Red>,Color<Red>,Color<Red>",
            "Color<Red>,Color<Green>,Color<Yellow>"
    })
    void shouldUseProgrammaticReturnTypeResolutionWhenReturnIsTemplate(
            @ConvertWith(TestTypes.TypeConverter.class) Type a,
            @ConvertWith(TestTypes.TypeConverter.class) Type b,
            @ConvertWith(TestTypes.TypeConverter.class) Type expected
    ) {
        Function function = Mockito.spy(new TestFunctions.Fun_CC2C());
        functionRegistry.add(function);

        Type[] arguments = Stream.of(a, b)
                .filter(Objects::nonNull)
                .toArray(Type[]::new);

        val actual = functionRegistry.lookup(SOURCE_NODE, "cc->c", mockArguments(arguments));
        verify(function).resolveReturnType(Mockito.any(TestTypes.Color.class), Mockito.anyMap(), Mockito.any());

        assertThat(actual.getType(), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
            "Color<Red>,Color<White>"
    })
    void shouldThrowIllegalArgumentExceptionWhenReturnIsTemplateAndProgrammaticReturnTypeResolutionFails(
            @ConvertWith(TestTypes.TypeConverter.class) Type a,
            @ConvertWith(TestTypes.TypeConverter.class) Type b
    ) {
        Function function = Mockito.spy(new TestFunctions.Fun_CC2C());
        functionRegistry.add(function);

        Type[] arguments = Stream.of(a, b)
                .filter(Objects::nonNull)
                .toArray(Type[]::new);

        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> functionRegistry.lookup(SOURCE_NODE, "cc->c", mockArguments(arguments)));
        verify(function).resolveReturnType(Mockito.any(TestTypes.Color.class), Mockito.anyMap(), Mockito.any());

        assertCannotResolve(actual);
    }

    @ParameterizedTest
    @CsvSource({
            "Polygon,One,p->1",
            "Color,One,c->1"
    })
    void shouldMatchMultiPrototypes(
            @ConvertWith(TestTypes.TypeConverter.class) Type t,
            @ConvertWith(TestTypes.TypeConverter.class) Type u,
            String function
    ) {
        functionRegistry.add(new TestFunctions.Fun_M2One());

        val actual = functionRegistry.lookup(SOURCE_NODE, function, mockArguments(t));

        assertThat(actual.getType(), equalTo(u));
    }

    private List<AbstractSyntaxTree> mockArguments(Type<?>... types) {
        return stream(types)
                .map(type -> {
                    AbstractSyntaxTree node = mock(AbstractSyntaxTree.class);
                    Mockito.<Type<?>>when(node.getType()).thenReturn(type);
                    return node;
                })
                .collect(toList());
    }

    private void assertCannotResolve(IllegalArgumentException actual) {
        assertThat(actual.getMessage(), containsString("Cannot resolve function"));
    }

    /**
     * Dummy types to support the tests above.
     */
    private static class TestTypes {
        public static class Polygon extends Type {
        }

        public static class Triangle extends Type {
        }

        public static class Rectangle extends Type {
        }

        public static class Square extends Type {
        }

        public static class One extends Type {
        }

        public static class Two extends Type {
        }

        @NoArgsConstructor
        public static class Color extends TemplateType {
            public Color(String color) {
                super(color);
            }
        }

        public static class MockedConversion implements Conversion<Object, Object> {
            @Override
            public Object apply(Object value) {
                return null;
            }
        }

        @ConversionPrototype(source = TestTypes.Square.class, target = TestTypes.Rectangle.class, implicit = true)
        static class SquareToRectangle extends MockedConversion {
        }

        @ConversionPrototype(source = TestTypes.Rectangle.class, target = Polygon.class, implicit = true)
        static class RectangleToPolygon extends MockedConversion {
        }

        @ConversionPrototype(source = TestTypes.Triangle.class, target = Polygon.class, implicit = true)
        static class TriangleToPolygon extends MockedConversion {
        }

        @ConversionPrototype(source = ZeroType.class, target = FloatType.class, implicit = true)
        static class ZeroToFloat extends MockedConversion {
        }

        @ConversionPrototype(source = ZeroType.class, target = DoubleType.class, implicit = true)
        static class ZeroToDouble extends MockedConversion {
        }

        @ConversionPrototype(source = ZeroType.class, target = IntegerType.class, implicit = true)
        static class ZeroToInteger extends MockedConversion {
        }

        private static final Set<Conversion<?, ?>> CONVERSIONS = new HashSet<>(Arrays.asList(
                new SquareToRectangle(),
                new RectangleToPolygon(),
                new TriangleToPolygon(),

                new ZeroToFloat(),
                new ZeroToDouble(),
                new ZeroToInteger()
        ));

        private static class TypeConverter implements ArgumentConverter {
            private final Pattern TEMPLATE_PATTERN = Pattern.compile("(\\w+)<(\\w+)?>");

            @Override
            public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
                if(source == null)
                    return null;

                if(!(source instanceof String))
                    throw new IllegalArgumentException("Failed to convert intermediate, expected a String as in input");

                String className;
                Object[] parameters;

                Matcher m = TEMPLATE_PATTERN.matcher((String) source);
                if(m.matches()) {
                    className = m.group(1);
                    parameters = new Object[] {m.group(2)};
                } else {
                    className = (String) source;
                    parameters = new Object[0];
                }

                Class<? extends Type> typeClass;
                try {
                    //noinspection unchecked
                    typeClass = (Class<? extends Type>) Class.forName(TestTypes.class.getName() + "$" + className);
                } catch(Exception e) {
                    throw new IllegalArgumentException("Failed to retrieve intermediate", e);
                }

                try {
                    return typeClass.getDeclaredConstructor(Arrays.stream(parameters).map(Object::getClass).toArray(Class[]::new)).newInstance(parameters);
                } catch(InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalArgumentException("Failed to instance intermediate " + typeClass, e);
                }
            }
        }
    }

    /**
     * Dummy functions to support the tests above.
     */
    private static class TestFunctions {
        private static class MockedFunction implements Function<Object> {
            @Override
            public UpscalableIterator<Object> evaluate(Interval interval, ExecutorContext context, Upscaler upscaler, Downscaler downscaler, Evaluable[] arguments) {
                return null;
            }
        }

        @FunctionPrototype(
                returns = @Returns(variable = "A"),
                name = "a->a",
                parameters = {@Parameter(variable = "A")}
        )
        static class Fun_A2A extends MockedFunction {
        }

        @FunctionPrototype(
                returns = @Returns(variable = "A"),
                name = "aa->a",
                parameters = {
                        @Parameter(variable = "A"),
                        @Parameter(variable = "A")
                }
        )
        static class Fun_AA2A extends MockedFunction {
        }

        @FunctionPrototype(
                returns = @Returns(variable = "A"),
                name = "a*->a",
                parameters = {
                        @Parameter(variable = "A", varArgs = true),
                }
        )
        static class Fun_AStar2A extends MockedFunction {
        }

        @FunctionPrototype(
                returns = @Returns(type = TestTypes.One.class),
                name = "a*->1",
                parameters = {
                        @Parameter(variable = "A", varArgs = true),
                }
        )
        static class Fun_AStar2One extends MockedFunction {
        }

        @FunctionPrototype(
                name = "a*->?",
                parameters = {
                        @Parameter(variable = "A", varArgs = true),
                }
        )
        static class Fun_AStar2Unknown extends MockedFunction {
        }

        @FunctionPrototype(
                returns = @Returns(variable = "B"),
                name = "(ab)*->b",
                parameters = {
                        @Parameter(variable = "A", varArgs = true),
                        @Parameter(variable = "B", varArgs = true)
                }
        )
        static class Fun_AB_Star2B extends MockedFunction {
        }

        @FunctionPrototype(
                returns = @Returns(type = TestTypes.Color.class),
                name = "cc->c",
                parameters = {
                        @Parameter(type = TestTypes.Color.class),
                        @Parameter(type = TestTypes.Color.class)
                }
        )
        static class Fun_CC2C extends MockedFunction {
            @Override
            public Optional<Type> resolveReturnType(Type proposed, Map<String, Type> variables, Type... argumentTypes) {
                Set<String> argTypes = stream(argumentTypes)
                        .map(type -> type.getParameters().get(0).toString())
                        .collect(Collectors.toSet());

                if(argTypes.size() == 1)
                    // Same color
                    return Optional.of(proposed.specialize(argTypes.iterator().next()));

                if(argTypes.containsAll(Arrays.asList("Red", "Green")))
                    // Red + Green -> Yellow
                    return Optional.of(proposed.specialize("Yellow"));

                return Optional.empty();
            }
        }

        @FunctionPrototype(
                returns = @Returns(type = TestTypes.One.class),
                name = "cc->1",
                parameters = {
                        @Parameter(variable = "T"),
                        @Parameter(variable = "T")
                },
                constraints = {
                        @Constraint(variable = "T", template = TestTypes.Color.class)
                }
        )
        static class Fun_CC2One extends MockedFunction {
        }

        @FunctionPrototypes({
                @FunctionPrototype(
                        returns = @Returns(type = TestTypes.One.class),
                        name = "c->1",
                        parameters = {
                                @Parameter(type = TestTypes.Color.class)
                        }
                ),
                @FunctionPrototype(
                        returns = @Returns(type = TestTypes.One.class),
                        name = "p->1",
                        parameters = {
                                @Parameter(type = TestTypes.Polygon.class)
                        }
                )
        })
        static class Fun_M2One extends MockedFunction {
        }

        static class Fun_Invalid_NoPrototypeAnnotations extends MockedFunction {
        }

        @FunctionPrototypes({
                @FunctionPrototype(
                        returns = @Returns(type = TestTypes.One.class),
                        name = "ambiguous",
                        parameters = {
                                @Parameter(type = IntegerType.class)
                        }
                ),
                @FunctionPrototype(
                        returns = @Returns(type = TestTypes.One.class),
                        name = "ambiguous",
                        parameters = {
                                @Parameter(type = FloatType.class)
                        }
                ),
                @FunctionPrototype(
                        returns = @Returns(type = TestTypes.One.class),
                        name = "ambiguous",
                        parameters = {
                                @Parameter(type = DoubleType.class)
                        }
                )
        })
        static class Fun_Ambiguous extends MockedFunction {
        }

        @FunctionPrototypes({})
        @FunctionPrototype(
                returns = @Returns(type = TestTypes.One.class),
                name = "invalid_varargs",
                parameters = {
                        @Parameter(type = TestTypes.Color.class, varArgs = true),
                        @Parameter(type = TestTypes.Color.class)
                }
        )
        static class Fun_Invalid_MultiplePrototypeAnnotations extends MockedFunction {
        }

        @FunctionPrototype(
                returns = @Returns(type = TestTypes.One.class),
                name = "invalid_varargs",
                parameters = {
                        @Parameter(type = TestTypes.Color.class, varArgs = true),
                        @Parameter(type = TestTypes.Color.class)
                }
        )
        static class Fun_Invalid_NonTailVarArgs extends MockedFunction {
        }
    }
}
