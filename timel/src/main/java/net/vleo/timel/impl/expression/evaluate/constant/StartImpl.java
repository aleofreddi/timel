/*
 * Copyright 2014-2016 Andrea Leofreddi
 *
 * This file is part of TimEL.
 *
 * TimEL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TimEL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TimEL.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.vleo.timel.impl.expression.evaluate.constant;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.executor.Sample;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.impl.expression.compile.AbstractValueFunction;
import net.vleo.timel.iterator.TimeIterator;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.IntervalMaps;
import net.vleo.timel.type.TimeType;
import net.vleo.timel.type.Types;

import java.util.NoSuchElementException;
import java.util.SortedMap;

/**
 * Start function implementation. This function returns the start date of the evaluation period.
 *
 * @author Andrea Leofreddi
 */
public class StartImpl extends AbstractValueFunction<Long> implements ValueNode<Long> {
    public static final String TOKEN = "Start";

    public StartImpl() {
        super(TOKEN, Types.getTimeType());
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public UpscalableIterator<Long> evaluate(final Interval interval, ExecutorContext context) {
        SortedMap<Interval, Long> map = IntervalMaps.of(
                Sample.of(
                        interval,
                        interval.getStart()
                )
        );

        final TimeIterator<Long> itor = IntervalMaps.iterator(map);

        return new UpscalableIterator<Long>() {
            @Override
            public Sample<Long> peekUpscaleNext(Interval interval) {
                return Sample.of(
                        interval,
                        interval.getStart()
                );
            }

            @Override
            public Sample<Long> next() throws NoSuchElementException {
                return itor.next();
            }

            @Override
            public Sample<Long> peekNext() throws NoSuchElementException {
                return itor.peekNext();
            }

            @Override
            public boolean hasNext() {
                return itor.hasNext();
            }

            @Override
            public String toString() {
                return "StartIterator";
            }
        };
    }
}
