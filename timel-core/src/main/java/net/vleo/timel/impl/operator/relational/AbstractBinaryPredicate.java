package net.vleo.timel.impl.operator.relational;

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

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.time.Sample;
import net.vleo.timel.impl.target.Evaluable;
import net.vleo.timel.impl.downscaler.Downscaler;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.AdapterTimeIterator;
import net.vleo.timel.iterator.IntersectIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.UpscalerIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.function.Function;

/**
 * Common superclass for binary predicates.
 *
 * @author Andrea Leofreddi
 */
public abstract class AbstractBinaryPredicate<T> implements Function<Boolean> {
    protected abstract boolean apply(T t, T u);

    @Override
    public final UpscalableIterator<Boolean> evaluate(Interval interval, ExecutorContext context, Upscaler<Boolean> upscaler, Downscaler<Boolean> downscaler, Evaluable<?>... arguments) {
        assert arguments.length == 2;
        return new UpscalerIterator<>(
                upscaler,
                new AdapterTimeIterator<Object[], Boolean>(
                        new IntersectIterator(
                                arguments[0].evaluate(interval, context),
                                arguments[1].evaluate(interval, context)
                        )
                ) {
                    @Override
                    protected Sample<Boolean> adapt(Sample<Object[]> sample) {
                        Object[] values = sample.getValue();
                        //noinspection unchecked
                        return sample.copyWithValue(apply((T) values[0], (T) values[1]));
                    }
                }
        );
    }
}

