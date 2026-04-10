package lib.expression;

import java.util.List;
import port.IExpressionFactory;

abstract class AFactory<E> implements IExpressionFactory<E> {

    protected abstract E wrap(Expression<E> expression);
    public E literal(String value) {
        return wrap(new Literal<>(value));
    }

    public E variableReference(String name) {
        return wrap(new VariableReference<>(name));
    }

    public E addition(E left, E right) {
        return wrap(new Addition<>(left, right));
    }

    public E subtraction(E left, E right) {
        return wrap(new Subtraction<>(left, right));
    }

    public E multiplication(E left, E right) {
        return wrap(new Multiplication<>(left, right));
    }

    public E division(E dividend, E divisor) {
        return wrap(new Division<>(dividend, divisor));
    }

    public E negation(E operand) {
        return wrap(new Negation<>(operand));
    }

    public E modulo(E left, E right) {
        return wrap(new Modulo<>(left, right));
    }

    public E exponentiation(E base, E exponent) {
        return wrap(new Exponentiation<>(base, exponent));
    }

    public E equality(E left, E right) {
        return wrap(new Equality<>(left, right));
    }

    public E inequality(E left, E right) {
        return wrap(new Inequality<>(left, right));
    }

    public E lessThan(E left, E right) {
        return wrap(new LessThan<>(left, right));
    }

    public E greaterThan(E left, E right) {
        return wrap(new GreaterThan<>(left, right));
    }

    public E lessThanOrEqual(E left, E right) {
        return wrap(new LessThanOrEqual<>(left, right));
    }

    public E greaterThanOrEqual(E left, E right) {
        return wrap(new GreaterThanOrEqual<>(left, right));
    }

    public E conjunction(E left, E right) {
        return wrap(new Conjunction<>(left, right));
    }

    public E disjunction(E left, E right) {
        return wrap(new Disjunction<>(left, right));
    }

    public E logicalNot(E operand) {
        return wrap(new LogicalNot<>(operand));
    }

    public E conditional(E condition, E whenTrue, E whenFalse) {
        return wrap(new Conditional<>(condition, whenTrue, whenFalse));
    }

    public E functionCall(E callee, List<E> arguments) {
        return wrap(new FunctionCall<>(callee, arguments));
    }
}

public class Factory extends AFactory<ExpressionV1> {
    public Factory() {}

    @Override
    protected ExpressionV1 wrap(Expression<ExpressionV1> expression) {
        return new ExpressionV1(expression);
    }
}