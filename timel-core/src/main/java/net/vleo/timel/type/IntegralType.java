package net.vleo.timel.type;

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

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A common superclass for all the Integral type templates.
 *
 * @author Andrea Leofreddi
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
class IntegralType<T> extends TemplateType<T> {
    public IntegralType(Integer degree) {
        super(degree);
    }

    public int getDegree() {
        return (int) getParameters().get(0);
    }
}
