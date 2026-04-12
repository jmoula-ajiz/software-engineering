package lib.expression;

public class ExpressionV2 implements Expression<ExpressionV2> {
    private final Expression<ExpressionV2> wrappee;

    public ExpressionV2(Expression<ExpressionV2> wrappee) {
        this.wrappee = wrappee;
    }

    /** The wrapped expression node (e.g. {@link FunctionCall}, {@link LambdaExpression}). */
    public Expression<ExpressionV2> unwrap() {
        return wrappee;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R, ExpressionV2> visitor) {
        return wrappee.accept(visitor);
    }
}
