package net.vleo.timel.impl.function.time;

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

import net.vleo.timel.impl.target.tree.TargetTreeMocks;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.IntegerType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrea Leofreddi
 */
class ExtractFunctionTest {
    @Test
    void shouldThrowEvaluationExceptionWhenInvalidCalendarField() {
        assertThrows(IllegalArgumentException.class, () ->
                new ExtractFunction()
                        .evaluate(
                                Interval.of(0, 864000),
                                null,
                                new IntegerType().getUpscaler(),
                                new IntegerType().getDownscaler(),
                                TargetTreeMocks.constant("invalid_value"),
                                TargetTreeMocks.constant("UTC")
                        )
                        .next()
        );
    }

    @Test
    void shouldThrowEvaluationExceptionWhenInvalidTimeZone() {
        assertThrows(IllegalArgumentException.class, () ->
                new ExtractFunction()
                        .evaluate(
                                Interval.of(0, 864000),
                                null,
                                new IntegerType().getUpscaler(),
                                new IntegerType().getDownscaler(),
                                TargetTreeMocks.constant("DAY_OF_WEEK"),
                                TargetTreeMocks.constant("invalid_zone")
                        )
                        .next()
        );
    }
}
