package net.vleo.timel.impl.target;

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

import lombok.var;
import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.impl.intermediate.tree.AbstractSyntaxTree;
import net.vleo.timel.impl.target.tree.AbstractTargetTree;
import net.vleo.timel.iterator.UpscalableIterator;
import net.vleo.timel.time.Interval;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.*;

/**
 * @author Andrea Leofreddi
 */
class TargetTreeDumperTest {
    private final TargetTreeDumper targetTreeDumper = new TargetTreeDumper();

    private static class DummyTargetTree extends AbstractTargetTree {
        DummyTargetTree(AbstractSyntaxTree reference, List<AbstractTargetTree> children) {
            super(reference, children);
        }

        @Override
        public UpscalableIterator<Object> evaluate(Interval interval, ExecutorContext context) {
            return null;
        }
    }

    @Test
    void dumpShouldWriteOneLinePerNode() {
        AbstractTargetTree leaf1 = new DummyTargetTree(null, Collections.emptyList()),
                leaf2 = new DummyTargetTree(null, Collections.emptyList()),
                parent = new DummyTargetTree(null, Arrays.asList(leaf1, leaf2));

        var actual = targetTreeDumper.dump(parent);

        assertThat(actual.length() - actual.replaceAll("\n", "").length(), is(3));
    }
}
