package net.vleo.timel.csv;

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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Trivial facade to parse a CSV to read a whole CSV into a List
 *
 * @author Andrea Leofreddi
 */
public class CsvReader {
    private CsvReader() {
        throw new AssertionError();
    }

    /**
     * Static method to parse a CSV file into an list of records.
     *
     * @param stream
     * @return
     * @throws IOException
     */
    public static List<List<String>> read(InputStream stream) throws IOException {
        List<List<String>> results = new ArrayList<>();

        try(Reader reader = new InputStreamReader(stream)) {
            Iterable<CSVRecord> records = CSVFormat.EXCEL.withDelimiter(';').parse(reader);

            for(CSVRecord record : records) {
                ArrayList<String> values = new ArrayList<>();

                for(Iterator<String> itor = record.iterator(); itor.hasNext(); ) {
                    String value = itor.next();

                    if(value != null && !value.isEmpty())
                        values.add(value);
                }

                results.add(values);
            }
        }

        return results;
    }
}
