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
package net.vleo.timel.impl.expression.evaluate.function.time;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.factory.FunctionFactory;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.FunctionArgumentParser;
import net.vleo.timel.impl.expression.evaluate.constant.EvalStartImpl;
import net.vleo.timel.impl.expression.evaluate.constant.EvalStopImpl;
import net.vleo.timel.impl.expression.evaluate.constant.StartImpl;
import net.vleo.timel.impl.expression.evaluate.constant.StopImpl;
import net.vleo.timel.impl.type.Utils;
import net.vleo.timel.type.StringType;
import net.vleo.timel.type.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for time functions.
 *
 * @author Andrea Leofreddi
 */
public class TimeFactory extends FunctionFactory {
    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        if(DateImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(DateImpl.TOKEN, arguments);

            ValueNode<String> valueNode = parser.next().withType(Utils.isStringPredicate()).last().chop();

            return new DateImpl(valueNode);

        } else if(EveryImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(EveryImpl.TOKEN, arguments);

            ValueNode<Double> sizeNode = parser.next().withType(Utils.isNumericAveragePredicate()).chop();

            ValueNode<String> fieldNode = parser.next().withType(Utils.isStringPredicate()).chop();

            // Jump next
            parser.next();

            ValueNode<String> phaseNode = null;

            // If 3rd argument is a period, acquire also the phase
            if(Utils.isPeriod(parser.current().type2())) {
                phaseNode = parser.current().chop();

                parser.next();
            }

            ValueNode<String> timeZoneIdNode = parser.current().withType(Utils.isStringPredicate()).last().chop();

            if(phaseNode != null)
                return new EveryImpl(
                        sizeNode,
                        fieldNode,
                        phaseNode,
                        timeZoneIdNode
                );
            else
                return new EveryImpl(
                        sizeNode,
                        fieldNode,
                        timeZoneIdNode
                );

        } else if(IntervalImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(IntervalImpl.TOKEN, arguments);

            ValueNode<Long> startNode = parser.next().withType(Utils.isDateTimePredicate()).chop(),
                    stopNode = parser.next().withType(Utils.isDateTimePredicate()).last().chop();

            return new IntervalImpl(
                    startNode,
                    stopNode
            );

        } else if(IntervalOfImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(IntervalOfImpl.TOKEN, arguments);

            List<ValueNode<?>> valueNodes = new ArrayList<ValueNode<?>>();

            do {
                valueNodes.add((ValueNode<?>) parser.next().withType(Utils.isValuePredicate()).chop());
            } while(parser.hasNext());

            return new IntervalOfImpl(valueNodes);

        } else if(PeriodImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(PeriodImpl.TOKEN, arguments);

            parser.next();

            if(Types.getStringType().isAssignableFrom(parser.current().type2())) {
                // ISO Period

                ValueNode<String> stringNode = parser.current().last().chop();

                return new IsoPeriodImpl(stringNode);
            } else {
                ValueNode<Double> sizeNode = parser.current().withType(Utils.isNumericAveragePredicate()).chop();

                ValueNode<String> fieldNode = parser.next().withType(Utils.isStringPredicate()).last().chop();

                return new PeriodImpl(sizeNode, fieldNode);
            }

        } else if(TruncImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(TruncImpl.TOKEN, arguments);

            ValueNode<Long> valueNode = parser.next().withType(Utils.isDateTimePredicate()).chop();

            ValueNode<String> fieldNode = parser.next().withType(Utils.isStringPredicate()).chop(),
                    timeZoneNode = parser.next().withType(Utils.isStringPredicate()).last().chop();

            return new TruncImpl(
                    valueNode,
                    fieldNode,
                    timeZoneNode
            );

        } else if(ExtractImpl.TOKEN.equals(id)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(ExtractImpl.TOKEN, arguments);

            ValueNode<Long> valueNode = parser.next().withType(Utils.isDateTimePredicate()).chop();

            ValueNode<String> fieldNode = parser.next().withType(Utils.isStringPredicate()).chop();

            ValueNode<String> timeZoneNode = parser.next().withType(Utils.isStringPredicate()).last().chop();

            return new ExtractImpl(
                    valueNode,
                    fieldNode,
                    timeZoneNode
            );

        } else if(id.equals(StartImpl.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(StartImpl.TOKEN, arguments);

            parser.empty();

            return new StartImpl();

        } else if(id.equals(StopImpl.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(StopImpl.TOKEN, arguments);

            parser.empty();

            return new StopImpl();

        } else if(id.equals(EvalStartImpl.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(EvalStartImpl.TOKEN, arguments);

            parser.empty();

            return new EvalStartImpl();

        } else if(id.equals(EvalStopImpl.TOKEN)) {
            FunctionArgumentParser parser = new FunctionArgumentParser(EvalStopImpl.TOKEN, arguments);

            parser.empty();

            return new EvalStopImpl();
        }

        return null;
    }
}
