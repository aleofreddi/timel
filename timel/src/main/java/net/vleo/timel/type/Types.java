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
package net.vleo.timel.type;

import java.util.Collection;

/**
 * Static methods only class for type commodity functions.
 *
 * Use this class to obtain types as well.
 *
 * @author Andrea Leofreddi
 */
public class Types {
    private Types() {
        throw new AssertionError();
    }

    public static Type getType() {
        return new Type(true);
    }

    public static StatementType getStatementType() {
        return new StatementType(true);
    }

    public static ValueType<?> getValueType() {
        return new ValueType<Object>(true);
    }

    public static NumericType<?> getNumericType() {
        return new NumericType<Number>(true);
    }

    public static NumericDoubleType getNumericDoubleType() {
        return new NumericDoubleType();
    }

    public static NumericBigDecimalType getNumericBigDecimalType() {
        return new NumericBigDecimalType();
    }

    public static IntegralType<?> getIntegralType() {
        return new IntegralType<Number>(true);
    }

    public static IntegralType<?> getIntegralType(int degree) {
        return new IntegralType<Number>(true, degree);
    }

    public static IntegralDoubleType getIntegralDoubleType() {
        return new IntegralDoubleType();
    }

    public static IntegralDoubleType getIntegralDoubleType(int degree) {
        return new IntegralDoubleType(degree);
    }

    public static IntervalType getIntervalType() {
        return new IntervalType();
    }

    public static TimeType getTimeType() {
        return new TimeType();
    }

    public static ArrayType getArrayType(Collection<ValueType<?>> innerTypes) {
        return new ArrayType(innerTypes);
    }

    public static PeriodType getPeriodType() {
        return new PeriodType();
    }

    public static StringType getStringType() {
        return new StringType();
    }

    public static MetaType getMetaType() {
        return new MetaType();
    }
}
