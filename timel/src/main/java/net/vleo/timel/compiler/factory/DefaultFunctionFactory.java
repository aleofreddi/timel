/*
 * Copyright 2014-2016 Andrea Leofreddi
 *
 * This file is part of TimEL.
 *
 * TimEL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TimEL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TimEL.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.vleo.timel.compiler.factory;

import net.vleo.timel.compiler.CompilerContext;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.impl.expression.evaluate.constant.ConstantFactory;
import net.vleo.timel.impl.expression.evaluate.function.array.ArrayFactory;
import net.vleo.timel.impl.expression.evaluate.function.cast.CastFactory;
import net.vleo.timel.impl.expression.evaluate.function.comparison.ComparisonFactory;
import net.vleo.timel.impl.expression.evaluate.function.conditional.ConditionalFactory;
import net.vleo.timel.impl.expression.evaluate.function.eval.EvalFactory;
import net.vleo.timel.impl.expression.evaluate.function.math.MathFactory;
import net.vleo.timel.impl.expression.evaluate.function.resample.ResampleFactory;
import net.vleo.timel.impl.expression.evaluate.function.time.*;
import net.vleo.timel.impl.expression.evaluate.function.variable.VariableFactory;
import net.vleo.timel.impl.expression.evaluate.operator.logical.LogicalOperatorFactory;
import net.vleo.timel.impl.expression.evaluate.operator.relational.RelationalOperatorFactory;
import net.vleo.timel.impl.expression.evaluate.statement.StatementFactory;
import net.vleo.timel.impl.expression.evaluate.type.MetaTypeFactory;
import net.vleo.timel.impl.expression.evaluate.variable.UnknownVariableNode;
import net.vleo.timel.impl.expression.compile.PlusFactory;
import net.vleo.timel.impl.expression.compile.PowerFactory;
import net.vleo.timel.impl.expression.compile.RoundFactory;
import net.vleo.timel.impl.expression.compile.TimesFactory;
import net.vleo.timel.impl.type.UnknownVariableType;

import java.util.Arrays;
import java.util.List;

/**
 * Default TimEL function factory.
 *
 * @author Andrea Leofreddi
 */
public final class DefaultFunctionFactory extends FunctionFactory {
    private static DefaultFunctionFactory INSTANCE = null;

    private static List<FunctionFactory> STANDARD_FACTORIES = Arrays.asList(
            // VariableFactory must be always the first factory invoked
            new VariableFactory(),

            // Constants
            new ConstantFactory(),

            // Types
            new MetaTypeFactory(),

            new ArrayFactory(),
            new CastFactory(),
            new ComparisonFactory(),
            new ConditionalFactory(),
            new EvalFactory(),
            new MathFactory(),
            new PlusFactory(),
            TimesFactory.get(),
            new PowerFactory(),
            new LogicalOperatorFactory(),
            new ResampleFactory(),
            new RoundFactory(),
            new TimeFactory(),
            new RelationalOperatorFactory(),
            new StatementFactory()
    );

    /**
     * Retrieve the singleton instance of this class.
     *
     * @return An instance of DefaultFunctionFactory.
     */
    public static final DefaultFunctionFactory get() {
        if(INSTANCE == null)
            synchronized(DefaultFunctionFactory.class) {
                if(INSTANCE == null)
                    INSTANCE = new DefaultFunctionFactory();
            }

        return INSTANCE;
    }

    private DefaultFunctionFactory() {
    }

    @Override
    public void preVisit(String id, List<String> arguments, CompilerContext context) throws ParseException {
        // Invoke the previsit function of delegated factories
        for(FunctionFactory factory : STANDARD_FACTORIES)
            factory.preVisit(id, arguments, context);
    }

    @Override
    public TreeNode instance(String id, List<TreeNode> arguments, CompilerContext context) throws ParseException {
        // Invoke the instance function of delegated factories
        for(FunctionFactory factory : STANDARD_FACTORIES) {
            TreeNode r = factory.instance(id, arguments, context);

            if(r != null)
                return r;

            // If we are not inside an assign block, unassigned variables are NOT allowed
            for(TreeNode node : arguments)
                if(UnknownVariableType.get().isAssignableFrom(node.getType())) {
                    String variable = ((UnknownVariableNode) node).getId();

                    throw new ParseException("Unknown variable " + variable);
                }
        }

        return null;
    }
}
