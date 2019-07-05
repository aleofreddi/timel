package net.vleo.timel.impl.function.version;

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

import lombok.val;
import net.vleo.timel.impl.downscaler.SameDownscaler;
import net.vleo.timel.impl.target.Evaluable;
import net.vleo.timel.impl.upscaler.SameUpscaler;
import net.vleo.timel.time.Interval;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;

/**
 * @author Andrea Leofreddi
 */
class VersionFunctionTest {
    private static final Interval INTERVAL = Interval.of(0, 1000);

    @Test
    void evaluateShouldReturnVersionAndCommitId() {
        val version = new VersionFunction();

        val itor = version.evaluate(INTERVAL, null, new SameUpscaler<>(), new SameDownscaler<>(), new Evaluable[0]);

        assertThat(itor.hasNext(), is(true));
        val actual = itor.next();
        assertThat(actual.getInterval(), is(INTERVAL));
        assertThat(actual.getValue(), is("V1 (42)"));
    }
}
