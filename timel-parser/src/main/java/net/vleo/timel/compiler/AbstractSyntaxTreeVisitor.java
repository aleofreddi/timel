package net.vleo.timel.compiler;

/**
 * Created by Andrea Leofreddi on 07/07/2017.
 */
public interface AbstractSyntaxTreeVisitor<T> {
    T visit(DeclarationNode declarationNode);
    T visit(FunctionCallNode functionCallNode);
    T visit(IntegerConstant integerConstant);
    T visit(FloatConstant integerLeaf);
    T visit(ZeroConstant zeroConstant);
}
