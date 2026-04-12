package lib.expression;

import port.IExpressionFactory2;

public class Factory2 extends AFactory<ExpressionV2> implements IExpressionFactory2<ExpressionV2> {
    @Override
    protected ExpressionV2 wrap(Expression<ExpressionV2> expression) {
        return new ExpressionV2(expression);
    }

    @Override
    public ExpressionV2 lambdaExpression(String parameterName, ExpressionV2 body) {
        return wrap(new LambdaExpression<>(parameterName, body));
    }
}
