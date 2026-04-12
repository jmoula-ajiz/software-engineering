package lib.expression;
public class ExpressionV2 implements Expression<ExpressionV2> {
    private final Expression<ExpressionV2> w;
    public ExpressionV2(Expression<ExpressionV2> x) { w = x; }
    public Expression<ExpressionV2> unwrap() { return w; }
    @Override public <R> R accept(ExpressionVisitor<R, ExpressionV2> v) { return w.accept(v); }
}
