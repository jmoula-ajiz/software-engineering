package lib.visitors;

import lib.expression.*;
import port.IExpressionDict;

public class IsomorphicGetter<T, E> implements ExpressionVisitor<T, E> {
    private final IExpressionDict<T> values;

    public IsomorphicGetter(IExpressionDict<T> values) {
        this.values = values;
    }

    public T visit(Literal<E> expression) { return values.literal(); }
    public T visit(VariableReference<E> expression) { return values.variableReference(); }
    public T visit(Addition<E> expression) { return values.addition(); }
    public T visit(Subtraction<E> expression) { return values.subtraction(); }
    public T visit(Multiplication<E> expression) { return values.multiplication(); }
    public T visit(Division<E> expression) { return values.division(); }
    public T visit(Negation<E> expression) { return values.negation(); }
    public T visit(Modulo<E> expression) { return values.modulo(); }
    public T visit(Exponentiation<E> expression) { return values.exponentiation(); }
    public T visit(Equality<E> expression) { return values.equality(); }
    public T visit(Inequality<E> expression) { return values.inequality(); }
    public T visit(LessThan<E> expression) { return values.lessThan(); }
    public T visit(GreaterThan<E> expression) { return values.greaterThan(); }
    public T visit(LessThanOrEqual<E> expression) { return values.lessThanOrEqual(); }
    public T visit(GreaterThanOrEqual<E> expression) { return values.greaterThanOrEqual(); }
    public T visit(Conjunction<E> expression) { return values.conjunction(); }
    public T visit(Disjunction<E> expression) { return values.disjunction(); }
    public T visit(LogicalNot<E> expression) { return values.logicalNot(); }
    public T visit(Conditional<E> expression) { return values.conditional(); }
    public T visit(FunctionCall<E> expression) { return values.functionCall(); }
}