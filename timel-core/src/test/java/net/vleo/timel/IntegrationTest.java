package net.vleo.timel;

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
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.IntervalMaps;
import net.vleo.timel.time.Sample;
import net.vleo.timel.tuple.Pair;
import net.vleo.timel.tuple.Tuple3;
import net.vleo.timel.type.*;
import net.vleo.timel.variable.TreeMapVariable;
import net.vleo.timel.variable.Variable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Run various integration tests from the integration resource directory.
 *
 * @author Andrea Leofreddi
 */
public class IntegrationTest {
    private static final Map<Class<?>, Double> epsilon;

    static {
        epsilon = new HashMap<>();

        epsilon.put(Integer.class, 1d);
        epsilon.put(Float.class, 0.01);
        epsilon.put(Double.class, 0.001);
    }

    private static final String TEST_PROPERTIES_FILE = "test.properties";
    private static final String DATA_CSV_FILE = "data.csv";
    private static final String INTEGRATION_DIR = "integration";

    @ParameterizedTest(name = "{0}, numeric={3}, integral={4}")
    @MethodSource("getSourceStream")
    void testExpression(String testName, Properties properties, Supplier<InputStream> dataReader, Type<?> numericType, Type<?> integralNumericType)
            throws IOException, ParseException {
        try(
                InputStream data = dataReader.get()
        ) {
            IntegrationTestContext context = new IntegrationTestContext(properties, data, numericType, integralNumericType);
            Expression expression = compile(context.getSource(), context.getEvaluationVariables());

            // Execute the program
            execute(expression, context.getInterval());

            // Compare results
            compare(context.getExpectedVariables(), context.getEvaluationVariables());
        }
    }

    /**
     * Retrieve all the tests from the integration resource directory. Note that this method supports both file as well as jar resolution, as one might depend
     * on the test jar to run integration tests for another variable backend.
     *
     * @return Stream of arguments with all the tests
     */
    private static Stream<Arguments> getSourceStream() throws IOException {
        String[] limit = {/* "filter" */};

        val url = Thread.currentThread().getContextClassLoader().getResource(INTEGRATION_DIR);

        Map<String, Pair<Supplier<InputStream>, Supplier<InputStream>>> tests = new HashMap<>();

        if(url.getProtocol().equals("file")) {
            Arrays.stream(Objects.requireNonNull(new File(URLDecoder.decode(url.getFile(), "UTF-8")).listFiles()))
                    .filter(File::isDirectory)
                    .filter(file -> limit.length == 0 || Arrays.stream(limit).anyMatch(l -> file.getName().toLowerCase().contains(l.toLowerCase())))
                    .map(File::toPath)
                    .forEach(directory -> {
                        tests.put(
                                directory.getFileName().toString(),
                                new Pair<>(
                                        () -> localFileSupplier(directory.resolve(TEST_PROPERTIES_FILE).toFile()),
                                        () -> localFileSupplier(directory.resolve(DATA_CSV_FILE).toFile())
                                )
                        );
                    });
        } else if(url.getProtocol().equals("jar")) {
            JarFile jar = new JarFile(URLDecoder.decode(
                    new File(url.getFile().substring(5, url.getFile().indexOf('!'))).toString(),
                    StandardCharsets.UTF_8.name()
            ));

            for(Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();

                if(entry.getName().startsWith("integration/") && !entry.getName().endsWith("/")) {
                    Path file = Paths.get(entry.getName());
                    String test = file.getParent().getFileName().toString();

                    Pair<Supplier<InputStream>, Supplier<InputStream>> current = tests.getOrDefault(test, new Pair<>(null, null)), next;

                    if(file.getFileName().toString().equals(TEST_PROPERTIES_FILE))
                        next = new Pair<>(
                                () -> jarFileSupplier(jar, entry),
                                current.getSecond()
                        );
                    else if(file.getFileName().toString().equals(DATA_CSV_FILE))
                        next = new Pair<>(
                                current.getFirst(),
                                () -> jarFileSupplier(jar, entry)
                        );
                    else
                        break;

                    tests.put(test, next);
                }
            }
        } else
            throw new UnsupportedOperationException("Cannot load integration tests from URL " + url);

        // Cross factor all the tests with the numeric types
        return tests.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(entry -> new Tuple3<>(
                        entry.getKey(),
                        readProperties(entry.getValue().getFirst()),
                        entry.getValue().getSecond()
                ))
                .flatMap(entry -> getNumericTypes(entry.getSecond()).map(entry::append))
                .map(entry -> Arguments.of(
                        entry.getFirst(),
                        entry.getSecond(),
                        entry.getThird(),
                        entry.getFourth().getFirst(),
                        entry.getFourth().getSecond()
                ));
    }

    private static Stream<Pair<Type<?>, Type<?>>> getNumericTypes(Properties properties) {
        String value = properties.containsKey("numericTypes") ?
                properties.get("numericTypes").toString() : "Integer,Float,Double";

        return Arrays.stream(value.split(","))
                .map(i -> {
                    switch(i) {
                        case "Integer":
                            return new Pair<>(new IntegerType(), new IntegralIntegerType(1));
                        case "Float":
                            return new Pair<Type<?>, Type<?>>(new FloatType(), new IntegralFloatType(1));
                        case "Double":
                            return new Pair<Type<?>, Type<?>>(new DoubleType(), new IntegralDoubleType(1));
                        default:
                            throw new IllegalArgumentException(i);
                    }
                });
    }

    private static Properties readProperties(Supplier<InputStream> inputStreamSupplier) {
        try {
            try(val is = inputStreamSupplier.get()) {
                val properties = new Properties();
                properties.load(is);
                return properties;
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <V> TreeMap<Interval, V> filterNull(TreeMap<Interval, V> map) {
        TreeMap<Interval, V> r = new TreeMap<>(IntervalMaps.getIntervalEndComparator());

        for(Map.Entry<Interval, V> entry : map.entrySet()) {
            if(entry.getValue() != null)
                r.put(
                        entry.getKey(),
                        entry.getValue()
                );
        }

        return r;
    }

    private void compare(Map<String, IntegrationTestContext.VariableInstance> expectedVariables, Map<String, IntegrationTestContext.VariableInstance> evaluationVariables) {
        // Extract the widest intervalStart/intervalStop from expected variables
        for(Map.Entry<String, IntegrationTestContext.VariableInstance> entry : expectedVariables.entrySet()) {
            String expectedId = entry.getKey();

            System.out.println("Compare: checking output variable " + expectedId);

            String outputId = expectedId.substring(0, expectedId.length() - IntegrationTestContext.OUTPUT_VARIABLE_SUFFIX.length());

            IntegrationTestContext.VariableInstance outputInstance = evaluationVariables.get(outputId);

            if(outputInstance == null)
                fail("Unable to find output variable " + outputId + " required by check variable " + expectedId);

            TreeMap<Interval, Object> expectedMap = filterNull((toMap((Variable<Object>) entry.getValue().variable))),
                    outputMap = filterNull(toMap((Variable<Object>) outputInstance.variable));

            boolean ok = expectedMap.size() == outputMap.size() && expectedMap.keySet().equals(outputMap.keySet());

            if(!ok) {
                System.out.println("Expected  => " + expectedMap);
                System.out.println("Actual    => " + outputMap);
            }

            assertEquals(expectedMap.size(), outputMap.size());

            assertEquals(expectedMap.keySet(), outputMap.keySet());

            for(Map.Entry<Interval, Object> outputEntry : outputMap.entrySet()) {
                Object expected = expectedMap.get(outputEntry.getKey());
                Object actual = outputEntry.getValue();

                if(!expected.getClass().equals(actual.getClass()))
                    fail("Failed result " + expected.getClass() + ", got " + actual.getClass());

                double diff;

                if(expected.getClass() == Float.class) {
                    diff = Math.abs((Float) expected - (Float) actual);
                } else if(expected.getClass() == Double.class) {
                    diff = Math.abs((Double) expected - (Double) actual);
                } else if(expected.getClass() == Integer.class) {
                    diff = Math.abs((Integer) expected - (Integer) actual);
                } else
                    diff = expected.equals(actual) ? 0.0 : 2.0;

                if(diff > epsilon.getOrDefault(expected.getClass(), 0d))
                    fail("Failed result check for interval " + outputEntry.getKey() + " expected " + expected + ", got " + actual);
            }

            System.out.println("Compare: " + expectedId + " check ok");
        }
    }

    private <V> TreeMap<Interval, V> toMap(Variable<V> variable) {
        TreeMap<Interval, V> map = new TreeMap<Interval, V>(IntervalMaps.getIntervalEndComparator());

        TimeIterator<V> iter = variable.readForward(Interval.of(
                Long.MIN_VALUE,
                Long.MAX_VALUE
        ), null);

        while(iter.hasNext()) {
            Sample<V> sample = iter.next();

            map.put(
                    sample.getInterval(),
                    sample.getValue()
            );
        }

        return map;
    }

    private void execute(Expression expression, Interval evaluationInterval) {
        System.out.println("Evaluate: evaluating in interval " + evaluationInterval);

        // Execute the expression
        val itor = TimEL.evaluate(expression, evaluationInterval);

        while(itor.hasNext())
            itor.next();
    }

    private Expression compile(String source, Map<String, IntegrationTestContext.VariableInstance> variables) throws ParseException {
        CompilerBuilder compiler = TimEL.parse(source);

        compiler = compiler.withVariableFactory((id, type) -> {
            IntegrationTestContext.VariableInstance instance = new IntegrationTestContext.VariableInstance();

            Variable<Object> variable = newVariable(id);

            instance.id = id;
            instance.type = type;
            instance.variable = variable;

            variables.put(id, instance);

            return variable;
        });

        for(Map.Entry<String, IntegrationTestContext.VariableInstance> entry : variables.entrySet()) {
            String id = entry.getKey();
            System.out.println("Compile: adding external variable " + id);
            IntegrationTestContext.VariableInstance details = entry.getValue();
            compiler = compiler.withVariable(id, details.type, details.variable);
        }

        return compiler.compile();
    }

    private static FileInputStream localFileSupplier(File file) {
        try {
            return new FileInputStream(file);
        } catch(FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream jarFileSupplier(JarFile jar, JarEntry entry) {
        try {
            return jar.getInputStream(entry);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * One can inherit this class and override this method, to test new backends with the standard integration test suite.
     *
     * @param id
     * @param <V>
     * @return
     */
    protected <V> Variable<V> newVariable(String id) {
        return new TreeMapVariable<>();
    }
}
