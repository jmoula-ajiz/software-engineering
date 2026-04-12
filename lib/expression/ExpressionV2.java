package lib.expression;

public class ExpressionV2 implements Expression<ExpressionV2> {
    private final Expression<ExpressionV2> wrappee;

    public ExpressionV2(Expression<ExpressionV2> wrappee) {
        this.wrappee = wrappee;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R, ExpressionV2> visitor) {
        return wrappee.accept(visitor);
    }
}
