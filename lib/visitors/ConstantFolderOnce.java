package lib.visitors;

import java.util.function.BiFunction;
import java.util.function.Function;

import lib.expression.*;
import lib.utils.Either;
import lib.utils.EitherVisitor;
import port.IExpressionFactory;

public class ConstantFolderOnce<E> implements ExpressionVisitor<E, E> {
    private final IExpressionFactory<E> factory;
    private final E e;
    private final Function<E, Either<Literal<E>, E>> isLiteral;

    public ConstantFolderOnce(IExpressionFactory<E> factory, E e, Function<E, Either<Literal<E>, E>> isLiteral) {
        this.factory = factory;
        this.e = e;
        this.isLiteral = isLiteral;
    }

    private E whenBoth(E left, E right, E otherwise,
            BiFunction<Literal<E>, Literal<E>, String> whenBoth) {
        return whenLiteral(left, otherwise, leftLiteral -> whenLiteral(right, otherwise,
                rightLiteral -> factory.literal(whenBoth.apply(leftLiteral, rightLiteral))));
    }

    private E whenLiteral(E expression, E otherwise,
            Function<Literal<E>, E> whenLiteral) {
        return isLiteral.apply(expression).accept(new EitherVisitor<Literal<E>, E, E>() {
            public E left(Literal<E> literal) {
                return whenLiteral.apply(literal);
            }

            public E right(E e) {
                return otherwise;
            }
        });
    }

    public E visit(VariableReference<E> _e) {
        return e;
    }

    public E visit(Literal<E> _e) {
        return e;
    }

    public E visit(FunctionCall<E> _e) {
        return e;
    }

    public E visit(Addition<E> expression) {
        return whenBoth(expression.left, expression.right, e,
                (left, right) -> Integer.toString(left.asInt() + right.asInt()));
    };

    public E visit(Subtraction<E> expression) {
        return whenBoth(expression.left, expression.right, e,
                (left, right) -> Integer.toString(left.asInt() - right.asInt()));
    };

    public E visit(Multiplication<E> expression) {
        return whenBoth(expression.left, expression.right, e,
                (left, right) -> Integer.toString(left.asInt() * right.asInt()));
    };

    public E visit(Division<E> expression) {
        return whenBoth(expression.dividend, expression.divisor, e,
                (left, right) -> Integer.toString(left.asInt() / right.asInt()));
    };

    public E visit(Modulo<E> expression) {
        return whenBoth(expression.left, expression.right, e,
                (left, right) -> Integer.toString(left.asInt() % right.asInt()));
    };

    public E visit(Exponentiation<E> expression) {
        return whenBoth(expression.base, expression.exponent, e,
                (base, exponent) -> Integer.toString((int) Math.pow(base.asInt(), exponent.asInt())));
    };

    public E visit(Negation<E> expression) {
        return whenLiteral(expression.operand, e,
                literal -> factory.literal(Integer.toString(-literal.asInt())));
    };

    public E visit(Equality<E> expression) {
        return whenBoth(expression.left, expression.right, e,
                (left, right) -> left.asInt().intValue() == right.asInt().intValue() ? "1"
                        : "0");
    };

    public E visit(Inequality<E> expression) {
        return whenBoth(expression.left, expression.right, e,
                (left, right) -> left.asInt().intValue() != right.asInt().intValue() ? "1"
                        : "0");
    };

    public E visit(LessThan<E> expression) {
        return whenBoth(expression.left, expression.right, e,
                (left, right) -> left.asInt().intValue() < right.asInt().intValue() ? "1"
                        : "0");
    };

    public E visit(GreaterThan<E> expression) {
        return whenBoth(expression.left, expression.right, e,
                (left, right) -> left.asInt().intValue() > right.asInt().intValue() ? "1"
                        : "0");
    };

    public E visit(LessThanOrEqual<E> expression) {
        return whenBoth(expression.left, expression.right, e,
                (left, right) -> left.asInt().intValue() <= right.asInt().intValue() ? "1"
                        : "0");
    };

    public E visit(GreaterThanOrEqual<E> expression) {
        return whenBoth(expression.left, expression.right, e,
                (left, right) -> left.asInt().intValue() >= right.asInt().intValue() ? "1"
                        : "0");
    };

    public E visit(Conditional<E> c) {
        return whenLiteral(c.condition, e,
                literal -> literal.asInt() != 0 ? c.whenTrue : c.whenFalse);
    };

    public E visit(LogicalNot<E> expression) {
        return whenLiteral(expression.operand, e,
                literal -> factory.literal(literal.asInt() == 0 ? "1" : "0"));
    };

    public E visit(Conjunction<E> expression) {
        return whenLiteral(expression.left, e,
                left -> left.asInt() == 0 ? factory.literal("0") : expression.right);
    };

    public E visit(Disjunction<E> expression) {
        return whenLiteral(expression.left, e,
                left -> left.asInt() != 0 ? factory.literal("1") : expression.right);
    };
}
