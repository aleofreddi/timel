package net.vleo.timel.compiler;

import net.vleo.timel.Type;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

public class TypeTree {
    private final ParseTree node;
    private final Type result;
    private final List<TypeTree> children;

    public Type getResult() {
        return result;
    }

    public List<TypeTree> getChildren() {
        return children;
    }

    public TypeTree(ParseTree node, Type result, List<TypeTree> children) {
        this.node = node;
        this.result = result;
        this.children = children;
    }

    public void dump() {
        dump(0);
    }

    private void dump(int level) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < level; i++)
            sb.append("  ");
        System.out.println(sb.toString() + node.getText() + " -> " + result);
        for(TypeTree child : children)
            child.dump(level + 1);
    }
}
