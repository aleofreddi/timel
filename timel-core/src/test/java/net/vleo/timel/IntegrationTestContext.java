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

import lombok.Value;
import net.vleo.timel.csv.CsvReader;
import net.vleo.timel.variable.TreeMapVariable;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.Sample;
import net.vleo.timel.type.*;
import net.vleo.timel.variable.Variable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * A class to parse an integration test scenario from a given directory.
 * <p>
 * This class will take care of loading the csv data as well as the test properties.
 *
 * @author Andrea Leofreddi
 */
@Value
public class IntegrationTestContext {
    public static final String OUTPUT_VARIABLE_SUFFIX = "*";

    public static class VariableInstance {
        String id;
        Type type;
        Variable<?> variable;
    }

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = of(
            "dd/MM/yyyy HH:mm:ss.SSS",
            "dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm"
    )
            .map(DateTimeFormat::forPattern)
            .collect(toList());

    private final String source;
    private final Interval interval;
    private final HashMap<String, VariableInstance> evaluationVariables = new HashMap<>(), expectedVariables = new HashMap<>();

    /**
     * Parse an integration test context from the given test home directory.
     *
     * @throws IOException
     */
    public IntegrationTestContext(InputStream meta, InputStream data) throws IOException {
        DateTimeZone tz = DateTimeZone.getDefault();
        List<List<String>> records = CsvReader.read(data);
        Long forcedIntervalStart = null, forcedIntervalStop = null;

        // Load test properties
        Properties properties = new Properties();
        properties.load(meta);

        // Parse test properties
        source = properties.get("expression").toString();

        if(properties.containsKey("timeZone"))
            tz = DateTimeZone.forID(properties.get("timeZone").toString());
        if(properties.containsKey("startTime"))
            forcedIntervalStart = parseDateTime(tz, properties.get("startTime").toString()).getMillis();
        if(properties.containsKey("stopTime"))
            forcedIntervalStop = parseDateTime(tz, properties.get("stopTime").toString()).getMillis();

        // Parse CSV test file
        Iterator<List<String>> itor = records.iterator();
        assertThat(itor.next(), is(Arrays.asList("variable", "from", "to", "value")));
        for(int line = 1; itor.hasNext(); line++) {
            List<String> record = itor.next();

            // Ignore empty lines or comments
            if(record.isEmpty() || record.get(0).startsWith("#"))
                continue;

            if(record.size() != 4)
                throw new IllegalArgumentException("Wrong data row at line " + line);

            String id = record.get(0), startStr = record.get(1), stopStr = record.get(2), valueStr = record.get(3);

            DateTime start = parseDateTime(tz, startStr), stop = parseDateTime(tz, stopStr);

            // Sometimes Excel uses , as a separator
            valueStr = valueStr.replaceAll(",", ".");

            // Variable
            VariableInstance variable = evaluationVariables.get(id) == null ? expectedVariables.get(id) : evaluationVariables.get(id);

            if(variable == null) {
                Type type;

                if(id.startsWith("boolean_"))
                    type = new BooleanType();
                else if(id.startsWith("I"))
                    type = new IntegralFloatType(1);
                else
                    type = new FloatType();

                variable = new VariableInstance();

                variable.id = id;
                variable.type = type;
                variable.variable = new TreeMapVariable<>();

                System.out.println("Loaded " + type + " variable " + id);

                if(id.endsWith(OUTPUT_VARIABLE_SUFFIX))
                    expectedVariables.put(id, variable);
                else
                    evaluationVariables.put(id, variable);
            }

            Object value = parseValue(valueStr, variable.type);

            try {
                Interval i = Interval.of(start.getMillis(), stop.getMillis());

                ((Variable<Object>) variable.variable).write(Sample.of(i, value), null);
            } catch(Throwable t) {
                throw new IllegalArgumentException("Failed to add value at line " + line + ": <" + start + ", " + stop + ">", t);
            }
        }

        // Set evaluation interval
        {
            Long intervalStart = null, intervalStop = null;

            // Extract the widest intervalStart/intervalStop from output variables
            for(Map.Entry<String, VariableInstance> entry : expectedVariables.entrySet()) {
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

            interval = Interval.of(intervalStart, intervalStop);
        }
    }

    private Object parseValue(String valueStr, Type type) {
        if(type.equals(new BooleanType()))
            return "1".equals(valueStr) || "true".equals(valueStr.toLowerCase());
        else if(type.equals(new FloatType())
                || type.template().equals(new IntegralFloatType()))
            return Float.parseFloat(valueStr);
        else if(type.equals(new DoubleType())
                || type.template().equals(new IntegralDoubleType()))
            return Double.parseDouble(valueStr);

        throw new IllegalStateException("Unknown value type " + type);
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

        throw new IllegalArgumentException("Invalid timestamp: " + s);
    }
}
