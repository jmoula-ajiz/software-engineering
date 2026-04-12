package lib.expression;
public interface ExpressionVisitorV2<R, E> extends ExpressionVisitor<R, E> { R visitLambda(LambdaExpression<E> e); }
