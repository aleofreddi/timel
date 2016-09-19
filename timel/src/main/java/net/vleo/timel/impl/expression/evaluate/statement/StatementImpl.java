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
package net.vleo.timel.impl.expression.evaluate.statement;

import net.vleo.timel.executor.ExecutorContext;
import net.vleo.timel.compiler.tree.StatementNode;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.SimpleTreeNode;
import net.vleo.timel.time.Interval;
import net.vleo.timel.type.StatementType;
import net.vleo.timel.type.Types;

import java.util.Arrays;
import java.util.List;

/**
 * Compound expression function implementation.
 *
 * @author Andrea Leofreddi
 */
public class StatementImpl extends SimpleTreeNode implements StatementNode {
    public static final String TOKEN = "CompoundExpression";

    public static final String EXPRESSION_SEPARATOR = ";";

    private static final String PRETTY_EXPRESSION_SEPARATOR = "; ";

    private TreeNode head, tail;

    public StatementImpl(TreeNode head) {
        super(Types.getStatementType());

        this.head = head;
    }

    public StatementImpl(TreeNode head, TreeNode tail) {
        super(Types.getStatementType());

        this.head = head;
        this.tail = tail;
    }

    @Override
    public String toCanonicalExpression() {
        StringBuilder sb = new StringBuilder();

        sb.append(head.toCanonicalExpression());

        if(tail != null) {
            sb.append(PRETTY_EXPRESSION_SEPARATOR);

            sb.append(tail.toCanonicalExpression());
        }

        return sb.toString();
    }

    @Override
    public String treeDump() {
        return toCanonicalExpression();
    }

    @Override
    public List<TreeNode> getChildren() {
        if(tail == null)
            return Arrays.asList(head);

        return Arrays.asList(head, tail);
    }

    private void executeNode(TreeNode node, Interval interval, ExecutorContext context) {
        StatementNode statementNode = (StatementNode) node;

        statementNode.execute(interval, context);
    }

    public void execute(Interval interval, ExecutorContext context) {
        executeNode(head, interval, context);

        if(tail != null)
            executeNode(tail, interval, context);
    }
}
