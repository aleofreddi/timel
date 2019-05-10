package net.vleo.timel.impl.poset;

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

import lombok.Value;
import net.vleo.timel.impl.poset.Poset.OrderEntry;

import java.util.stream.Stream;

/**
 * Represent a weighted edge between to nodes. There is a {@link DirectPath} for each edge provided when initializing the {@link Poset}.
 *
 * @param <N> Element type
 * @param <E> Partial order entry
 * @author Andrea Leofreddi
 */
@Value
class DirectPath<N, E extends OrderEntry<N>> implements Path<N, E> {
    private final E edge;

    @Override
    public String toString() {
        return "(" + edge.getSource().toString() + " -> " + edge.getTarget().toString() + ")";
    }

    @Override
    public N getTargetNode() {
        return edge.getTarget();
    }

    @Override
    public Stream<E> getPath() {
        return Stream.of(edge);
    }

    @Override
    public int getWeight() {
        return 1; //edge.getWeight();
    }
}
