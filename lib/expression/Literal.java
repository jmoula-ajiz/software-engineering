package lib.expression;

import java.util.function.Function;

public class Literal<E> implements Expression<E> {
    Literal(String value) { this.value = value; }
    public <R>R accept(ExpressionVisitor<R, E> visitor) {return visitor.visit(this); }
    public final String value;
    public Integer asInt() { return Integer.parseInt(value); }
}