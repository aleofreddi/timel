package net.vleo.timel.function;

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

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.vleo.timel.ConfigurationException;
import net.vleo.timel.ParseException;
import net.vleo.timel.annotation.Constraint;
import net.vleo.timel.annotation.FunctionPrototypes;
import net.vleo.timel.annotation.Parameter;
import net.vleo.timel.annotation.FunctionPrototype;
import net.vleo.timel.conversion.Conversion;
import net.vleo.timel.impl.intermediate.tree.AbstractSyntaxTree;
import net.vleo.timel.impl.intermediate.tree.Cast;
import net.vleo.timel.impl.intermediate.tree.FunctionCall;
import net.vleo.timel.impl.parser.tree.AbstractParseTree;
import net.vleo.timel.tuple.Pair;
import net.vleo.timel.type.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static net.vleo.timel.annotation.FunctionPrototype.NULL_VARIABLE;

/**
 * Function registry. This class will hold references to all the known {@link Function}s, and allow the resolution of the correct one given its arguments.
 *
 * @author Andrea Leofreddi
 */
@RequiredArgsConstructor
public class FunctionRegistry {
    @Data
    private static class FunctionCallMatch {
        private final Function<Object> function;
        private final FunctionPrototype functionPrototype;
        private final Type<?> returnType;
        private final List<AbstractSyntaxTree> arguments;

        private final int weight;
    }

    @Data
    private static class BoundedArgument {
        private final AbstractSyntaxTree input;
        private final Type<?> inputType;
        private final Type<?> inputTemplate;
        private final Parameter parameter;

        private AbstractSyntaxTree output;
        private List<Conversion<Object, Object>> outputConversion;
        private int weight;

        BoundedArgument(AbstractSyntaxTree input, Parameter parameter) {
            this.input = input;
            this.parameter = parameter;

            inputType = input.getType();
            inputTemplate = inputType.template();
        }
    }

    @Getter
    private final TypeSystem typeSystem;
    private final Map<String, Set<Pair<FunctionPrototype, Function<?>>>> functions = new HashMap<>();

    /**
     * Add a function to the registry.
     *
     * @param function Function to register
     */
    public void add(Function<?> function) {
        val functionClass = function.getClass();

        FunctionPrototype functionPrototype = functionClass.getDeclaredAnnotation(FunctionPrototype.class);
        FunctionPrototypes functionPrototypes = functionClass.getDeclaredAnnotation(FunctionPrototypes.class);

        if(functionPrototype != null && functionPrototypes != null)
            throw new IllegalArgumentException("Class " + functionClass + " is annotated with both " + FunctionPrototype.class.getName() + " and " + FunctionPrototypes.class.getName());
        if(functionPrototype == null && functionPrototypes == null)
            throw new IllegalArgumentException("Class " + functionClass + " should be annotated with " + FunctionPrototype.class.getName() + " or " + FunctionPrototypes.class.getName());

        // Register all the prototypes
        (functionPrototype == null ? Arrays.asList(functionPrototypes.value()) : singletonList(functionPrototype)).stream()
                .map(entry -> new Pair<FunctionPrototype, Function<?>>(entry, function))
                .forEach(entry ->
                        functions.computeIfAbsent(entry.getFirst().name(), key -> new HashSet<>())
                                .add(entry)
                );
    }

    /**
     * Add all the functions to the registry.
     *
     * @param functions Collections of functions to be registered
     */
    public void addAll(Collection<Function<?>> functions) {
        functions.forEach(this::add);
    }

    /**
     * Lookup a function for the given arguments.
     *
     * @param reference Source reference node
     * @param function  Function to lookup
     * @param arguments Call arguments
     * @return A {@link FunctionCall} applying the given function to the passed arguments (casting them when needed).
     * @throws ParseException When the lookup failed
     */
    public FunctionCall lookup(AbstractParseTree reference, String function, List<AbstractSyntaxTree> arguments) throws ParseException {
        List<FunctionCallMatch> alternatives = functions.getOrDefault(function, emptySet())
                .stream()
                .map(registryFunction -> functionMatches(registryFunction.getFirst(), registryFunction.getSecond(), arguments))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(FunctionCallMatch::getWeight))
                .collect(toList());

        if(alternatives.isEmpty())
            throw new ParseException("Cannot resolve function " + getSignature(function, arguments));

        if(alternatives.size() > 1 && alternatives.get(0).getWeight() == alternatives.get(1).getWeight())
            throw new ParseException("Ambiguous function call, matches: " +
                    alternatives.stream()
                            .map(alternative -> getSignature(alternative.getFunctionPrototype()))
                            .collect(Collectors.joining("; ")));

        val match = alternatives.get(0);
        return new FunctionCall(
                reference,
                match.getFunction(),
                getSignature(match.getFunctionPrototype()),
                match.getReturnType(),
                match.getArguments()
        );
    }

    private FunctionCallMatch functionMatches(FunctionPrototype functionPrototype, Function<?> function, List<AbstractSyntaxTree> arguments) throws ConfigurationException {
        val functionClass = function.getClass();
        val metaReturns = functionPrototype.returns();
        val metaParameters = functionPrototype.parameters();

        List<Parameter> declaredParameters = new ArrayList<>(), declaredVarArgs = new ArrayList<>();

        Map<String, Class<? extends TemplateType>> variableContraints = Arrays.stream(functionPrototype.constraints())
                .collect(toMap(
                        Constraint::variable,
                        Constraint::template
                ));

        // Collect the parameters and varargs
        boolean varArgs = false;
        for(val metaParameter : metaParameters) {
            if(varArgs && !metaParameter.varArgs())
                throw new IllegalStateException("FunctionCall " + functionClass + ": varArgs are only allowed at the tail of the parameters list");

            if(metaParameter.varArgs()) {
                varArgs = true;
                declaredVarArgs.add(metaParameter);
            } else
                declaredParameters.add(metaParameter);
        }

        // Processed function arguments
        List<BoundedArgument> functionArguments = new LinkedList<>();

        // Pair parameters to their respective method argument
        val currentArgument = arguments.iterator();
        for(val declaredParameter : declaredParameters) {
            if(!currentArgument.hasNext())
                return null;

            functionArguments.add(new BoundedArgument(currentArgument.next(), declaredParameter));
        }

        // Bail out if there are too many arguments
        if(currentArgument.hasNext() && declaredVarArgs.isEmpty())
            return null;

        // Pair tail varArgs group (if any)
        while(currentArgument.hasNext()) {
            for(val declaredVarArg : declaredVarArgs) {
                if(!currentArgument.hasNext())
                    return null;

                functionArguments.add(new BoundedArgument(currentArgument.next(), declaredVarArg));
            }
        }

        // Resolve the intermediate variables from all the arguments.
        Map<String, Optional<Type<?>>> typeVariableGuess = functionArguments.stream()
                .filter(functionArgument -> !NULL_VARIABLE.equals(functionArgument.getParameter().variable()))
                .collect(Collectors.groupingBy(
                        functionArgument -> functionArgument.getParameter().variable(),
                        Collectors.toList()
                ))
                .entrySet()
                .stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(
                        entry.getKey(),
                        resolveVariableType(
                                function,
                                entry.getKey(),
                                entry.getValue(),
                                Optional.ofNullable(variableContraints.get(entry.getKey()))
                                        .map(this::newInstance)
                        )
                )).collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        // Bail out if a variable has not been resolved
        if(!typeVariableGuess.values()
                .stream()
                .allMatch(Optional::isPresent))
            return null;

        // Strip out the optional from type variables, as they are all present
        //noinspection OptionalGetWithoutIsPresent
        Map<String, Type> typeVariables = typeVariableGuess.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().get()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        // Convert arguments into AbstractSyntaxTree nodes
        for(val functionArgument : functionArguments) {
            Type<?> sourceType = functionArgument.getInput().getType(), targetType;

            boolean isVariable = !functionArgument.getParameter().variable().equals(NULL_VARIABLE);

            if(!isVariable)
                targetType = Types.instance((Class<? extends Type<Object>>) functionArgument.getParameter().type());
            else
                targetType = typeVariables.get(functionArgument.parameter.variable());

            // Convert the source type into target type
            if(!sourceType.equals(targetType)) {
                ConversionResult result = typeSystem.getConcretePath(true, sourceType, targetType);

                if(result == null)
                    return null;

                if(targetType.isSpecializedTemplate()) {
                    if(!targetType.equals(result.getResultType()))
                        return null;
                } else if(targetType.isUnboundTemplate())
                    targetType = result.getResultType();

                functionArgument.setOutputConversion(result.getConversions());
                functionArgument.setOutput(new Cast(
                        functionArgument.getInput().getReference(),
                        functionArgument.getInput(),
                        targetType,
                        result.getConversions()
                ));
                functionArgument.setWeight(result.getConversions().size());

                // If a template was resolved, upgrade the variable type accordingly
                if(isVariable)
                    typeVariables.put(functionArgument.parameter.variable(), targetType);
            } else {
                functionArgument.setOutput(functionArgument.getInput());
                functionArgument.setWeight(0);
            }
        }

        // Calculate the total call weight (in terms of intermediate casting)
        int weight = functionArguments.stream()
                .mapToInt(BoundedArgument::getWeight)
                .sum();

        // Get the return intermediate
        Type<?> returnType = null;

        if(!NULL_VARIABLE.equals(metaReturns.variable()))
            // If return intermediate is a variable, get it
            returnType = typeVariables.get(metaReturns.variable());
        else if(metaReturns.type() != FunctionPrototype.NilType.class)
            returnType = newInstance(metaReturns.type());

        // If the return intermediate is a non-specialized template, rely on Function's resolveReturnType
        if(returnType == null || !returnType.isConcrete()) {
            Optional<Type> deductedReturnType = function.resolveReturnType(
                    returnType,
                    typeVariables,
                    functionArguments.stream()
                            .map(BoundedArgument::getInputType)
                            .toArray(Type[]::new)
            );

            if(!deductedReturnType.isPresent())
                return null;

            returnType = deductedReturnType.get();
        }

        return new FunctionCallMatch(
                (Function<Object>) function,
                functionPrototype,
                returnType,
                functionArguments.stream()
                        .map(arg -> arg.output)
                        .collect(toList()),
                weight
        );
    }

    private String getSignature(String function, List<AbstractSyntaxTree> arguments) {
        return function + "(" +
                arguments.stream()
                        .map(AbstractSyntaxTree::getType)
                        .map(Object::toString)
                        .collect(Collectors.joining(", "))
                + ")";
    }

    private String getSignature(FunctionPrototype functionPrototype) {
        val parameters = functionPrototype.parameters();

        return functionPrototype.name() + "(" +
                Arrays.stream(parameters)
                        .map(parameter -> {
                            String type;

                            if(parameter.type() != FunctionPrototype.NilType.class) {
                                type = Types.instance((Class<? extends Type<Object>>) parameter.type()).toString();
                                try {
                                    type = parameter.type().getDeclaredConstructor().newInstance().toString();
                                } catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                                    type = "?unknown?";
                                }
                            } else
                                type = "$" + parameter.variable();

                            if(parameter.varArgs())
                                type += "*";

                            return type;
                        })
                        .collect(Collectors.joining(", "))
                + ")";
    }

    private Optional<Type<?>> resolveVariableType(Function<?> function, String variable, List<BoundedArgument> arguments, Optional<Type<?>> constraintType) {
        // Use type system to see if all the variables are convertible to a single type
        Optional<Type<?>> resolvedType = typeSystem.leastUpperBound(
                true,
                Stream.concat(
                        arguments.stream()
                                .map(boundArgument -> boundArgument.input.getType().template()),
                        constraintType.isPresent() ? Stream.of(constraintType.get()) : Stream.empty()
                )
                        .collect(toSet())
        );

        if(!resolvedType.isPresent())
            return Optional.empty();

        if(constraintType.isPresent() && !resolvedType.get().equals(constraintType.get()))
            return Optional.empty();

        Type resolved = resolvedType.get();

        // If they are convertible to a template, use programmatic specialisation
        if(resolved instanceof TemplateType)
            return function.specializeVariableTemplate(
                    variable,
                    (TemplateType) resolved,
                    arguments.stream()
                            .map(BoundedArgument::getInputType)
                            .toArray(Type[]::new)
            );

        return resolvedType;
    }

    private <T> T newInstance(Class<? extends T> class_) {
        try {
            return class_.getDeclaredConstructor().newInstance();
        } catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(new ParseException("Cannot instantiate type " + class_));
        }
    }
}
