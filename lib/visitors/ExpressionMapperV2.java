package lib.visitors;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import lib.expression.*;
import port.IExpressionFactory2;
import port.IHandlerFactory;

public class ExpressionMapperV2 extends ExpressionMapper<ExpressionV2> implements ExpressionVisitorV2<ExpressionV2, ExpressionV2> {
    private final IExpressionFactory2<ExpressionV2> f2;

    public ExpressionMapperV2(
            IHandlerFactory<ExpressionV2> handlers,
            BiFunction<ExpressionV2, Supplier<ExpressionV2>, ExpressionV2> recurse,
            BiFunction<ExpressionV2, ExpressionMapper<ExpressionV2>, ExpressionV2> acceptVisitor) {
        super(handlers, recurse, acceptVisitor);
        this.f2 = (IExpressionFactory2<ExpressionV2>) handlers.expressionFactory();
    }

    @Override
    public ExpressionV2 visitLambda(LambdaExpression<ExpressionV2> e) {
        return f2.lambdaExpression(e.parameter, apply(e.body));
    }
}
