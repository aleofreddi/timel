package net.vleo.timel.type;

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

import net.vleo.timel.ParseException;
import net.vleo.timel.cast.AbstractTypeConversion;
import net.vleo.timel.impl.poset.WeightedPoset;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A type system to support implicit casting.
 *
 * @author Andrea Leofreddi
 */
public class TypeSystem extends WeightedPoset<Type, AbstractTypeConversion> {
    private Set<Type> types;

    private Map<String, Type> idToType;

    WeightedPoset<Type, AbstractTypeConversion> implicit;
    WeightedPoset<Type, AbstractTypeConversion> explicit;

    public List<AbstractTypeConversion> getExplicitPath(Type source, Type target) {
        return explicit.getPath(source, target);
    }

    public List<AbstractTypeConversion> explicitLeastUpperBound(Type source, Type target) {
        return explicit.getPath(source, target);
    }

    public Type parse(String type) throws ParseException {
        int separator = type.indexOf('<');

        if(separator != -1) {
            if(!type.endsWith(">"))
                throw new ParseException("Invalid type " + type);

            throw new AssertionError(); // FIXME - IMPLEMENT ME! A TYPE SHOULD SOMEHOW KNOW ITS PARAMETER LIST TYPES
        }

        Type result = idToType.get(type);

        if(result == null)
            throw new ParseException("Unknown type " + type);

        return result;
    }

    /**
     * Build a {@link WeightedPoset} from the given edges.
     *
     * @param edges Posted edges
     */
    public TypeSystem(Set<AbstractTypeConversion> edges, Set<AbstractTypeConversion> explicitEdges) {
        super(edges);

        explicit = new WeightedPoset<>(explicitEdges);

        types = Stream.concat(
                edges.stream().map(AbstractTypeConversion::getSource),
                Stream.concat(
                        edges.stream().map(AbstractTypeConversion::getTarget),
                        Stream.concat(
                                explicitEdges.stream().map(AbstractTypeConversion::getSource),
                                explicitEdges.stream().map(AbstractTypeConversion::getTarget)
                        )
                )
        ).collect(Collectors.toSet());

        idToType = types.stream()
                .collect(Collectors.toMap(
                        Type::getName,
                        Function.identity()
                ));
    }
}
