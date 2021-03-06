package net.vleo.timel.conversion;

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

import net.vleo.timel.impl.poset.Poset;
import net.vleo.timel.impl.poset.Poset.OrderEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Leofreddi
 */
class PosetTest {
    private static Object NODE_A = build("A"),
            NODE_B = build("B"),
            NODE_C = build("C"),
            NODE_D = build("D"),
            NODE_E = build("E"),
            NODE_F = build("F"),
            NODE_G = build("G"),
            NODE_H = build("H"),
            NODE_I = build("I");

    private static Object build(String toString) {
        return new Object() {
            @Override
            public String toString() {
                return toString;
            }
        };
    }

    private static <T> Set<T> asSet(T... items) {
        return Arrays.stream(items).collect(toSet());
    }

    private static OrderEntry<Object> edge(Object from, Object to) {
        return new OrderEntry<Object>() {
            @Override
            public Object getSource() {
                return from;
            }

            @Override
            public Object getTarget() {
                return to;
            }

            @Override
            public String toString() {
                return from + "->" + to;
            }
        };
    }

    private static Map<String, Object> KNOWN_NODES = Stream.of(NODE_A, NODE_B, NODE_C, NODE_D, NODE_E, NODE_F, NODE_G, NODE_H, NODE_I)
            .collect(toMap(Object::toString, identity()));

    @SuppressWarnings("unchecked")
    private static List<Set<OrderEntry<Object>>> TYPE_CONVERSION_GRAPH = asList(
            /*
             *  B
             *  |
             *  A
             */
            asSet(
                    edge(NODE_A, NODE_B)
            ),

            /*
             *    D
             *   / \
             *  B   C
             *   \ /
             *    A
             */
            asSet(
                    edge(NODE_A, NODE_B),
                    edge(NODE_A, NODE_C),
                    edge(NODE_B, NODE_D),
                    edge(NODE_C, NODE_D)
            ),

            /*
             *    E
             *   / \
             *  D   | F
             *  |   |/
             *  B   C
             *   \ /
             *    A
             */
            asSet(
                    edge(NODE_A, NODE_B),
                    edge(NODE_A, NODE_C),
                    edge(NODE_B, NODE_D),
                    edge(NODE_C, NODE_E),
                    edge(NODE_C, NODE_F),
                    edge(NODE_D, NODE_E)
            ),

            /*
             *    E
             *   / \
             *  D   | F
             *  |   |/
             *  B   C
             *   \ /
             *    A
             */
            asSet(
                    edge(NODE_A, NODE_B),
                    edge(NODE_A, NODE_C),
                    edge(NODE_B, NODE_D),
                    edge(NODE_C, NODE_E),
                    edge(NODE_C, NODE_F),
                    edge(NODE_D, NODE_E)
            ),

            /*
             *    E
             *   / \
             *  D   | F  H   I
             *  |   |/    \ /
             *  B   C      G
             *   \ /
             *    A
             */
            asSet(
                    edge(NODE_A, NODE_B),
                    edge(NODE_A, NODE_C),
                    edge(NODE_B, NODE_D),
                    edge(NODE_C, NODE_E),
                    edge(NODE_C, NODE_F),
                    edge(NODE_D, NODE_E),
                    edge(NODE_G, NODE_H),
                    edge(NODE_G, NODE_I)
            )
    );

    private static List<Set<OrderEntry<Object>>> INVALID_GRAPHS = asList(
            /*
             * A->B->D is ambiguous to A->C->D
             *
             *    D
             *   / \
             *  B   C
             *   \ /
             *    A
             */
            asSet(
                    edge(NODE_A, NODE_B),
                    edge(NODE_A, NODE_C),
                    edge(NODE_B, NODE_D),
                    edge(NODE_C, NODE_D)
            ),

            /*
             * A->B->D->G is ambiguous to A->C->E->G
             *
             *    G
             *   / \
             *  D   E F
             *  |   |/
             *  B   C
             *   \ /
             *    A
             */
            asSet(
                    edge(NODE_A, NODE_B),
                    edge(NODE_A, NODE_C),
                    edge(NODE_B, NODE_D),
                    edge(NODE_C, NODE_E),
                    edge(NODE_C, NODE_F),
                    edge(NODE_D, NODE_G),
                    edge(NODE_E, NODE_G)
            )
    );

    @ParameterizedTest(name = "graph=#{0} expectedWeight={1}")
    @CsvSource({
            "0,2",
            "1,3"
    })
    void ambiguousGraphShouldThrowIllegalArgumentException(int graph, int expectedWeight) {
        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> new Poset<>(INVALID_GRAPHS.get(graph)));

        assertThat(actual.getMessage(), containsString("Ambiguous path"));
        assertThat(actual.getMessage(), containsString("weight " + expectedWeight));
    }

    @ParameterizedTest(name = "graph=#{0} types={1}, expectedLub={2}")
    @CsvSource({
            "0,A,A",
            "2,A,A",
            "2,A;B,B",
            "2,C;B,E",
            "2,F;B,null",
            "4,A;H,null"
    })
    void leastUpperBoundShouldWork(int graph, String typeNames, String expectedLubName) {
        Poset<Object, OrderEntry<Object>> conversionClosure = new Poset<>(TYPE_CONVERSION_GRAPH.get(graph));
        Set<Object> types = Arrays.stream(typeNames.split(";"))
                .map(typeName -> KNOWN_NODES.get(typeName))
                .collect(toSet());
        Optional<Object> expectedLub = Optional.ofNullable(KNOWN_NODES.get(expectedLubName));

        Optional<Object> actualLub = conversionClosure.leastUpperBound(types);

        assertThat(actualLub, is(expectedLub));
    }

    @ParameterizedTest(name = "graph=#{0} from={1} to={2} via={3} weight={4}")
    @CsvSource({
            "0,A,B,A->B,1",
            "2,B,E,B->D+D->E,2",
            "2,A,E,A->C+C->E,2",
            "3,A,E,A->C+C->E,4",
            "4,G,H,G->H,1",
            "4,G,I,G->I,3"
    })
    void conversionPathShouldWork(int graph, String fromName, String toName, String expectedPath, int expectedWeight) {
        Poset<Object, Poset.OrderEntry<Object>> conversionClosure = new Poset<>(TYPE_CONVERSION_GRAPH.get(graph));
        Object from = KNOWN_NODES.get(fromName), to = KNOWN_NODES.get(toName);

        List<OrderEntry<Object>> conversions = conversionClosure.getPath(from, to);

        String actualPath = conversions.stream()
                .map(Object::toString)
                .collect(joining("+"));

        assertThat(actualPath, is(expectedPath));
    }

    @ParameterizedTest(name = "graph=#{0} from={1} to={2}")
    @CsvSource({
            "4,F,E"
    })
    void shouldReturnNullWhenNoPath(int graph, String fromName, String toName) {
        Poset<Object, OrderEntry<Object>> conversionClosure = new Poset<>(TYPE_CONVERSION_GRAPH.get(graph));
        Object from = KNOWN_NODES.get(fromName), to = KNOWN_NODES.get(toName);

        List<Poset.OrderEntry<Object>> conversions = conversionClosure.getPath(from, to);

        assertThat(conversions, nullValue());
    }

    @Test
    void conversionPathShouldReturnEmptyListForUnknownTypesIdentity() {
        Poset<Object, OrderEntry<Object>> conversionClosure = new Poset<>(TYPE_CONVERSION_GRAPH.get(0));
        Object newType = new Object() {
        };

        assertThat(conversionClosure.getPath(newType, newType), is(emptyList()));
    }
}
