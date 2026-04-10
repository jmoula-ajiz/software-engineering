package lib.expression;

public class Conditional<E> implements Expression<E> {
    Conditional(E condition, E whenTrue, E whenFalse) {
        this.condition = condition;
        this.whenTrue = whenTrue;
        this.whenFalse = whenFalse;
    }
    public <R>R accept(ExpressionVisitor<R, E> visitor) {return visitor.visit(this); }
    public final E condition;
    public final E whenTrue;
    public final E whenFalse;
}