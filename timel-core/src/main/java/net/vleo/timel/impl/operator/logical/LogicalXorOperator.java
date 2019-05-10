package net.vleo.timel.impl.operator.logical;

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

import net.vleo.timel.annotation.Prototype;
import net.vleo.timel.annotation.Parameter;
import net.vleo.timel.annotation.Returns;
import net.vleo.timel.type.BooleanType;

/**
 * Implementation of the logical xor operator that applies the following truth table:
 * <p>
 * +------+------+-------+
 * |  A   |  B   | A ^ B |
 * +------+------+-------+
 * | 0    | 0    | 0     |
 * | 0    | 1    | 1     |
 * | 0    | null | null  |
 * | 1    | 0    | 1     |
 * | 1    | 1    | 0     |
 * | 1    | null | null  |
 * | null | 0    | null  |
 * | null | 1    | null  |
 * | null | null | null  |
 * +------+------+-------+
 *
 * @author Andrea Leofreddi
 */
@Prototype(
        name = "^",
        returns = @Returns(type = BooleanType.class),
        parameters = {
                @Parameter(type = BooleanType.class),
                @Parameter(type = BooleanType.class)
        }
)
public class LogicalXorOperator extends AbstractTrivalentLogicalOperator {
    @Override
    protected Boolean apply(Boolean t, Boolean u) {
        if(t == null || u == null)
            return null;

        return t ^ u;
    }
}

