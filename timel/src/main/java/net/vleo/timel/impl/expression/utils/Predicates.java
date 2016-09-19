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
package net.vleo.timel.impl.expression.utils;

import net.vleo.timel.type.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A re-implementation of the Predicates class.
 * <p/>
 * This is to avoid dependency on Guava.
 *
 * @author Andrea Leofreddi
 */
public class Predicates {
    private Predicates() {
        throw new AssertionError();
    }

    public static <T> Predicate<T> true_() {
        return new Predicate<T>() {
            @Override
            public boolean apply(T input) {
                return true;
            }
        };
    }

    public static <T> Predicate<T> false_() {
        return new Predicate<T>() {
            @Override
            public boolean apply(T input) {
                return false;
            }
        };
    }

    private static <T> List<Predicate<T>> merge(Predicate<T> first, Predicate<T> second, Predicate<T>... predicates) {
        ArrayList<Predicate<T>> merged = new ArrayList<Predicate<T>>(predicates.length + 2);

        merged.add(first);
        merged.add(second);
        merged.addAll(Arrays.asList(predicates));

        return merged;
    }

    public static <T> Predicate<T> and(final Predicate<T> first, final Predicate<T> second, final Predicate<T>... predicates) {
        return new Predicate<T>() {
            @Override
            public boolean apply(T input) {
                if(!first.apply(input))
                    return false;

                if(!second.apply(input))
                    return false;

                for(Predicate<T> predicate : predicates)
                    if(!predicate.apply(input))
                        return false;

                return true;
            }

            @Override
            public String toString() {
                return "(" + Joiner.on(" and ").join(merge(first, second, predicates)) + ")";
            }
        };
    }

    public static <T> Predicate<T> or(final Predicate<T> first, final Predicate<T> second, final Predicate<T>... predicates) {
        return new Predicate<T>() {
            @Override
            public boolean apply(T input) {
                if(first.apply(input))
                    return true;

                if(second.apply(input))
                    return true;

                for(Predicate<T> predicate : predicates)
                    if(predicate.apply(input))
                        return true;

                return false;
            }

            @Override
            public String toString() {
                return "(" + Joiner.on(" or ").join(merge(first, second, predicates)) + ")";
            }
        };
    }

    public static <T> Predicate<T> not(final Predicate<T> predicate) {
        return new Predicate<T>() {
            @Override
            public boolean apply(T input) {
                return !predicate.apply(input);
            }

            @Override
            public String toString() {
                return "not " + predicate;
            }
        };
    }
}
