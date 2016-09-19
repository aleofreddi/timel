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
package net.vleo.timel.impl.expression.evaluate.type;

import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.impl.expression.evaluate.constant.NumericConstantNode;
import net.vleo.timel.type.Types;

/**
 * @author Andrea Leofreddi
 */
public class IntegralDoubleImpl extends AbstractMetaType {
    public static final String TOKEN = "IntegralDouble";

    private static int getDegree(NumericConstantNode degreeNode) throws ParseException {
        double value = degreeNode.getValue();

        if(Math.floor(value) != value)
            throw new ParseException(TOKEN + ": degree must be an integer");

        return (int) value;
    }

    public IntegralDoubleImpl(NumericConstantNode degreeNode) throws ParseException {
        super(TOKEN, Types.getIntegralDoubleType(getDegree(degreeNode)));
    }
}
