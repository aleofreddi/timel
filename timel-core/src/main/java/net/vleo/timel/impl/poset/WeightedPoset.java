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

import java.util.*;
import java.util.stream.IntStream;

import static java.util.Collections.*;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.concat;

/**
 * A class to represent a weighted, partially ordered set.
 * <p>
 * This class supports leastUpperBound and getPath operations so it is suitable as a base for the intermediate system.
 *
 * @param <N> Node intermediate
 * @param <E> Edge intermediate
 * @author Andrea Leofreddi
 */
public class WeightedPoset<N, E extends WeightedPoset.Edge<N>> {
    private final Map<N, Integer> nodeEnumeration;
    private final Map<Integer, Path<N, E>> closure;

    /**
     * A weighted poset edge.
     *
     * @param <T>
     */
    public interface Edge<T> {
        T getSource();

        T getTarget();

        int getWeight();
    }

    /**
     * Build a {@link WeightedPoset} from the given edges.
     *
     * @param edges Posted edges
     */
    public WeightedPoset(Set<E> edges) {
        List<N> types = concat(
                edges.stream()
                        .map(Edge::getSource),
                edges.stream()
                        .map(Edge::getTarget)
        )
                .distinct()
                .collect(toList());

        int items = types.size();
        Map<Integer, Path<N, E>> current = new HashMap<>(), next = new HashMap<>();
        final Map<Integer, Path<N, E>> init = current;

        // Enumerate types
        nodeEnumeration = IntStream.range(0, items)
                .boxed()
                .collect(toMap(types::get, identity()));

        // Map identity
        types.forEach(type -> {
            int index = nodeEnumeration.get(type);
            init.put(index * items + index, new IdentityPath<>(type));
        });

        // Map edges
        edges.forEach(edge -> {
            int from = nodeEnumeration.get(edge.getSource()),
                    to = nodeEnumeration.get(edge.getTarget());

            init.put(from * items + to, new DirectPath<>(edge));
        });

        // Compute transitive closure using Warshall's algorithm
        for(int k = 0; k < items; k++) {
            for(int i = 0; i < items; i++)
                for(int j = 0; j < items; j++) {
                    Path<N, E> existing = null, chained = null;

                    if(current.get(i * items + j) != null)
                        existing = current.get(i * items + j);

                    if(i != k && k != j && current.get(i * items + k) != null && current.get(k * items + j) != null)
                        chained = new ChainedPath<>(current.get(i * items + k), current.get(k * items + j));

                    if(existing != null && chained != null && existing.getWeight() == chained.getWeight())
                        throw new IllegalArgumentException("Ambiguous paths with same weight " + existing.getWeight()
                                + ": <" + existing + "> and <" + chained + ">");

                    Path<N, E> add = existing != null && (chained == null || existing.getWeight() < chained.getWeight()) ? existing : chained;

                    if(add != null)
                        next.put(i * items + j, add);
                }

            Map<Integer, Path<N, E>> t = current;
            current = next;
            next = t;
        }

        closure = next;
    }

    /**
     * Retrieve the least-upper-bound of a given set of nodes.
     *
     * @param nodes T to check
     * @return An optional least-upper-bound intermediate
     */
    public Optional<N> leastUpperBound(Set<N> nodes) {
        int items = nodeEnumeration.size();

        List<N> upperBounds = null;

        if(nodes.isEmpty())
            return Optional.empty();
        if(nodes.size() == 1)
            return Optional.of(nodes.iterator().next());

        for(N type : nodes) {
            Integer position = nodeEnumeration.get(type);

            if(position == null)
                return Optional.empty();

            int offset = items * position;

            if(upperBounds == null) {
                upperBounds = new ArrayList<>(items);

                for(int i = 0; i < items; i++)
                    upperBounds.add(closure.get(offset + i) == null ? null : closure.get(offset + i).getTargetNode());
            } else
                for(int i = 0; i < items; i++)
                    if(closure.get(offset + i) == null)
                        upperBounds.set(i, null);
        }

        List<Integer> upperBoundsIndices = upperBounds.stream()
                .filter(Objects::nonNull)
                .map(nodeEnumeration::get)
                .collect(toList());

        for(Integer i : upperBoundsIndices) {
            int offset = i * items;

            long k = upperBoundsIndices.stream()
                    .filter(j -> closure.get(offset + j) != null)
                    .count();

            if(k == upperBoundsIndices.size())
                return Optional.of(upperBounds.get(i));
        }

        return Optional.empty();
    }

    /**
     * Retrieve the path between two nodes. It is assumed that such a conversion is possible (to ensure that, use leastUpperBound).
     *
     * @param source Source intermediate
     * @param target Target intermediate
     * @return Path path
     */
    public List<E> getPath(N source, N target) {
        int items = nodeEnumeration.size();

        if(source != null && source.equals(target))
            return emptyList();

        Integer from = nodeEnumeration.get(source), to = nodeEnumeration.get(target);

        if(from == null || to == null)
            return null;

        Path<N, E> path = closure.get(items * from + to);

        if(path == null)
            return null;

        return path.getPath().collect(toList());
    }
}
