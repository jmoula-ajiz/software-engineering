package lib.visitors;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import lib.expression.*;
import port.IHandlerFactory;
import port.IExpressionFactory;

public class ExpressionMapper<E> implements ExpressionVisitor<E, E>, Function<E, E> {
    private final BiFunction<E, Supplier<E>, E> recurse;
    private final BiFunction<E, ExpressionMapper<E>, E> acceptVisitor;
    private final IExpressionFactory<E> factory;
    private final IHandlerFactory<E> handlers;

    public ExpressionMapper(
            IHandlerFactory<E> handlers,
            BiFunction<E, Supplier<E>, E> recurse,
            BiFunction<E, ExpressionMapper<E>, E> acceptVisitor) {
        this.factory = handlers.expressionFactory();
        this.handlers = handlers;
        this.recurse = recurse;
        this.acceptVisitor = acceptVisitor;
    }

    public E apply(E e) {
        return recurse.apply(e, () -> acceptVisitor.apply(e, this));
    }

    private List<E> produceAll(List<E> expressions) {
        return expressions.stream().map(this::apply).toList();
    }

    public E visit(Literal<E> expression) {
        return factory.literal(expression.value);
    }

    public E visit(VariableReference<E> expression) {
        return factory.variableReference(expression.name);
    }

    public E visit(Addition<E> expression) {
        return factory.addition(apply(expression.left), apply(expression.right));
    }

    public E visit(Subtraction<E> expression) {
        return factory.subtraction(apply(expression.left), apply(expression.right));
    }

    public E visit(Multiplication<E> expression) {
        return factory.multiplication(apply(expression.left), apply(expression.right));
    }

    public E visit(Division<E> expression) {
        return factory.division(apply(expression.dividend), apply(expression.divisor));
    }

    public E visit(Negation<E> expression) {
        return factory.negation(apply(expression.operand));
    }

    public E visit(Modulo<E> expression) {
        return factory.modulo(apply(expression.left), apply(expression.right));
    }

    public E visit(Exponentiation<E> expression) {
        return factory.exponentiation(apply(expression.base), apply(expression.exponent));
    }

    public E visit(Equality<E> expression) {
        return factory.equality(apply(expression.left), apply(expression.right));
    }

    public E visit(Inequality<E> expression) {
        return factory.inequality(apply(expression.left), apply(expression.right));
    }

    public E visit(LessThan<E> expression) {
        return factory.lessThan(apply(expression.left), apply(expression.right));
    }

    public E visit(GreaterThan<E> expression) {
        return factory.greaterThan(apply(expression.left), apply(expression.right));
    }

    public E visit(LessThanOrEqual<E> expression) {
        return factory.lessThanOrEqual(apply(expression.left), apply(expression.right));
    }

    public E visit(GreaterThanOrEqual<E> expression) {
        return factory.greaterThanOrEqual(apply(expression.left), apply(expression.right));
    }

    public E visit(Conjunction<E> expression) {
        return factory.conjunction(apply(expression.left), apply(expression.right));
    }

    public E visit(Disjunction<E> expression) {
        return factory.disjunction(apply(expression.left), apply(expression.right));
    }

    public E visit(LogicalNot<E> expression) {
        return factory.logicalNot(apply(expression.operand));
    }

    public E visit(Conditional<E> expression) {
        return factory.conditional(apply(expression.condition), apply(expression.whenTrue),
                apply(expression.whenFalse));
    }

    public E visit(FunctionCall<E> expression) {
        return factory.functionCall(apply(expression.callee), produceAll(expression.arguments));
    }
}