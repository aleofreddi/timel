package net.vleo.timel.compiler;

import static java.util.Arrays.asList;

/**
 * Created by Andrea Leofreddi on 07/07/2017.
 */
public class DeclarationNode extends AbstractSyntaxTree {

    private final String identifier;

    public DeclarationNode(String identifier, AbstractSyntaxTree value) {
        super(asList(value));
        this.identifier = identifier;
    }

    @Override
    public <T> T accept(AbstractSyntaxTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
