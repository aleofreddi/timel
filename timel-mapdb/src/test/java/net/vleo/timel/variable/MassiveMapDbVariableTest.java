package net.vleo.timel.variable;

/*-
 * #%L
 * TimEL MapDB backend
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
import net.vleo.timel.ParseException;
import net.vleo.timel.TimEL;
import net.vleo.timel.Expression;
import net.vleo.timel.time.Interval;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

/**
 * @author Andrea Leofreddi
 */
public class MassiveMapDbVariableTest {
    @Test
    void shouldWorkWithALotOfData() throws ParseException {
        Expression expression = TimEL.parse("V = scale(1, every(1, \"SECOND_OF_MINUTE\", \"UTC\"))")
                .withVariableFactory((id, type) -> new MapDbVariable<>())
                .compile();

        System.out.println("Evaluating...");

        val itor = TimEL.evaluate(
                expression,
                Interval.of(
                        new DateTime(2000, 1, 1, 0, 0).getMillis(),
                        new DateTime(2000, 1, 2, 0, 0).getMillis()
                )
        );

        while(itor.hasNext())
            itor.next();

        System.out.println("Done!");
    }
}
