package net.vleo.timel.impl.operator.arithmetic;

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

import net.vleo.timel.annotation.Prototype;
import net.vleo.timel.annotation.Parameter;
import net.vleo.timel.annotation.Returns;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.function.Function;
import net.vleo.timel.impl.target.Evaluable;
import net.vleo.timel.impl.downscaler.Downscaler;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.IntersectIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.UpscalerIterator;
import net.vleo.timel.iterator.ValueAdapterTimeIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.FloatType;

/**
 * Float div implementation.
 *
 * @author Andrea Leofreddi
 */
@Prototype(
        name = "/",
        returns = @Returns(type = FloatType.class),
        parameters = {
                @Parameter(type = FloatType.class),
                @Parameter(type = FloatType.class)
        }
)
public class DivFloatOperator implements Function<Float> {
    @Override
    public UpscalableIterator<Float> evaluate(Interval interval, ExecutorContext context, Upscaler<Float> upscaler, Downscaler<Float> downscaler, Evaluable<?>... arguments) {
        assert arguments.length == 2;
        return new UpscalerIterator<>(
                upscaler,
                new ValueAdapterTimeIterator<>(
                        new IntersectIterator(
                                Function.evaluateAll(interval, context, arguments)
                        ),
                        values -> (Float) values[0] / (Float) values[1]
                )
        );
    }
}
