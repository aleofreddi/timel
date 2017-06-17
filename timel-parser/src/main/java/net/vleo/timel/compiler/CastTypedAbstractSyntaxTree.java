package net.vleo.timel.compiler;

import lombok.Data;
import net.vleo.timel.Type;

@Data
public class CastTypedAbstractSyntaxTree<T extends AbstractSyntaxTree> extends TypedAbstractSyntaxTree<T> {
    private final Type sourceType;
}
