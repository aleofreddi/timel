package net.vleo.timel.compiler;

import lombok.Getter;

import java.util.List;

@Getter
public class FunctionCallNode extends AbstractSyntaxTree {

    private final String function;

    public FunctionCallNode(String function, List<AbstractSyntaxTree> arguments) {
        super(arguments);
        this.function = function;
    }

    @Override
    public <T> T accept(AbstractSyntaxTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
