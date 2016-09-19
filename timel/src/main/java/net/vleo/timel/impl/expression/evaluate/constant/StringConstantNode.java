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
package net.vleo.timel.impl.expression.evaluate.constant;

import net.vleo.timel.type.Types;

/**
 * A constant string value.
 *
 * @author Andrea Leofreddi
 */
public class StringConstantNode extends SimpleConstantNode<String> {
    public StringConstantNode(String value) {
        super(Types.getStringType(), value);
    }

    @Override
    public String toCanonicalExpression() {
        return "\"" + getValue() + "\"";
    }

    @Override
    public String treeDump() {
        return getValue();
    }
}
