package net.vleo.timel.compiler;

import lombok.Data;
import net.vleo.timel.DoubleType;
import net.vleo.timel.FloatType;
import net.vleo.timel.IntegerType;
import net.vleo.timel.Type;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Arrays.sort;

/**
 * Created by Andrea Leofreddi on 11/07/2017.
 */
public class FunctionRegistry {
    @Data
    private static class Signature {
        private final List<Type> arguments;
        private final Type returnType;
    }

    private static Map<String, Collection<Signature>> functionLookup = new HashMap<>();

    @Data
    private static class Parameter implements Weighted {
        public final int position;

        public final TypedAbstractSyntaxTree<?> tree;

        @Override
        public int getWeight() {
            return tree.getWeight();
        }
    }

    static {
        functionLookup.computeIfAbsent("+", key -> new ArrayList<>())
                .add(new Signature(asList(new FloatType(), new FloatType()), new FloatType()));
        functionLookup.computeIfAbsent("+", key -> new ArrayList<>())
                .add(new Signature(asList(new DoubleType(), new DoubleType()), new DoubleType()));
        functionLookup.computeIfAbsent("+", key -> new ArrayList<>())
                .add(new Signature(asList(new IntegerType(), new IntegerType()), new IntegerType()));
    }

    public AlternativeProducer<TypedAbstractSyntaxTree<?>> match(String function, List<AlternativeProducer<TypedAbstractSyntaxTree<?>>> arguments) {
        PriorityQueue<AlternativeProducer<Parameter>> parameters = new PriorityQueue<>(
                Comparator.comparing(parameter -> parameter.peek().getWeight())
        );

        for(int i = 0; i < arguments.size(); i++) {
            if(arguments.get(i).hasNext()) {
                final int j = i;

                parameters.add(arguments.get(i).map(tree -> new Parameter(j, tree));
            }
        }

        Iterator<AlternativeProducer.Alternatives<Parameter>> itor = new Iterator<AlternativeProducer.Alternatives<Parameter>>() {
            @Override
            public boolean hasNext() {
                return !parameters.isEmpty();
            }

            @Override
            public AlternativeProducer.Alternatives<Parameter> next() {
                AlternativeProducer<Parameter> result = parameters.poll();

                AlternativeProducer.Alternatives<Parameter> value = result.next();

                if(result.hasNext())
                    parameters.add(result);

                return value;
            }
        };

        return
                itor
        );

                arguments

                functionLookup.get(function)
                        .stream()
                        .filter(signature -> signature.arguments.size() == arguments.size())
                        .map(signature -> {
                            List<List<TypedAbstractSyntaxTree<?>>> acceptedTypes = arguments.stream()
                                    .map(argument -> new ArrayList<TypedAbstractSyntaxTree<?>>())
                                    .collect(Collectors.toList());

                            for(int i = 0; i < arguments.size(); i++) {
                                Type expectedType = signature.getArguments().get(i);

                                for(TypedAbstractSyntaxTree<?> argument : arguments.get(i)) {
                                    TypedAbstractSyntaxTree<?> convertedArgument = implicitConversion(argument, expectedType);

                                    acceptedTypes.get(i).add(convertedArgument);
                                }

                                if(acceptedTypes.get(i).isEmpty())
                                    return null;
                            }

                            return new AbstractMap.SimpleEntry<>(signature, acceptedTypes);
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ))
        );
    }

    private TypedAbstractSyntaxTree<?> implicitConversion(TypedAbstractSyntaxTree<?> actual, Type expected) {
        Integer weight = converter.implicitConversion(actual.getType(), expected);

//        if(weight == null)
        return null;

//        return new TypedAbstractSyntaxTree<>()
    }
}