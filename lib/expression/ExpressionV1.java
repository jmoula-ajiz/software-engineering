package lib.expression;

public class ExpressionV1 implements Expression<ExpressionV1> {
    private final Expression<ExpressionV1> wrappee;

    public ExpressionV1(Expression<ExpressionV1> wrappee) {
        this.wrappee = wrappee;
    }
    
    public <R> R accept(ExpressionVisitor<R, ExpressionV1> visitor) {
        return wrappee.accept(visitor);
    }

}
