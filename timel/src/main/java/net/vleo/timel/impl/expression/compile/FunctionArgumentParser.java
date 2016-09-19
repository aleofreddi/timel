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
package net.vleo.timel.impl.expression.compile;

import net.vleo.timel.impl.compiler.MissingArgumentException;
import net.vleo.timel.compiler.ParseException;
import net.vleo.timel.compiler.tree.TreeNode;
import net.vleo.timel.compiler.tree.ValueNode;
import net.vleo.timel.type.Type;
import net.vleo.timel.type.ValueType;
import net.vleo.timel.impl.expression.utils.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Chop parser.
 *
 * @author Andrea Leofreddi
 */
public class FunctionArgumentParser {
    private String function;

    private List<TreeNode> arguments;

    private List<Predicate<TreeNode>> predicates = new ArrayList<Predicate<TreeNode>>();

    private int position = -1;

    public FunctionArgumentParser(String function, List<TreeNode> arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    public class ParsedElement {
        private int position;

        private boolean isLast = false;

        private Predicate<Type> typePredicate = null;

        private Predicate<TreeNode> nodePredicate = null;

        private ParsedElement(int position) {
            this.position = position;
        }

        public ParsedElement withType(Predicate<Type> predicate) {
            typePredicate = predicate;

            return this;
        }

        public ParsedElement withNode(Predicate<TreeNode> predicate) {
            nodePredicate = predicate;

            return this;
        }

        public ParsedElement last() throws ParseException {
            isLast = true;

            return this;
        }

        public <T extends TreeNode> T chop() throws ParseException {
            TreeNode validItem = getValidItem();

            return (T) validItem;
        }

        public <V> ValueNode<V> chopGenValue() throws ParseException {
            TreeNode validItem = getValidItem();

            return (ValueNode<V>) validItem;
        }

        public TreeNode chop2() throws ParseException {
            TreeNode validItem = getValidItem();

            return validItem;
        }

        public <V> ValueType<V> genType() throws ParseException {
            TreeNode validItem = getValidItem();

            return (ValueType<V>) validItem.getType();
        }

        public <T extends ValueNode<V>, V> T chopValue() throws ParseException {
            TreeNode validItem = getValidItem();

            return (T) validItem;
        }

        public <T extends ValueType<V>, V> T type() throws ParseException {
            TreeNode validItem = getValidItem();

            return (T) validItem.getType();
        }

        public Type absType() throws ParseException {
            TreeNode validItem = getValidItem();

            return validItem.getType();
        }

        public Type type2() throws ParseException {
            return getValidItem().getType();
        }

        private TreeNode getValidItem() throws ParseException {
            if(position == -1)
                throw new IllegalStateException("Invoked current() without calling next() first");

            int errPosition = position + 1;

            if(isLast && position + 1 != arguments.size())
                throw new MissingArgumentException(function, errPosition);

            if(position >= arguments.size())
                throw new MissingArgumentException(function, errPosition);

            TreeNode output = arguments.get(position);

            if(typePredicate != null && !typePredicate.apply(output.getType()))
                throw new ParseException(function + ": at argument " + errPosition + " expected type " + typePredicate + ", got " + output.getType());

            if(nodePredicate != null && !nodePredicate.apply(output))
                throw new ParseException(function + ": at argument " + errPosition + " expected value " + nodePredicate + ", got " + output);

            return output;
        }
    }

    public ParsedElement current() {
        return new ParsedElement(position);
    }

    public ParsedElement next() {
        position++;

        return new ParsedElement(position);
    }

    public boolean hasNext() {
        return position + 1 < arguments.size();
    }

    public int remaining() {
        return arguments.size() - position - 1;
    }

    public void empty() throws ParseException {
        if(hasNext())
            throw new ParseException("Too many arguments for the " + function + " function");
    }

    public <T> ValueNode<T> chop(Predicate<Type> typePredicate, Predicate<TreeNode> nodePredicate) throws ParseException {
        if(position > arguments.size())
            throw new ParseException("Not enough arguments for the " + function + " function");

        TreeNode argument = arguments.get(position++);

        if(typePredicate != null && !typePredicate.apply(argument.getType()))
            throw new ParseException("Argument #" + position + " of the " + function + " function violates the constraint " + typePredicate);

        if(nodePredicate != null && !nodePredicate.apply(argument))
            throw new ParseException("Argument #" + position + " of the " + function + " function violates the constraint " + nodePredicate);

        return (ValueNode<T>) argument;
    }
}
