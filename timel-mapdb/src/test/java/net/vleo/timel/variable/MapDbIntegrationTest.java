package net.vleo.timel.variable;

/*-
 * #%L
 * TimEL MapDB backend
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

import net.vleo.timel.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Andrea Leofreddi
 */
class MapDbIntegrationTest extends IntegrationTest {
    private DB db;
    private AtomicInteger counter = new AtomicInteger(0);

    @TempDir
    protected Path testFolder;

    @BeforeEach
    private void setup() {
        File dbFile = testFolder.resolve("mapdb").toFile();

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

                .make();
    }

    @Override
    protected <V> Variable<V> newVariable(String id) {
        return new MapDbVariable<>(db, "variable_" + counter.incrementAndGet());
    }
}
