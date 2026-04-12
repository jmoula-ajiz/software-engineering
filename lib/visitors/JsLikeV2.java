package lib.visitors;

import java.util.function.Function;

import lib.expression.ExpressionVisitorV2;
import lib.expression.LambdaExpression;
import lib.expression.ExpressionV2;
import port.IHandlerFactory;

public class JsLikeV2 extends ExpressionToJsLikeSyntax<ExpressionV2> implements ExpressionVisitorV2<String, ExpressionV2> {
    private final IHandlerFactory<ExpressionV2> hf;

    public JsLikeV2(IHandlerFactory<ExpressionV2> h, Function<ExpressionV2, String> p, ExpressionV2 e) {
        super(h, p, e);
        this.hf = h;
    }

    @Override
    public String visitLambda(LambdaExpression<ExpressionV2> e) {
        return e.parameter + " => " + e.body.accept(new JsLikeV2(hf, hf.jsLikeSyntaxPrinter(), e.body));
    }
}
