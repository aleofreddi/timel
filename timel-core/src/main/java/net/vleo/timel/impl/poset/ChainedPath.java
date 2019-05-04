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

import java.util.stream.Stream;

/**
 * This class represents a chain between two paths.
 *
 * @param <T>
 * @author Andrea Leofreddi
 */
@Value
class ChainedPath<T, C> implements Path<T, C> {
    private final Path<T, C> first, second;

    @Override
    public String toString() {
        return first.toString() + " -> " + second.toString();
    }

    @Override
    public T getTargetNode() {
        return second.getTargetNode();
    }

    @Override
    public Stream<C> getPath() {
        return Stream.concat(
                first.getPath(),
                second.getPath()
        );
    }

    @Override
    public int getWeight() {
        return first.getWeight() + second.getWeight();
    }
}
