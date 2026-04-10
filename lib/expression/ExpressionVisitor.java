package lib.expression;

import java.util.function.Consumer;
import java.util.function.Function;

public interface ExpressionVisitor<R, E> {

    R visit(Literal<E> e);
    R visit(VariableReference<E> e);
    R visit(Addition<E> e);
    R visit(Subtraction<E> e);
    R visit(Multiplication<E> e);
    R visit(Division<E> e);
    R visit(Negation<E> e);
    R visit(Modulo<E> e);
    R visit(Exponentiation<E> e);
    R visit(Equality<E> e);
    R visit(Inequality<E> e);
    R visit(LessThan<E> e);
    R visit(GreaterThan<E> e);
    R visit(LessThanOrEqual<E> e);
    R visit(GreaterThanOrEqual<E> e);
    R visit(Conjunction<E> e);
    R visit(Disjunction<E> e);
    R visit(LogicalNot<E> e);
    R visit(Conditional<E> e);
    R visit(FunctionCall<E> e);
}
