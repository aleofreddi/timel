package net.vleo.timel.impl.function.eval;

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
import net.vleo.timel.time.Sample;
import net.vleo.timel.impl.target.Evaluable;
import net.vleo.timel.impl.downscaler.Downscaler;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.*;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.IntervalType;

/**
 * Resample a value accordingly to the given interval.
 * <p>
 * An inner downscaler is used to interpolate the value while aggregating it for the periodicity,
 * an outer one to further interpolate the outcome to the evaluation interval.
 *
 * @author Andrea Leofreddi
 */
@Prototype(
        name = "scale",
        returns = @Returns(variable = "T"),
        parameters = {
                @Parameter(variable = "T"),
                @Parameter(type = IntervalType.class),
        }
)
public class ScaleIntervalFunction<T> implements Function<T> {
    private <K> K debug(String message, Interval interval, K k) {
        return k;
    }

    @Override
    public UpscalableIterator<T> evaluate(Interval interval, ExecutorContext context, Upscaler<T> upscaler, Downscaler<T> downscaler, Evaluable<?>[] arguments) {
        assert arguments.length == 2;
        return new UpscalerIterator<>(
                upscaler,

                // (1) For each period in intervalNode
                debug("forEachPeriodicity", interval, new NestedLoopTimeIterator<Interval, T>(
                        ((Evaluable<Interval>) arguments[1]).evaluate(
                                interval,
                                context
                        )
                ) {
                    @Override
                    protected TimeIterator<T> nestedIterator(Sample<Interval> value) {
                        Interval periodInterval = value.getValue();

                        // (2) Scalarize the inner value
                        return new DownscalerIterator<>(
                                downscaler,
                                ((Evaluable<T>) arguments[0]).evaluate(periodInterval, context),
                                periodInterval
                        );
                    }
                })
        );
    }
}
