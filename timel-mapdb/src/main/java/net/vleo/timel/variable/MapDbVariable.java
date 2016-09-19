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

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.executor.variable.Variable;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.IntervalMaps;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * A variable backed by a MapDB database.
 *
 * @author Andrea Leofreddi
 */
public class MapDbVariable<V> implements Variable<V> {
    /**
     * MapDB value type. This class acts like Java 8's Optional, and is added to ensure
     * null values are storable (as they represent an already evaluated, but empty, interval).
     */
    private static class Payload<T> implements Serializable {
        private final T payload;

        public T getPayload() {
            return payload;
        }

        private Payload(T payload) {
            this.payload = payload;
        }

        public static <V> Payload<V> of(V value) {
            return new Payload<V>(value);
        }
    }

    private static DB getDefaultDb() {
        File dbFile;

        try {
            dbFile = File.createTempFile("timel-variable", ".tmp");
        } catch(IOException e) {
            throw new RuntimeException(e);
        }

        return DBMaker
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

    private final DB db;

    private final ConcurrentNavigableMap<Interval, Payload<V>> values;

    /**
     * Construct a MapDbVariable backed by its own private database.
     */
    public MapDbVariable() {
        this(getDefaultDb(), "variable");
    }

    /**
     * Constructs a MapDbVariable backed by an existing MapDB database.
     *
     * @param db   The backend database
     * @param name The collection name to be used in the DB
     */
    public MapDbVariable(DB db, String name) {
        this.db = db;

        values = db.createTreeMap(name)
                .comparator(IntervalMaps.getIntervalEndComparator())
                .make()
        ;
    }

    /**
     * Retrieve the MapDb DB instance backing this variable.
     *
     * @return MapDb's backend DB instance
     */
    public DB getDb() {
        return db;
    }

    @Override
    public TimeIterator<V> readForward(Interval interval, ExecutorContext context) {
        return new AdapterTimeIterator<Payload<V>, V>(
                IntervalMaps.supremum(
                        values,
                        interval
                )
        ) {
            @Override
            protected Sample<V> adapt(Sample<Payload<V>> sample) {
                return sample.copyWithValue(
                        sample.getValue().getPayload()
                );
            }
        };
    }

    @Override
    public TimeIterator<V> readBackward(Interval interval, ExecutorContext context) {
        return new AdapterTimeIterator<Payload<V>, V>(
                IntervalMaps.supremumBackward(
                        values,
                        interval
                )
        ) {
            @Override
            protected Sample<V> adapt(Sample<Payload<V>> sample) {
                return sample.copyWithValue(
                        sample.getValue().getPayload()
                );
            }
        };
    }

    @Override
    public void write(Sample<V> sample, ExecutorContext context) {
        values.put(
                sample.getInterval(),
                Payload.of(sample.getValue())
        );
    }
}
