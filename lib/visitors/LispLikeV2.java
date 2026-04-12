package lib.visitors;

import lib.expression.ExpressionVisitorV2;
import lib.expression.LambdaExpression;
import lib.expression.ExpressionV2;
import port.IHandlerFactory;

public class LispLikeV2 extends ExpressionToLispLikeSyntax<ExpressionV2> implements ExpressionVisitorV2<String, ExpressionV2> {
    private final IHandlerFactory<ExpressionV2> hf;

    public LispLikeV2(IHandlerFactory<ExpressionV2> h) {
        super(h);
        this.hf = h;
    }

    @Override
    public String visitLambda(LambdaExpression<ExpressionV2> e) {
        return "(lambda (" + e.parameter + ") " + e.body.accept(new LispLikeV2(hf)) + ")";
    }
}
