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
package net.vleo.timel.impl.expression.evaluate.function.resample;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.downscaler.AverageDownscaler;
import net.vleo.timel.impl.downscaler.IntegralDownscaler;
import net.vleo.timel.impl.expression.compile.FunctionArgumentParser;
import net.vleo.timel.impl.expression.evaluate.function.eval.DiracDelta;
import net.vleo.timel.impl.type.Utils;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.IntervalType;
import net.vleo.timel.type.Types;
import net.vleo.timel.type.ValueType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory for the Resample functions.
 *
 * @author Andrea Leofreddi
 */
public class ResampleFactory extends FunctionFactory {
    public static final String AVERAGE_TOKEN = "Average",
            INTEGRAL_TOKEN = "Integral",
            SCALAR_TOKEN = "Scalar";

    private static final Map<String, ValueType<Double>> outerTypeById = new HashMap<String, ValueType<Double>>();

    static {
        outerTypeById.put(AVERAGE_TOKEN, Types.getNumericDoubleType());
        outerTypeById.put(INTEGRAL_TOKEN, Types.getIntegralDoubleType(1));
        outerTypeById.put(SCALAR_TOKEN, null);
    }

    @SuppressWarnings("unchecked")
    public static <V> TimeIterator<V> getDownscaler(ValueType<V> type, UpscalableIterator<V> delegate, Interval interval) {
        TimeIterator<?> r;

        if(Types.getNumericDoubleType().isAssignableFrom(type)) {
            r = new AverageDownscaler(
                    (UpscalableIterator<Double>) delegate,
                    interval
            );

            return (TimeIterator<V>) r;
        } else if(Types.getIntegralDoubleType().isAssignableFrom(type)) {
            r = new IntegralDownscaler(
                    (UpscalableIterator<Double>) delegate,
                    interval
            );

            return (TimeIterator<V>) r;
        }

        return null;
    }

    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(outerTypeById.keySet().contains(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(id, arguments);

            ValueNode<Double> valueNode = parser.next().withType(Utils.isValuePredicate()).chop();

            ValueType<Double> innerType = valueNode.getType();

            ValueType<Double> outerType = outerTypeById.get(id);

            if(outerType == null)
                // If no explicit output type is provided, guess it from inner argument
                outerType = innerType;

            // Ensure outer type can be downscaled
            if(getDownscaler(outerType, null, null) == null)
                throw new ParseException("Unable to downscale type " + outerType);

            if(parser.hasNext()) {
                parser.next();

                if(Types.getIntervalType().isAssignableFrom(parser.current().type2())) {
                    ValueNode<Interval> periodicityNode = parser.current().last().chop();

                    return new ResampleIntervalImpl<Double>(
                            id,
                            valueNode,
                            periodicityNode,
                            innerType,
                            outerType
                    );
                } else {
                    // This will raise exception
                    parser.current().withType(Utils.isIntervalPredicate()).last().chop();

                    throw new AssertionError();
                }
            } else {
                return new ResampleImpl<Double>(
                        id,
                        valueNode,
                        innerType,
                        outerType
                );
            }
        } else if(MinImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(id, arguments);

            ValueNode<Object> valueNode = parser.next().withType(Utils.isValuePredicate()).chop();

            ValueNode<Interval> intervalNode;

            if(parser.hasNext()) {
                intervalNode = parser.next().withType(Utils.isIntervalPredicate()).last().chop();

                return new MinImpl(
                        valueNode,
                        intervalNode
                );
            } else
                return new MinImpl(
                        valueNode
                );

        } else if(MaxImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(id, arguments);

            ValueNode<Object> valueNode = parser.next().withType(Utils.isValuePredicate()).chop();

            ValueNode<Interval> intervalNode;

            if(parser.hasNext()) {
                intervalNode = parser.next().withType(Utils.isIntervalPredicate()).last().chop();

                return new MaxImpl(
                        valueNode,
                        intervalNode
                );
            } else
                return new MaxImpl(
                        valueNode
                );

        } else if(DiracDelta.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(DiracDelta.TOKEN, arguments);

            ValueNode<Double> valueNode = parser.next().withType(Utils.isNumericPredicate()).chop();

            ValueNode<Interval> intervalNode;

            if(parser.hasNext()) {
                intervalNode = parser.next().withType(Utils.isIntervalPredicate()).last().chop();

                return new DiracDelta(
                        valueNode,
                        intervalNode
                );
            } else
                return new DiracDelta(
                        valueNode
                );

        }

        return null;
    }
}
