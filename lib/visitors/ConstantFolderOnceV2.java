package lib.visitors;

import java.util.function.Function;

import lib.expression.*;
import lib.utils.Either;
import port.IExpressionFactory2;

public class ConstantFolderOnceV2 implements ExpressionVisitorV2<ExpressionV2, ExpressionV2> {
    private final ConstantFolderOnce<ExpressionV2> inner;
    private final ExpressionV2 root;

    public ConstantFolderOnceV2(
            IExpressionFactory2<ExpressionV2> factory,
            ExpressionV2 root,
            Function<ExpressionV2, Either<Literal<ExpressionV2>, ExpressionV2>> isLiteral) {
        this.root = root;
        this.inner = new ConstantFolderOnce<>(factory, root, isLiteral);
    }

    public ExpressionV2 visit(Literal<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(VariableReference<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(Addition<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(Subtraction<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(Multiplication<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(Division<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(Negation<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(Modulo<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(Exponentiation<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(Equality<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(Inequality<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(LessThan<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(GreaterThan<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(LessThanOrEqual<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(GreaterThanOrEqual<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(Conjunction<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(Disjunction<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(LogicalNot<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(Conditional<ExpressionV2> e) {
        return inner.visit(e);
    }

    public ExpressionV2 visit(FunctionCall<ExpressionV2> e) {
        return inner.visit(e);
    }

    @Override
    public ExpressionV2 visitLambda(LambdaExpression<ExpressionV2> e) {
        return root;
    }
}
