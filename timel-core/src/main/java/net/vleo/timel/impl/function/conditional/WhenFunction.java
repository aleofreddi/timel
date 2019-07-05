package net.vleo.timel.impl.function.conditional;

/*-
 * #%L
 * TimEL core
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

import net.vleo.timel.annotation.FunctionPrototype;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.time.Sample;
import net.vleo.timel.impl.target.Evaluable;
import net.vleo.timel.impl.downscaler.Downscaler;
import net.vleo.timel.impl.upscaler.SameUpscaler;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.*;
import net.vleo.timel.time.Interval;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.vleo.timel.annotation.Parameter;

import net.vleo.timel.annotation.Returns;
import net.vleo.timel.type.BooleanType;
import net.vleo.timel.function.Function;

/**
 * A function similar to C's switch block. When accepts a list or condition-value pairs and will return the value of the first pair where the condition
 * evaluates to true.
 *
 * @param <T>
 * @author Andrea Leofreddi
 */
@FunctionPrototype(
        name = "when",
        returns = @Returns(variable = "T"),
        parameters = {
                @Parameter(type = BooleanType.class, varArgs = true),
                @Parameter(variable = "T", varArgs = true),
        }
)
public class WhenFunction<T> implements Function<T> {
    @Override
    public UpscalableIterator<T> evaluate(Interval interval, ExecutorContext context, Upscaler<T> upscaler, Downscaler<T> downscaler, Evaluable<?>[] arguments) {
        //
        // Assemble the coalesce-set as all the conditionNodes transformed as follows:
        //
        // true -> #condition index
        // false or undef -> undef
        //
        // In such a way we can use coalesce's output as pointer to the right value
        //
        CoalesceIterator<Integer> coalesceIndex = new CoalesceIterator<>(
                IntStream.range(0, arguments.length / 2)
                        .mapToObj(i -> new UpscalerIterator<>(
                                SameUpscaler.get(),
                                new SplitAdapterTimeIterator<Boolean, Integer>(
                                        ((Evaluable<Boolean>) arguments[2 * i]).evaluate(interval, context)
                                ) {
                                    @Override
                                    protected List<Sample<Integer>> adapt(Sample<Boolean> sample) {
                                        Boolean v = sample.getValue();

                                        if(v)
                                            return Arrays.asList(sample.copyWithValue(2 * i + 1));

                                        // Undefine false values
                                        return Collections.emptyList();
                                    }
                                }
                        ))
                        .collect(Collectors.toList())
        );

        return new UpscalerIterator<>(
                upscaler,
                // (1) For each condition coalesce
                new NestedLoopTimeIterator<Integer, T>(
                        coalesceIndex
                ) {
                    // (2) Get its position, and evaluate the relative value node for the given interval
                    @Override
                    protected TimeIterator<T> nestedIterator(Sample<Integer> value) {
                        Interval interval = value.getInterval();
                        int position = value.getValue();

                        return ((Evaluable<T>) arguments[position]).evaluate(interval, context);
                    }
                }
        );
    }
}
