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
package net.vleo.timel.impl.type;

import net.vleo.timel.impl.expression.utils.Predicate;
import net.vleo.timel.impl.expression.utils.Predicates;
import net.vleo.timel.type.ArrayType;
import net.vleo.timel.type.Type;
import net.vleo.timel.type.Types;
import net.vleo.timel.type.ValueType;

import java.util.Collection;

/**
 * Static method class with commodity functions for NodeDataType.
 *
 * @author Andrea.Leofreddi
 */
public class Utils {
    public static final String ARRAY_TYPE_NAME = "Array";
    private static boolean metaTypePredicate;

    private static Predicate<Type> isPredicate(final Type type) {
        return new Predicate<Type>() {
            @Override
            public boolean apply(Type input) {
                return type.isAssignableFrom(input);
            }

            @Override
            public String toString() {
                return type.toString();
            }
        };
    }

    public static Predicate<Type> isArrayPredicate() {
        return new Predicate<Type>() {
            @Override
            public boolean apply(Type input) {
                return input instanceof ArrayType;
            }

            @Override
            public String toString() {
                return ARRAY_TYPE_NAME;
            }
        };
    }

    public static class SamePredicate implements Predicate<Type> {
        private Type type;

        public Type getType() {
            return type;
        }

        @Override
        public boolean apply(Type input) {
            if(input == null)
                throw new NullPointerException("Provided null type to isSamePredicate");

            if(type == null) {
                type = input;

                return true;
            }

            return type.isAssignableFrom(input);
        }

        @Override
        public String toString() {
            if(type != null)
                return "Same Type[" + type + "]";

            return "Same Type";
        }
    }

    public static boolean isArray(Type type) {
        return isArrayPredicate().apply(type);
    }

    public static Predicate<Type> isNumericIntegralPredicate() {
        return isPredicate(Types.getIntegralDoubleType());
    }

    public static boolean isNumericIntegral(Type type) {
        return isNumericIntegralPredicate().apply(type);
    }

    public static Predicate<Type> isNumericAveragePredicate() {
        return isPredicate(Types.getNumericType());
    }

    public static boolean isNumericAverage(Type type) {
        return isNumericAveragePredicate().apply(type);
    }

    public static Predicate<Type> isMetaTypePredicate() {
        return isPredicate(Types.getMetaType());
    }

    public static boolean isMetaType(Type type) {
        return isMetaTypePredicate().apply(type);
    }

    public static Predicate<Type> isPeriodPredicate() {
        return isPredicate(Types.getPeriodType());
    }

    public static boolean isPeriod(Type type) {
        return isPeriodPredicate().apply(type);
    }

    public static Predicate<Type> isDateTimePredicate() {
        return isPredicate(Types.getTimeType());
    }

    public static boolean isDateTime(Type type) {
        return isDateTimePredicate().apply(type);
    }

    public static Predicate<Type> isValuePredicate() {
        return new Predicate<Type>() {
            @Override
            public boolean apply(Type input) {
                return input instanceof ValueType;
            }
        };
    }

    public static boolean isValue(Type type) {
        return isValuePredicate().apply(type);
    }

    public static Predicate<Type> isStatementPredicate() {
        return isPredicate(Types.getStatementType());
    }

    public static boolean isStatement(Type type) {
        return isStatementPredicate().apply(type);
    }

    public static Predicate<Type> isStringPredicate() {
        return isPredicate(Types.getStringType());
    }

    public static boolean isString(Type type) {
        return isStringPredicate().apply(type);
    }

    public static Predicate<Type> isNumericPredicate() {
        return Predicates.or(
                isNumericIntegralPredicate(),
                isNumericAveragePredicate()
        );
    }

    public static boolean isNumeric(Type type) {
        return isNumericPredicate().apply(type);
    }

    public static Predicate<Type> isIntervalPredicate() {
        return isPredicate(Types.getIntervalType());
    }

    public static boolean isInterval(Type type) {
        return isIntervalPredicate().apply(type);
    }

    public static Predicate<Type> isComparablePredicate() {
        return Predicates.or(
                isNumericPredicate(),
                isStringPredicate(),
                isDateTimePredicate()
        );
    }

    public static boolean isComparable(Type type) {
        return isComparablePredicate().apply(type);
    }

    public static SamePredicate isSamePredicate() {
        return new SamePredicate();
    }

    public static Type toArrayType(Collection<ValueType<?>> types) {
        if(types == null || types.isEmpty())
            throw new IllegalArgumentException("Array type must contain at least one inner type");

        return Types.getArrayType(types);
    }

    public static Type getStatement() {
        return Types.getStatementType();
    }

    public static Type getDateTime() {
        return Types.getTimeType();
    }

    public static Type getNumericAverage() {
        return Types.getNumericType();
    }

    public static Type getNumericIntegral() {
        return Types.getIntegralDoubleType();
    }

    public static Type getInterval() {
        return Types.getIntervalType();
    }
}
