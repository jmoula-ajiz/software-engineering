package lib.expression;
import lib.visitors.IntegerEvaluationVisitor;
public final class LambdaExpression<E> implements Expression<E> {
    public final String parameter;
    public final E body;
    public LambdaExpression(String p, E b) { parameter = p; body = b; }
    public <R> R accept(ExpressionVisitor<R, E> v) {
        if (v instanceof IntegerEvaluationVisitor) throw new IllegalArgumentException("Cannot directly evaluate a lambda expression");
        if (v instanceof ExpressionVisitorV2<R, E> x) return x.visitLambda(this);
        throw new IllegalArgumentException("Unsupported visitor for lambda expression");
    }
}
