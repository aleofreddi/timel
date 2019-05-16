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

import lombok.val;
import net.vleo.timel.ConfigurationException;
import net.vleo.timel.ParseException;
import net.vleo.timel.annotation.ConversionPrototype;
import net.vleo.timel.conversion.Conversion;
import net.vleo.timel.impl.poset.Poset;
import net.vleo.timel.tuple.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A type system to support implicit and explicit casting.
 *
 * @author Andrea Leofreddi
 */
public class TypeSystem {
    private final Poset<Type<?>, ConversionOrderEntry> implicitPoset;
    private final Poset<Type<?>, ConversionOrderEntry> explicitPoset;
    private Set<Type<?>> types;
    private Map<String, Type<?>> idToType;

    /**
     * Retrieve the list of conversions to be applied to convert a concrete type source into to (a possibly non-concrete) target.
     * <p>
     * If the conversion is successful, the returned {@link ConversionResult} will contain the conversion path along with the resolved, concrete result type.
     *
     * @param implicit Use only implicit conversions
     * @param source   Source type
     * @param target   Target type
     * @return The conversion path, or null if any
     * @throws ConfigurationException If an inconsistent conversion configuration was found
     */
    public ConversionResult getConcretePath(boolean implicit, Type<?> source, Type<?> target) {
        if(!source.isConcrete())
            throw new IllegalArgumentException("Type " + source + " is not concrete");

        List<ConversionOrderEntry> conversionEdges = (implicit ? implicitPoset : explicitPoset).getPath(source.template(), target.template());

        if(conversionEdges == null)
            return null;

        List<Conversion<Object, Object>> path = new ArrayList<>(conversionEdges.size());

        Type<?> type = source;
        for(ConversionOrderEntry conversionEdge : conversionEdges) {
            Conversion<Object, Object> nextConversion = (Conversion<Object, Object>) conversionEdge.getConversion();
            val targetType = conversionEdge.getTarget();

            val nextType = type.isUnboundTemplate() || type.isSpecializedTemplate() || targetType.isUnboundTemplate() ?
                    nextConversion.resolveReturnType(type, targetType) : Optional.of(targetType);

            if(!nextType.isPresent())
                return null;

            type = nextType.get();
            path.add(nextConversion);
        }

        return new ConversionResult(path, type);
    }

    /**
     * Get the least upper bound given a set of types, either for implicit or explicit conversions.
     *
     * @param implicit If true, use the implicit type conversions
     * @param types    Input elements
     * @return The least upper bound if any
     */
    public Optional<Type<?>> leastUpperBound(boolean implicit, Set<Type<?>> types) {
        return (implicit ? implicitPoset : explicitPoset)
                .leastUpperBound(types.stream().map(Type::template).collect(Collectors.toSet()));
    }

    /**
     * Parse a type given its id, and a list of specialization
     *
     * @param id        Type id
     * @param arguments Template arguments (if any)
     * @return Parsed type
     * @throws ParseException When the type declaration is not consistent
     */
    public Type parse(String id, List<Object> arguments) throws ParseException {
        Type result = idToType.get(id);

        if(result == null)
            throw new ParseException("Unknown type " + id);

        if(result.isUnboundTemplate() && arguments.isEmpty())
            throw new ParseException("Cannot instance template type " + id);

        if(result.isConcrete() && !arguments.isEmpty())
            throw new ParseException("Type " + id + " is not a template");

        if(arguments.isEmpty())
            return result;

        return result.specialize(arguments.toArray());
    }

    /**
     * Initializes the type system with the given conversions and types.
     *
     * @param conversions Supported conversions
     * @param types       Additional types to consider (useful when no conversion exists for such a type)
     */
    public TypeSystem(Set<Conversion<?, ?>> conversions, Set<Type<?>> types) {
        val groupedConversions = parse(conversions);

        groupedConversions.putIfAbsent(true, Collections.emptySet());
        groupedConversions.putIfAbsent(false, Collections.emptySet());

        implicitPoset = new Poset<>(groupedConversions.get(true));
        explicitPoset = new Poset<>(groupedConversions.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));

        this.types = Stream.concat(
                Stream.concat(
                        groupedConversions.getOrDefault(true, Collections.emptySet()).stream(),
                        groupedConversions.get(false).stream()
                )
                        .flatMap(conversion -> Stream.of(conversion.getSource(), conversion.getTarget())),
                types.stream()
        )
                .collect(Collectors.toSet());

        idToType = this.types.stream()
                .collect(Collectors.toMap(
                        Type::getName,
                        Function.identity()
                ));
    }

    private Map<Boolean, Set<ConversionOrderEntry>> parse(Set<Conversion<?, ?>> conversions) {
        return conversions.stream()
                .map(conversion -> {
                    ConversionPrototype prototype = conversion.getClass().getDeclaredAnnotation(ConversionPrototype.class);

                    if(prototype == null)
                        throw new ConfigurationException("Cast class " + conversion.getClass() + " should be annotated via " + ConversionPrototype.class.getName());

                    return new Pair<>(conversion, prototype);
                })
                .collect(Collectors.groupingBy(
                        entry -> entry.getSecond().implicit(),
                        Collectors.mapping(
                                conversion -> new ConversionOrderEntry(
                                        Types.instance((Class<? extends Type<Object>>) conversion.getSecond().source()),
                                        Types.instance((Class<? extends Type<Object>>) conversion.getSecond().target()),
                                        conversion.getFirst()
                                ),
                                Collectors.toSet()
                        )
                ));
    }
}
