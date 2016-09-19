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
package net.vleo.timel.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

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
     * Static method to parse a CSV file into an List<List<String>>.
     *
     * @param dataFile
     * @return
     * @throws IOException
     */
    public static List<List<String>> read(File dataFile) throws IOException {
        List<List<String>> results = new ArrayList<List<String>>();

        FileReader reader = new FileReader(dataFile);

        try {
            Iterable<CSVRecord> records = CSVFormat.EXCEL.withDelimiter(';').parse(reader);

            for(CSVRecord record : records) {
                ArrayList<String> values = new ArrayList<String>();

                for(Iterator<String> itor = record.iterator(); itor.hasNext(); ) {
                    String value = itor.next();

                    if(value != null && !value.isEmpty())
                        values.add(value);
                }

                results.add(values);
            }
        } finally {
            reader.close();
        }

        return results;
    }
}
