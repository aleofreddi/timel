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
package net.vleo.timel.variable;

import net.vleo.timel.EndToEndTest;
import net.vleo.timel.executor.variable.Variable;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.io.IOException;

/**
 * @author Andrea Leofreddi
 */
public class MapDbEndToEndTest extends EndToEndTest {
    public MapDbEndToEndTest(File testFile, String testFileName) {
        super(testFile, testFileName);
    }

    private DB db;

    private int i = 0;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Override
    protected <V> Variable<V> newVariable(String id) {
        if(db == null) {
            File dbFile = null;
            try {
                dbFile = testFolder.newFile("mapdb");
            } catch(IOException e) {
                throw new RuntimeException(e);
            }

            db = DBMaker
                    .fileDB(dbFile)

                    // Disable transactions
                    .transactionDisable()

                    // Use MMAP if supported
                    .fileMmapEnableIfSupported()

                    // Close the file on JVM shutdown
                    .closeOnJvmShutdown()

                    // Enable asynchronous writes
                    .asyncWriteEnable()
                    .asyncWriteFlushDelay(5000) // 5 secs

                    // Enable LRU cache
                    .cacheLRUEnable()
                    .cacheSize(32 * 1024)

                    .make()
            ;
        }

        return new MapDbVariable<V>(db, "var" + i++);
    }
}
