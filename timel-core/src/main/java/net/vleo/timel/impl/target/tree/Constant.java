package net.vleo.timel.impl.target.tree;

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
import net.vleo.timel.impl.intermediate.tree.AbstractSyntaxTree;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.SingletonTimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.UpscalerIterator;
import net.vleo.timel.time.Interval;

import java.util.Collections;

/**
 * Constant node.
 *
 * @author Andrea Leofreddi
 */
public class Constant extends AbstractTargetTree {
    private final Upscaler<Object> upscaler;
    private final Object value;

    public Constant(AbstractSyntaxTree reference, Upscaler<Object> upscaler, Object value) {
        super(reference, Collections.emptyList());
        this.upscaler = upscaler;
        this.value = value;
    }

    @Override
    public UpscalableIterator<Object> evaluate(Interval interval, ExecutorContext context) {
        return new UpscalerIterator<>(
                upscaler,
                new SingletonTimeIterator<>(Sample.of(interval, value))
        );
    }
}
