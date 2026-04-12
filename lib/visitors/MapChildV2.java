package lib.visitors;

import java.util.List;
import java.util.function.UnaryOperator;

import lib.expression.*;
import port.IExpressionFactory2;

public class MapChildV2 implements ExpressionVisitorV2<ExpressionV2, ExpressionV2> {
    private final IExpressionFactory2<ExpressionV2> f;
    private final UnaryOperator<ExpressionV2> op;

    public MapChildV2(IExpressionFactory2<ExpressionV2> f, UnaryOperator<ExpressionV2> op) {
        this.f = f;
        this.op = op;
    }

    public ExpressionV2 visit(Literal<ExpressionV2> e) {
        return f.literal(e.value);
    }

    public ExpressionV2 visit(VariableReference<ExpressionV2> e) {
        return f.variableReference(e.name);
    }

    public ExpressionV2 visit(Addition<ExpressionV2> e) {
        return f.addition(op.apply(e.left), op.apply(e.right));
    }

    public ExpressionV2 visit(Subtraction<ExpressionV2> e) {
        return f.subtraction(op.apply(e.left), op.apply(e.right));
    }

    public ExpressionV2 visit(Multiplication<ExpressionV2> e) {
        return f.multiplication(op.apply(e.left), op.apply(e.right));
    }

    public ExpressionV2 visit(Division<ExpressionV2> e) {
        return f.division(op.apply(e.dividend), op.apply(e.divisor));
    }

    public ExpressionV2 visit(Negation<ExpressionV2> e) {
        return f.negation(op.apply(e.operand));
    }

    public ExpressionV2 visit(Modulo<ExpressionV2> e) {
        return f.modulo(op.apply(e.left), op.apply(e.right));
    }

    public ExpressionV2 visit(Exponentiation<ExpressionV2> e) {
        return f.exponentiation(op.apply(e.base), op.apply(e.exponent));
    }

    public ExpressionV2 visit(Equality<ExpressionV2> e) {
        return f.equality(op.apply(e.left), op.apply(e.right));
    }

    public ExpressionV2 visit(Inequality<ExpressionV2> e) {
        return f.inequality(op.apply(e.left), op.apply(e.right));
    }

    public ExpressionV2 visit(LessThan<ExpressionV2> e) {
        return f.lessThan(op.apply(e.left), op.apply(e.right));
    }

    public ExpressionV2 visit(GreaterThan<ExpressionV2> e) {
        return f.greaterThan(op.apply(e.left), op.apply(e.right));
    }

    public ExpressionV2 visit(LessThanOrEqual<ExpressionV2> e) {
        return f.lessThanOrEqual(op.apply(e.left), op.apply(e.right));
    }

    public ExpressionV2 visit(GreaterThanOrEqual<ExpressionV2> e) {
        return f.greaterThanOrEqual(op.apply(e.left), op.apply(e.right));
    }

    public ExpressionV2 visit(Conjunction<ExpressionV2> e) {
        return f.conjunction(op.apply(e.left), op.apply(e.right));
    }

    public ExpressionV2 visit(Disjunction<ExpressionV2> e) {
        return f.disjunction(op.apply(e.left), op.apply(e.right));
    }

    public ExpressionV2 visit(LogicalNot<ExpressionV2> e) {
        return f.logicalNot(op.apply(e.operand));
    }

    public ExpressionV2 visit(Conditional<ExpressionV2> e) {
        return f.conditional(op.apply(e.condition), op.apply(e.whenTrue), op.apply(e.whenFalse));
    }

    public ExpressionV2 visit(FunctionCall<ExpressionV2> e) {
        return f.functionCall(op.apply(e.callee), mapArgs(e.arguments));
    }

    @Override
    public ExpressionV2 visitLambda(LambdaExpression<ExpressionV2> e) {
        return f.lambdaExpression(e.parameter, op.apply(e.body));
    }

    private List<ExpressionV2> mapArgs(List<ExpressionV2> a) {
        return a.stream().map(op).toList();
    }
}
