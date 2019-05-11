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
import net.vleo.timel.iterator.FilterNullTimeIterator;
import net.vleo.timel.impl.intermediate.tree.AbstractSyntaxTree;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.*;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.Sample;
import net.vleo.timel.type.Type;
import net.vleo.timel.variable.Variable;

import java.util.Collections;
import java.util.Optional;

/**
 * A node that will read values from its value node, store them in the owned variable, and pass them downstream.
 *
 * @author Andrea Leofreddi
 */
public class VariableWriter extends AbstractTargetTree {
    private final Variable<Object> variable;
    private final Upscaler<Object> upscaler;
    private final AbstractTargetTree value;

    public VariableWriter(AbstractSyntaxTree reference, Variable<Object> variable, Type type, AbstractTargetTree value) {
        super(reference, Collections.singletonList(value));
        this.variable = variable;
        this.upscaler = (Upscaler<Object>) type.getUpscaler();
        this.value = value;
    }

    @Override
    public UpscalableIterator<Object> evaluate(Interval interval, ExecutorContext context) {
        return new ChopUpscalableIterator<>(
                new UpscalerIterator<>(
                        upscaler,
//                        context.debug(this, "FilterNullTimeIterator", interval,
                        (
                                new FilterNullTimeIterator<>(
                                        new AdapterTimeIterator<Optional<Object>, Object>( /* Upscalable */
//                                                context.debug(this, "GapEvaluatorTimeIterator", interval,
                                                (
                                                        new GapEvaluatorTimeIterator<>(
                                                                variable,
                                                                value.evaluate(
                                                                        interval,
                                                                        context
                                                                ),
                                                                interval,
                                                                context
                                                        )
                                                )
                                        ) {
                                            @Override
                                            protected Sample<Object> adapt(Sample<Optional<Object>> sample) {
                                                return Sample.of(
                                                        sample.getInterval(),
                                                        sample.getValue().orElse(null)
                                                );
                                            }
                                        }
                                )
                        )
                ),
                interval
        );
    }
}
