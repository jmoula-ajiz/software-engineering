package lib.visitors;

import java.util.function.Function;

import lib.expression.Expression;
import lib.expression.ExpressionVisitorV2;
import lib.expression.LambdaExpression;

public abstract class FallbackVisitorV2<R, E> extends FallbackVisitor<R, E> implements ExpressionVisitorV2<R, E> {
    private final Function<Expression<E>, R> fb;

    public FallbackVisitorV2(Function<Expression<E>, R> fallback) {
        super(fallback);
        this.fb = fallback;
    }

    @Override
    public R visitLambda(LambdaExpression<E> e) {
        return fb.apply(e);
    }
}
