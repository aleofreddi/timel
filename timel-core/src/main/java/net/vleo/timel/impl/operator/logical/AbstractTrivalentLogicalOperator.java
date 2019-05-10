package net.vleo.timel.impl.operator.logical;

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
import net.vleo.timel.iterator.SplitAdapterTimeIterator;
import net.vleo.timel.iterator.UnionIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.UpscalerIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.function.Function;

import java.util.Collections;
import java.util.List;

/**
 * Base class for trivalent logical binary operators.
 * <p>
 * In TimEL logical operators can handle (and produce) three values: true, false and null.
 *
 * @author Andrea Leofreddi
 */
public abstract class AbstractTrivalentLogicalOperator implements Function<Boolean> {
    protected abstract Boolean apply(Boolean t, Boolean u);

    @Override
    public final UpscalableIterator<Boolean> evaluate(Interval interval, ExecutorContext context, Upscaler<Boolean> upscaler, Downscaler<Boolean> downscaler, Evaluable<?>... arguments) {
        return new UpscalerIterator<>(
                upscaler,
                new SplitAdapterTimeIterator<Object[], Boolean>(
                        new UnionIterator(
                                arguments[0].evaluate(interval, context),
                                arguments[1].evaluate(interval, context)
                        )
                ) {
                    @Override
                    protected List<Sample<Boolean>> adapt(Sample<Object[]> sample) {
                        Object[] values = sample.getValue();

                        Boolean t = (Boolean) values[0], u = (Boolean) values[1];
                        Boolean r = apply(t, u);

                        if(r == null)
                            // Suppress unknown
                            return Collections.emptyList();

                        return Collections.singletonList(sample.copyWithValue(r));
                    }
                }
        );
    }
}
