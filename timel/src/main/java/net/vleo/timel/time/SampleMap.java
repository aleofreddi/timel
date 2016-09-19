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
package net.vleo.timel.time;

import net.vleo.timel.iterator.TimeIterator;

import java.util.*;

/**
 * A SampleMap is a commodity class to manage collections of samples.
 *
 * @author Andrea Leofreddi
 */
public class SampleMap<T> implements NavigableMap<Interval, T> {
    private final SampleMap<T> parent;

    private final NavigableMap<Interval, T> data;

    private int version;

    public SampleMap() {
        this.parent = null;

        this.data = new TreeMap<Interval, T>(IntervalMaps.getIntervalEndComparator());
    }

    private SampleMap(SampleMap<T> parent, NavigableMap<Interval, T> data) {
        this.parent = parent;
        this.data = data;
    }

    private void incrementVersion() {
        version++;

        SampleMap<T> p = parent;

        while(p != null) {
            p.version++;

            p = p.parent;
        }
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean containsKey(Object o) {
        return data.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return data.containsValue(o);
    }

    @Override
    public T get(Object o) {
        return data.get(o);
    }

    @Override
    public Comparator<? super Interval> comparator() {
        return data.comparator();
    }

    @Override
    public Interval firstKey() {
        return data.firstKey();
    }

    @Override
    public Interval lastKey() {
        return data.lastKey();
    }

    @Override
    public void putAll(Map<? extends Interval, ? extends T> map) {
        incrementVersion();

        data.putAll(map);
    }

    @Override
    public T put(Interval interval, T t) {
        incrementVersion();

        return data.put(interval, t);
    }

    @Override
    public T remove(Object o) {
        incrementVersion();

        return data.remove(o);
    }

    @Override
    public void clear() {
        incrementVersion();

        data.clear();
    }

    @Override
    public SampleMap<T> clone() {
        TreeMap<Interval, T> newData = new TreeMap<Interval, T>(IntervalMaps.getIntervalEndComparator());

        newData.putAll(data);

        return new SampleMap<T>(
                null,
                newData
        );
    }

    @Override
    public Entry<Interval, T> firstEntry() {
        return data.firstEntry();
    }

    @Override
    public Entry<Interval, T> lastEntry() {
        return data.lastEntry();
    }

    @Override
    public Entry<Interval, T> pollFirstEntry() {
        incrementVersion();

        return data.pollFirstEntry();
    }

    @Override
    public Entry<Interval, T> pollLastEntry() {
        incrementVersion();

        return data.pollLastEntry();
    }

    @Override
    public Entry<Interval, T> lowerEntry(Interval interval) {
        return data.lowerEntry(interval);
    }

    @Override
    public Interval lowerKey(Interval interval) {
        return data.lowerKey(interval);
    }

    @Override
    public Entry<Interval, T> floorEntry(Interval interval) {
        return data.floorEntry(interval);
    }

    @Override
    public Interval floorKey(Interval interval) {
        return data.floorKey(interval);
    }

    @Override
    public Entry<Interval, T> ceilingEntry(Interval interval) {
        return data.ceilingEntry(interval);
    }

    @Override
    public Interval ceilingKey(Interval interval) {
        return data.ceilingKey(interval);
    }

    @Override
    public Entry<Interval, T> higherEntry(Interval interval) {
        return data.higherEntry(interval);
    }

    @Override
    public Interval higherKey(Interval interval) {
        return data.higherKey(interval);
    }

    @Override
    public Set<Interval> keySet() {
        return data.keySet();
    }

    @Override
    public NavigableSet<Interval> navigableKeySet() {
        return data.navigableKeySet();
    }

    @Override
    public NavigableSet<Interval> descendingKeySet() {
        return data.descendingKeySet();
    }

    @Override
    public Collection<T> values() {
        return data.values();
    }

    @Override
    public Set<Entry<Interval, T>> entrySet() {
        return data.entrySet();
    }

    @Override
    public NavigableMap<Interval, T> descendingMap() {
        return data.descendingMap();
    }

    @Override
    public SampleMap<T> subMap(Interval interval, boolean b, Interval k1, boolean b1) {
        return new SampleMap<T>(
                this,
                data.subMap(interval, b, k1, b1)
        );
    }

    @Override
    public SampleMap<T> headMap(Interval interval, boolean b) {
        return new SampleMap<T>(
                this,
                data.headMap(interval, b)
        );
    }

    @Override
    public SampleMap<T> tailMap(Interval interval, boolean b) {
        return new SampleMap<T>(
                this,
                data.tailMap(interval, b)
        );
    }

    @Override
    public SampleMap<T> subMap(Interval interval, Interval k1) {
        return new SampleMap<T>(
                this,
                data.subMap(interval, true, k1, false)
        );
    }

    @Override
    public SampleMap<T> headMap(Interval interval) {
        return new SampleMap<T>(
                this,
                data.headMap(interval, false)
        );
    }

    @Override
    public SampleMap<T> tailMap(Interval interval) {
        return new SampleMap<T>(
                this,
                data.tailMap(interval, true)
        );
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        return data.equals(o);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public String toString() {
        return data.toString();
    }

    /**
     * Access the version of this map.
     *
     * @return The version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Retrieve a forward time iterator.
     *
     * @param interval
     * @return
     */
    public TimeIterator<T> forwardTimeIterator(Interval interval) {
        return new SampleMapForwardIterator<T>(this, interval);
    }

    /**
     * Retrieve a forward time iterator.
     *
     * @param interval
     * @return
     */
    public TimeIterator<T> backwardTimeIterator(Interval interval) {
        return new SampleMapBackwardIterator<T>(this, interval);
    }
}
