package net.vleo.timel.iterator;

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

import net.vleo.timel.time.Sample;

import java.util.function.Function;

/**
 * A TimeIterator that will pull samples out of its delegate, and apply a function to adapt each sample.
 * <p>
 * The adapter function is called once per sample and its result is cached to serve multiple next or peekNext calls.
 *
 * @param <S> Source value Java type
 * @param <D> Target value Java type
 * @author Andrea Leofreddi
 */
public final class SampleAdapterTimeIterator<S, D> extends AdapterTimeIterator<S, D> {
    private final Function<Sample<S>, Sample<D>> adapter;

    /**
     * Build a new adapter around the given delegate, using the given mapping adapter function.
     *
     * @param delegate Source iterator
     * @param adapter  Sample adapter
     */
    public SampleAdapterTimeIterator(TimeIterator<S> delegate, Function<Sample<S>, Sample<D>> adapter) {
        super(delegate);
        this.adapter = adapter;
    }

    @Override
    protected Sample<D> adapt(Sample<S> sample) {
        return adapter.apply(sample);
    }
}
