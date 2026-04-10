package lib.expression;

public interface Expression<E> {
    <R>R accept(ExpressionVisitor<R, E> visitor);
}
