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
import net.vleo.timel.impl.intermediate.tree.AbstractSyntaxTree;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.ChopUpscalableIterator;
import net.vleo.timel.iterator.FilterNullTimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.iterator.UpscalerIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.Type;
import net.vleo.timel.variable.Variable;

import java.util.Collections;

/**
 * A node that will read from a variable.
 *
 * @author Andrea Leofreddi
 */
public class VariableReader extends AbstractTargetTree {
    private final Variable<Object> variable;
    private final Upscaler<Object> upscaler;

    public VariableReader(AbstractSyntaxTree reference, Variable<Object> variable, Type type) {
        super(reference, Collections.emptyList());
        this.variable = variable;
        this.upscaler = (Upscaler<Object>) type.getUpscaler();
    }

    @Override
    public UpscalableIterator<Object> evaluate(Interval interval, ExecutorContext context) {
        return new ChopUpscalableIterator<>(
                new UpscalerIterator<>(
                        upscaler,
                        context.debug(this, "FilterNullTimeIterator", interval,
                                new FilterNullTimeIterator<>(
                                        variable.readForward(
                                                interval,
                                                context
                                        )
                                )
                        )
                ),
                interval
        );
    }
}
