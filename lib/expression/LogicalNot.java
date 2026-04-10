package lib.expression;

public class LogicalNot<E> implements Expression<E> {
    LogicalNot(E operand) { this.operand = operand; }
    public <R>R accept(ExpressionVisitor<R, E> visitor) {return visitor.visit(this); }
    public final E operand;
}