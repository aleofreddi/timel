/*
 * Copyright 2014-2016 Andrea Leofreddi
 *
 * This file is part of TimEL.
 *
 * TimEL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TimEL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TimEL.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.vleo.timel;

import net.vleo.timel.compiler.CompilerBuilder;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.Program;
import net.vleo.timel.csv.CsvReader;
import net.vleo.timel.executor.ExecutionException;
import net.vleo.timel.executor.ProgramExecutorBuilder;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.executor.variable.Variable;
import net.vleo.timel.executor.variable.VariableFactory;
import net.vleo.timel.impl.expression.evaluate.variable.TreeMapVariable;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.IntervalMaps;
import net.vleo.timel.type.Types;
import net.vleo.timel.type.ValueType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Run various test cases from a CSV files.
 *
 * @author Andrea Leofreddi
 */
@RunWith(Parameterized.class)
public class EndToEndTest {
    private static final double EPSILON = 0.001;

    private static final String OUTPUT_VARIABLE_SUFFIX = "*";

    private static final String[] DATE_TIME_FORMATS = new String[]{
            "dd/MM/yyyy HH:mm:ss.SSS",
            "dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm",
    };

    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = new DateTimeFormatter[DATE_TIME_FORMATS.length];

    static {
        for(int i = 0; i < DATE_TIME_FORMATS.length; i++)
            DATE_TIME_FORMATTERS[i] = DateTimeFormat.forPattern(DATE_TIME_FORMATS[i]);
    }

    private enum ParserState {
        HEADER, DATA
    }

    private static class VariableInstance {
        String id;

        ValueType<?> type;

        Variable<?> variable;
    }

    // The file being tested
    private File testFile;

    // Name of the test
    private String testName;

    // The expression being tested
    private String source;

    // The evaluation interval
    private Interval evaluationInterval;

    // The variables declared
    private HashMap<String, VariableInstance> variables = new HashMap<String, VariableInstance>();

    // Compiled program
    private Program program;

    @Parameters(name = "{1}")
    public static Collection<Object[]> data() throws UnsupportedEncodingException {
        File testDirFile = new File(URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource("expression-tests").getFile(), "UTF-8"));

        List<Object[]> rows = new ArrayList<Object[]>();

        String[] limit = {/* "filter" */};

        if(limit.length > 0) {
            for(File testFile : testDirFile.listFiles())
                for(String s : limit)
                    if(testFile.getName().toLowerCase().contains(s.toLowerCase())) {
                        rows.add(new Object[]{testFile, testFile.getName()});

                        break;
                    }
        } else {
            // Add all the files
            File[] files = testDirFile.listFiles();

            if(files == null)
                files = new File[0];

            Arrays.sort(files);

            for(File testFile : files)
                rows.add(new Object[]{testFile, testFile.getName()});

        }

        return rows;
    }

    public EndToEndTest(File testFile, String testFileName /* used only to name test */) {
        super();

        this.testFile = testFile;

        this.testName = testFile.getName();
    }

    private TreeMap<Interval, Double> filterNull(TreeMap<Interval, Double> map) {
        TreeMap<Interval, Double> r = new TreeMap<Interval, Double>(IntervalMaps.getIntervalEndComparator());

        for(Map.Entry<Interval, Double> entry : map.entrySet()) {
            if(entry.getValue() != null)
                r.put(
                        entry.getKey(),
                        entry.getValue()
                );
        }

        return r;
    }

    private void parseFile() throws IOException {
        ParserState state = ParserState.HEADER;

        DateTimeZone tz = DateTimeZone.getDefault();

        List<List<String>> records = CsvReader.read(testFile);

        Long forcedIntervalStart = null, forcedIntervalStop = null;

        // Parse CSV test file
        int line = 1;

        for(List<String> record : records) {
            if(!record.isEmpty() && record.get(0).startsWith("#") || record.isEmpty())
                // Ignore comments and empty lines
                continue;

            switch(state) {
                case HEADER: {
                    if(record.size() != 1)
                        throw new IllegalArgumentException(testName + ": wrong header directive at line " + line);

                    String e = record.get(0);

                    if(e.equals("data")) {
                        // Switch to data mode
                        state = ParserState.DATA;

                        continue;
                    }

                    int delimiter = e.indexOf('=');

                    String key = e.substring(0, delimiter), value = e.substring(delimiter + 1);

                    if("program".equals(key))
                        source = value;
                    else if("timeZone".equals(key))
                        tz = DateTimeZone.forID(value);
                    else if("startTime".equals(key))
                        forcedIntervalStart = parseDateTime(tz, value).getMillis();
                    else if("stopTime".equals(key))
                        forcedIntervalStop = parseDateTime(tz, value).getMillis();
                    else
                        throw new IllegalArgumentException(testName + ": unexpected key: " + key);

                    break;
                }

                case DATA: {
                    if(record.size() != 4)
                        throw new IllegalArgumentException(testName + ": wrong data row at line " + line);

                    String id = record.get(0), startStr = record.get(1), stopStr = record.get(2), valueStr = record.get(3);

                    DateTime start = parseDateTime(tz, startStr), stop = parseDateTime(tz, stopStr);

                    // Sometimes Excel uses , as a separator
                    valueStr = valueStr.replaceAll(",", ".");

                    double value = Double.parseDouble(valueStr);

                    // Variable
                    VariableInstance variable = variables.get(id);

                    if(variable == null) {
                        ValueType<Double> type;

                        if(id.startsWith("I"))
                            type = Types.getIntegralDoubleType(1);
                        else
                            type = Types.getNumericDoubleType();

                        variable = new VariableInstance();

                        variable.id = id;
                        variable.type = type;
                        variable.variable = newVariable(id);

                        System.out.println("Loaded variable " + id + " with type " + type);

                        variables.put(id, variable);
                    }

                    try {
                        Interval i = Interval.of(start.getMillis(), stop.getMillis());

                        ((Variable<Double>) variable.variable).write(
                                Sample.of(
                                        i,
                                        value
                                ), null
                        );
                    } catch(Throwable t) {
                        throw new IllegalArgumentException("Failed to add value at " + testName + ":" + line + ": <" + start + ", " + stop + ">", t);
                    }

                    break;
                }
            }

            line++;
        }

        // Set evaluation interval
        {
            Long intervalStart = null, intervalStop = null;

            // Extract the widest intervalStart/intervalStop from output variables
            for(Map.Entry<String, VariableInstance> entry : variables.entrySet()) {
                String id = entry.getKey();

                if(!id.endsWith(OUTPUT_VARIABLE_SUFFIX))
                    // Consider only output variables
                    continue;

                VariableInstance result = entry.getValue();

                TimeIterator<?> iter = result.variable.readForward(Interval.of(
                        Long.MIN_VALUE,
                        Long.MAX_VALUE
                ), null);

                Interval i = iter.next().getInterval();

                long start = i.getStart(), stop = i.getEnd();

                while(iter.hasNext())
                    stop = iter.next().getInterval().getEnd();

                if(intervalStart == null || intervalStart > start)
                    intervalStart = start;

                if(intervalStop == null || intervalStop > stop)
                    intervalStop = stop;
            }

            if(forcedIntervalStart != null)
                intervalStart = forcedIntervalStart;

            if(forcedIntervalStop != null)
                intervalStop = forcedIntervalStop;

            evaluationInterval = Interval.of(intervalStart, intervalStop);
        }
    }

    private DateTime parseDateTime(DateTimeZone tz, String s) {
        for(DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            DateTimeFormatter f = formatter.withZone(tz);

            try {
                return f.parseDateTime(s);
            } catch(Exception e) {
                // Do nothing
            }
        }

        throw new IllegalArgumentException("Unparseable date time: " + s);
    }

    /**
     * Factory method to instance variables.
     *
     * @param id
     * @param <V>
     * @return
     */
    protected <V> Variable<V> newVariable(String id) {
        return new TreeMapVariable<V>();
    }

    /**
     * Read, parse and executes the CSV tests from expression-tests directory in test resources.
     *
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws ParseException
     * @throws ExecutionException
     */
    @Test
    public void testExpression() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, ParseException {
        String testName = testFile.getName();

        System.out.println(testName + ": starting test");

        // Parse the CSV file
        parseFile();

        // Compile the program
        compile();

        // Execute the program
        execute();

        // Compare results
        compare();

        System.out.println(testName + ": test file executed with success");
    }

    private void compare() {
        // Extract the widest intervalStart/intervalStop from output variables
        for(Map.Entry<String, VariableInstance> entry : variables.entrySet()) {
            String expectedId = entry.getKey();

            if(!expectedId.endsWith(OUTPUT_VARIABLE_SUFFIX))
                // Skip input (or unbound) variables
                continue;

            System.out.println("Compare: checking output variable " + expectedId);

            String outputId = expectedId.substring(0, expectedId.length() - OUTPUT_VARIABLE_SUFFIX.length());

            VariableInstance outputInstance = variables.get(outputId);

            if(outputInstance == null)
                Assert.fail("Unable to find output variable " + outputId + " required by check variable " + expectedId);

            TreeMap<Interval, Double> expected = filterNull((toMap((Variable<Double>) entry.getValue().variable))),
                    output = filterNull(toMap((Variable<Double>) outputInstance.variable));

            boolean ok = expected.size() == output.size() && expected.keySet().equals(output.keySet());

            if(!ok) {
                System.out.println("Expected  => " + expected);
                System.out.println("Actual    => " + output);
            }

            Assert.assertEquals(expected.size(), output.size());

            Assert.assertEquals(expected.keySet(), output.keySet());

            for(Map.Entry<Interval, Double> outputEntry : output.entrySet()) {
                Double expectedValues = expected.get(outputEntry.getKey());

                Double outputValues = outputEntry.getValue();

                Double diff = Math.abs(expectedValues - outputValues);

                if(diff > EPSILON)
                    Assert.fail("Failed result check for interval " + outputEntry.getKey() + " expected " + expectedValues + " but got " + outputValues);
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

    private void execute() {
        ProgramExecutorBuilder executor = TimEL
                .getExecutor(program)

                // Autobind variables and register them to variables map
                .autoBind(new VariableFactory() {
                    @Override
                    public <V> Variable<V> newVariable(String id, ValueType<V> type) {
                        VariableInstance instance = new VariableInstance();

                        Variable<V> variable = EndToEndTest.this.newVariable(id);

                        instance.id = id;
                        instance.type = type;
                        instance.variable = variable;

                        variables.put(
                                id,
                                instance
                        );

                        return variable;
                    }
                });

        for(Map.Entry<String, VariableInstance> entry : variables.entrySet()) {
            String id = entry.getKey();

            if(id.endsWith(OUTPUT_VARIABLE_SUFFIX))
                // Skip output variables
                continue;

            System.out.println("Evaluate: binding variable " + id);

            executor = executor.bindVariable(
                    id,
                    entry.getValue().variable
            );
        }

        System.out.println("Evaluate: evaluating in interval " + evaluationInterval);

        executor
                // Execute the program
                .executeFor(evaluationInterval);
    }

    private void compile() throws ParseException {
        CompilerBuilder compiler = TimEL.getCompiler(source);

        for(Map.Entry<String, VariableInstance> entry : variables.entrySet()) {
            String id = entry.getKey();

            if(id.endsWith(OUTPUT_VARIABLE_SUFFIX))
                // This is an output variable, skip
                continue;

            System.out.println("Compile: adding external variable " + id);

            VariableInstance variableInstance = entry.getValue();

            compiler = compiler.declareVariable(
                    id,
                    variableInstance.type
            );
        }

        program = compiler.compileProgram();
    }
}
