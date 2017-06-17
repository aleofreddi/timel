package net.vleo.timel.conversion;

import net.vleo.timel.IntegerType;
import net.vleo.timel.Type;
import net.vleo.timel.ZeroType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Andrea Leofreddi on 03/07/2017.
 */
public class TypeConverter {

    private static Map<Type, Map<Type, Integer>> conversions = new HashMap<>();

    static {
        conversions.put(new ZeroType(), Stream.of(new IntegerType()).collect(Collectors.toMap(Function.identity(), type -> 1)));

        conversions.forEach((key, value) -> value.put(key, 0));
    }

    public Map<Type, Integer> implicitConversionsFrom(Type source) {
        return conversions.getOrDefault(source, Collections.singletonMap(source, 0));
    }

    public Integer implicitConversion(Type source, Type to) {
        return conversions.get(source).get(to);
    }
}
