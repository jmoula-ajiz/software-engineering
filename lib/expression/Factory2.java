package lib.expression;
import lib.visitors.IntegerEvaluationVisitor;
import port.*;
public class Factory2 extends AFactory<ExpressionV2> implements IExpressionFactory2<ExpressionV2> {
    public static final class LambdaExpression<E> implements Expression<E> {
        public final String parameter;
        public final E body;
        public LambdaExpression(String p, E b) { parameter = p; body = b; }
        public <R> R accept(ExpressionVisitor<R, E> v) {
            if (v instanceof IntegerEvaluationVisitor) throw new IllegalArgumentException("Cannot directly evaluate a lambda expression");
            if (v instanceof Factory2.ExpressionVisitorV2<R, E> x) return x.visitLambda(this);
            throw new IllegalArgumentException("Unsupported visitor for lambda expression");
        }
    }
    public interface ExpressionVisitorV2<R, E> extends ExpressionVisitor<R, E> { R visitLambda(LambdaExpression<E> e); }
    protected ExpressionV2 wrap(Expression<ExpressionV2> e) { return new ExpressionV2(e); }
    public ExpressionV2 lambdaExpression(String p, ExpressionV2 b) { return wrap(new LambdaExpression<>(p, b)); }
}
