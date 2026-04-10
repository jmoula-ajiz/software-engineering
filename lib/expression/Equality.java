package lib.expression;

public class Equality<E> implements Expression<E> {
    Equality(E left, E right) {
        this.left = left;
        this.right = right;
    }
    public <R>R accept(ExpressionVisitor<R, E> visitor) {return visitor.visit(this); }
    public final E left;
    public final E right;
}