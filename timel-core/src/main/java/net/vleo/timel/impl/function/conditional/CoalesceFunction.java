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

import net.vleo.timel.annotation.Prototype;
import net.vleo.timel.annotation.Parameter;
import net.vleo.timel.annotation.Returns;
import net.vleo.timel.function.Function;
import net.vleo.timel.impl.target.Evaluable;
import net.vleo.timel.impl.downscaler.Downscaler;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.UpscalerIterator;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.iterator.CoalesceIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;

/**
 * Implementation of the coalesce function.
 *
 * @author Andrea Leofreddi
 */
@Prototype(
        name = "coalesce",
        returns = @Returns(variable = "T"),
        parameters = {
                @Parameter(variable = "T"),
                @Parameter(variable = "T", varArgs = true)
        }
)
public class CoalesceFunction implements Function<Object> {
    @Override
    public UpscalableIterator<Object> evaluate(Interval interval, ExecutorContext context, Upscaler<Object> upscaler, Downscaler<Object> downscaler, Evaluable<?>... arguments) {
        return new UpscalerIterator<>(
                upscaler,
                new CoalesceIterator<>(
                        Function.evaluateAll(interval, context, arguments)
                )
        );
    }
}
