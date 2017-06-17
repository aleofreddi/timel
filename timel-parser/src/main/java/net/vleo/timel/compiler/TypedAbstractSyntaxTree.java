package net.vleo.timel.compiler;

import lombok.Data;
import net.vleo.timel.Type;

@Data
public class TypedAbstractSyntaxTree<T extends AbstractSyntaxTree> implements Weighted {
    private final T tree;

    private final Type type;
    private final int weight;

    public TypedAbstractSyntaxTree(T tree, Type type, int weight) {
        this.tree = tree;
        this.type = type;
        this.weight = weight;
    }
}
