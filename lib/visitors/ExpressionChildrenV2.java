package lib.visitors;

import java.util.List;

import lib.expression.LambdaExpression;

public class ExpressionChildrenV2<E> extends ExpressionChildren<E> implements lib.expression.ExpressionVisitorV2<List<E>, E> {
    @Override
    public List<E> visitLambda(LambdaExpression<E> e) {
        return List.of(e.body);
    }
}
