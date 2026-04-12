package lib.visitors;

import lib.expression.*;
import port.IExpressionDict2;

public class IsomorphicGetterV2<T, E> implements ExpressionVisitorV2<T, E> {
    private final IExpressionDict2<T> values;

    public IsomorphicGetterV2(IExpressionDict2<T> values) {
        this.values = values;
    }

    public T visit(Literal<E> e) {
        return values.literal();
    }

    public T visit(VariableReference<E> e) {
        return values.variableReference();
    }

    public T visit(Addition<E> e) {
        return values.addition();
    }

    public T visit(Subtraction<E> e) {
        return values.subtraction();
    }

    public T visit(Multiplication<E> e) {
        return values.multiplication();
    }

    public T visit(Division<E> e) {
        return values.division();
    }

    public T visit(Negation<E> e) {
        return values.negation();
    }

    public T visit(Modulo<E> e) {
        return values.modulo();
    }

    public T visit(Exponentiation<E> e) {
        return values.exponentiation();
    }

    public T visit(Equality<E> e) {
        return values.equality();
    }

    public T visit(Inequality<E> e) {
        return values.inequality();
    }

    public T visit(LessThan<E> e) {
        return values.lessThan();
    }

    public T visit(GreaterThan<E> e) {
        return values.greaterThan();
    }

    public T visit(LessThanOrEqual<E> e) {
        return values.lessThanOrEqual();
    }

    public T visit(GreaterThanOrEqual<E> e) {
        return values.greaterThanOrEqual();
    }

    public T visit(Conjunction<E> e) {
        return values.conjunction();
    }

    public T visit(Disjunction<E> e) {
        return values.disjunction();
    }

    public T visit(LogicalNot<E> e) {
        return values.logicalNot();
    }

    public T visit(Conditional<E> e) {
        return values.conditional();
    }

    public T visit(FunctionCall<E> e) {
        return values.functionCall();
    }

    @Override
    public T visitLambda(LambdaExpression<E> e) {
        return values.lambdaExpression();
    }
}
