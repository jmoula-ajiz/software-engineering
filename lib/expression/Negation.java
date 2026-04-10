package lib.expression;

public class Negation<E> implements Expression<E> {
    Negation(E operand) { this.operand = operand; }
    public <R>R accept(ExpressionVisitor<R, E> visitor) {return visitor.visit(this); }
    public final E operand;
}