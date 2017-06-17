package net.vleo.timel.compiler;

import static java.util.Collections.emptyList;

public class ZeroConstant extends AbstractSyntaxTree {

    public ZeroConstant() {
        super(emptyList());
    }

    @Override
    public <T> T accept(AbstractSyntaxTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
