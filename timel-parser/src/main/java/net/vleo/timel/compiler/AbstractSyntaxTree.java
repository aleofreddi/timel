package net.vleo.timel.compiler;

import java.util.List;

/**
 * Created by Andrea Leofreddi on 07/07/2017.
 */
public abstract class AbstractSyntaxTree {
    private final List<AbstractSyntaxTree> children;

    public AbstractSyntaxTree(List<AbstractSyntaxTree> children) {
        this.children = children;
    }

    public abstract <T> T accept(AbstractSyntaxTreeVisitor<T> visitor);

    public List<AbstractSyntaxTree> getChildren() {
        return children;
    }
}
