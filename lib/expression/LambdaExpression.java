package lib.expression;

import lib.visitors.IntegerEvaluationVisitor;

public final class LambdaExpression<E> implements Expression<E> {
    public final String parameter;
    public final E body;

    public LambdaExpression(String parameter, E body) {
        this.parameter = parameter;
        this.body = body;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R, E> visitor) {
        if (visitor instanceof IntegerEvaluationVisitor) {
            throw new IllegalArgumentException("Cannot directly evaluate a lambda expression");
        }
        if (visitor instanceof ExpressionVisitorV2<R, E> v2) {
            return v2.visitLambda(this);
        }
        throw new IllegalArgumentException("Unsupported visitor for lambda expression");
    }
}
