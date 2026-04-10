package lib.expression;

public class Conjunction<E> implements Expression<E> {
    Conjunction(E left, E right) {
        this.left = left;
        this.right = right;
    }
    public <R>R accept(ExpressionVisitor<R, E> visitor) {return visitor.visit(this); }
    public final E left;
    public final E right;
}