package lib.expression;

public class GreaterThanOrEqual<E> implements Expression<E> {
    GreaterThanOrEqual(E left, E right) {
        this.left = left;
        this.right = right;
    }
    public <R>R accept(ExpressionVisitor<R, E> visitor) {return visitor.visit(this); }
    public final E left;
    public final E right;
}