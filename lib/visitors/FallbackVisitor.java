package lib.visitors;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import lib.expression.*;

public abstract class FallbackVisitor<R, E> implements ExpressionVisitor<R, E> {
    private final Function<Expression<E>, R> fallback;

    public FallbackVisitor(Function<Expression<E>, R> fallback) {
        this.fallback = fallback;
    }

    private R empty(Expression<E> e) {
        return fallback.apply(e);
    }

    public R visit(Literal<E> e) {
        return empty(e);
    }
    public R visit(VariableReference<E> e) {
        return empty(e);
    }
    public R visit(Addition<E> e) {
        return empty(e);
    }
    public R visit(Subtraction<E> e) {
        return empty(e);
    }
    public R visit(Multiplication<E> e) {
        return empty(e);
    }
    public R visit(Division<E> e) {
        return empty(e);
    }
    public R visit(Negation<E> e) {
        return empty(e);
    }
    public R visit(Modulo<E> e) {
        return empty(e);
    }
    public R visit(Exponentiation<E> e) {
        return empty(e);
    }
    public R visit(Equality<E> e) {
        return empty(e);
    }
    public R visit(Inequality<E> e) {
        return empty(e);
    }
    public R visit(LessThan<E> e) {
        return empty(e);
    }
    public R visit(GreaterThan<E> e) {
        return empty(e);
    }
    public R visit(LessThanOrEqual<E> e) {
        return empty(e);
    }
    public R visit(GreaterThanOrEqual<E> e) {
        return empty(e);
    }
    public R visit(Conjunction<E> e) {
        return empty(e);
    }
    public R visit(Disjunction<E> e) {
        return empty(e);
    }
    public R visit(LogicalNot<E> e) {
        return empty(e);
    }
    public R visit(Conditional<E> e) {
        return empty(e);
    }
    public R visit(FunctionCall<E> e) {
        return empty(e);
    }
    
}