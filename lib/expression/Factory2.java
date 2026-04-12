package lib.expression;
import port.IExpressionFactory2;
public class Factory2 extends AFactory<ExpressionV2> implements IExpressionFactory2<ExpressionV2> {
    @Override protected ExpressionV2 wrap(Expression<ExpressionV2> e) { return new ExpressionV2(e); }
    @Override public ExpressionV2 lambdaExpression(String p, ExpressionV2 b) { return wrap(new LambdaExpression<>(p, b)); }
}
