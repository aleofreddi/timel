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

import net.vleo.timel.annotation.FunctionPrototype;
import net.vleo.timel.annotation.FunctionPrototypes;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.impl.target.Evaluable;
import net.vleo.timel.impl.downscaler.Downscaler;
import net.vleo.timel.impl.upscaler.Upscaler;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.TemplateType;
import net.vleo.timel.type.Type;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An interface to define a TimEL function. This interface should be annotated with {@link FunctionPrototype} (or {@link FunctionPrototypes}) so to
 * properly declare the function prototype.
 *
 * @author Andrea Leofreddi
 */
public interface Function<T> {
    /**
     * Binds a variable to a template specialization when dealing with template types.
     * <p>
     * The default implementation allows variables to bind only to a particular template specialization, if matched by all occurrences.
     * <p>
     * By overriding this method, one can change the default behavior so to accept different templates specializations.
     *
     * @param variable      Variable name to specialize
     * @param template      Template type to specialize
     * @param argumentTypes List of argument types
     * @return The deduced template type if any
     */
    default Optional<Type<?>> specializeVariableTemplate(String variable, TemplateType template, Type<?>... argumentTypes) {
        Set<List<Object>> parametersSet = Arrays.stream(argumentTypes)
                .map(Type::getParameters)
                .collect(Collectors.toSet());

        if(parametersSet.size() > 1)
            return Optional.empty();

        return Optional.of(template.specialize(parametersSet.iterator().next().toArray()));
    }

    /**
     * Helps the compiler to resolve the return type for a possible function call candidate. This method is invoked by the compiler due to one of the following
     * conditions:
     *
     * <ul>
     * <li>The function's metadata has no information about the return type</li>
     * <li>The function return type is a template</li>
     * <li>The function return type is bound to a variable, that resolved to a template</li>
     * </ul>
     * <p>
     * The default implementation is to return empty, which will make the function a non match.
     *
     * @param proposed      The return value guessed so far
     * @param variables     A map of the variable types resolved
     * @param argumentTypes All argument types
     * @return The specialized return type if any
     */
    default Optional<Type> resolveReturnType(Type proposed, Map<String, Type> variables, Type... argumentTypes) {
        return Optional.empty();
    }

    /**
     * Evaluates the function.
     *
     * @param interval   Evaluation interval
     * @param context    Executor context
     * @param upscaler   Result upscaler. This one is provided to be coherent with the expected return type.
     * @param downscaler Result downscaler. This one is provided to be coherent with the expected return type.
     * @param arguments  List of arguments to evaluate.
     * @return Evaluation result iterator
     */
    UpscalableIterator<T> evaluate(Interval interval, ExecutorContext context, Upscaler<T> upscaler, Downscaler<T> downscaler, Evaluable<?>... arguments);

    /**
     * Convenience function to evaluate all the given arguments for the same interval and context.
     *
     * @param <T>       Expected return value Java type
     * @param interval  Evaluation interval
     * @param context   Evaluation context
     * @param arguments Arguments to evaluate
     * @return An array containing the evaluation result
     */
    static <T> UpscalableIterator<T>[] evaluateAll(Interval interval, ExecutorContext context, Evaluable<? extends T>... arguments) {
        return Arrays.stream(arguments)
                .map(argument -> argument.evaluate(interval, context))
                .toArray(UpscalableIterator[]::new);
    }
}
