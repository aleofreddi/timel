package net.vleo.timel.impl.function.math;

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

import net.vleo.timel.annotation.Parameter;
import net.vleo.timel.annotation.Prototype;
import net.vleo.timel.annotation.Prototypes;
import net.vleo.timel.annotation.Returns;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.function.Function;
import net.vleo.timel.impl.downscaler.Downscaler;
import net.vleo.timel.impl.target.Evaluable;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.SampleAdapterTimeIterator;
import net.vleo.timel.iterator.SingletonTimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.UpscalerIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.Sample;
import net.vleo.timel.type.DoubleType;
import net.vleo.timel.type.IntegerType;

import java.util.Random;

/**
 * Produce a (deterministic) random value in the range [0.0, 1.0) for its given start interval and an optional integer seed.
 *
 * @author Andrea Leofreddi
 */
@Prototypes({
        @Prototype(
                name = "rand",
                returns = @Returns(type = DoubleType.class),
                parameters = {}
        ),
        @Prototype(
                name = "rand",
                returns = @Returns(type = DoubleType.class),
                parameters = {
                        @Parameter(type = IntegerType.class)
                }
        )
})
public class RandFunction implements Function<Double> {
    @Override
    public UpscalableIterator<Double> evaluate(Interval interval, ExecutorContext context, Upscaler<Double> upscaler, Downscaler<Double> downscaler, Evaluable<?>[] arguments) {
        assert arguments.length == 0 || arguments.length == 1;
        return new UpscalerIterator<>(
                upscaler,
                new SampleAdapterTimeIterator<>(
                        arguments.length != 0 ?
                                ((Evaluable<Integer>) arguments[0]).evaluate(interval, context)
                                : new SingletonTimeIterator<>(Sample.of(interval, 0)),
                        value -> Sample.of(
                                value.getInterval(),
                                new Random(interval.getStart() + value.getValue()).nextDouble()
                        )
                )
        );
    }
}
