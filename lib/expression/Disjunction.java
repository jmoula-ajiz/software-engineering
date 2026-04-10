package lib.expression;

public class Disjunction<E> implements Expression<E> {
    Disjunction(E left, E right) {
        this.left = left;
        this.right = right;
    }
    public <R>R accept(ExpressionVisitor<R, E> visitor) {return visitor.visit(this); }
    public final E left;
    public final E right;
}