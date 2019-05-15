package net.vleo.timel.impl.function.integral;

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
import net.vleo.timel.annotation.Parameter;
import net.vleo.timel.annotation.Returns;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.function.Function;
import net.vleo.timel.impl.target.Evaluable;
import net.vleo.timel.impl.downscaler.Downscaler;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.UpscalerIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.*;

import java.util.Map;
import java.util.Optional;

/**
 * Declare an uniform integral for a given value, covering the whole evaluation interval.
 *
 * @author Andrea Leofreddi
 */
@FunctionPrototype(
        name = "uniform",
        returns = @Returns(type = IntegralFloatType.class),
        parameters = {
                @Parameter(type = FloatType.class)
        }
)
public class UniformFloatFunction<T> implements Function<T> {
    @Override
    public Optional<Type> resolveReturnType(Type proposed, Map<String, Type> variables, Type... argumentTypes) {
        // Aways return a 1st degree integral
        return Optional.of(new IntegralFloatType(1));
    }

    @Override
    public UpscalableIterator<T> evaluate(Interval interval, ExecutorContext context, Upscaler<T> upscaler, Downscaler<T> downscaler, Evaluable<?>[] arguments) {
        assert arguments.length == 1;
        return new UpscalerIterator<>(
                upscaler,
                ((Evaluable<T>) arguments[0]).evaluate(interval, context)
        );
    }
}
