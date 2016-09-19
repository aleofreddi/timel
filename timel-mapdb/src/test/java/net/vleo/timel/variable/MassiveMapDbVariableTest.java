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
package net.vleo.timel.variable;

import net.vleo.timel.TimEL;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.Program;
import net.vleo.timel.executor.variable.Variable;
import net.vleo.timel.executor.variable.VariableFactory;
import net.vleo.timel.time.Interval;
import net.vleo.timel.time.IntervalMapsTest;
import net.vleo.timel.type.ValueType;
import org.joda.time.DateTime;
import org.junit.Test;

/**
 * @author Andrea Leofreddi
 */
public class MassiveMapDbVariableTest {
    @Test
    public void f() throws ParseException {
        Program p = TimEL
                .getCompiler(
                        "V = Extract[Start, \"SECOND_OF_MINUTE\", \"UTC\"];" +
                                "K = V + Scalar[Extract[Start, \"MONTH_OF_YEAR\", \"UTC\"], Every[1, \"MONTH_OF_YEAR\", \"UTC\"]] * 10"
                )
                .compileProgram();

        System.out.println("Evaluating...");

        TimEL
                .getExecutor(p)
                .autoBind(new VariableFactory() {
                    @Override
                    public <V> Variable<V> newVariable(String id, ValueType<V> type) {
                        return new MapDbVariable<V>();
                    }
                })
                .executeFor(
                        Interval.of(
                                new DateTime(2000, 1, 1, 0, 0).getMillis(),
                                new DateTime(2000, 1, 2, 0, 0).getMillis()
                        )
                );

        System.out.println("Done!");
    }
}
