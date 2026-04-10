package lib.expression;

public class Division<E> implements Expression<E> {
    Division(E dividend, E divisor) {
        this.dividend = dividend;
        this.divisor = divisor;
    }
    public <R>R accept(ExpressionVisitor<R, E> visitor) {return visitor.visit(this); }
    public final E dividend;
    public final E divisor;
}