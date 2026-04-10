package lib.expression;

import java.util.List;
public class FunctionCall<E> implements Expression<E> {
    FunctionCall(E callee, List<E> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }
    public <R>R accept(ExpressionVisitor<R, E> visitor) {return visitor.visit(this); }
    public final E callee;
    public final List<E> arguments;
}