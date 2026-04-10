package lib.visitors;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import ds.Dict;
import lib.expression.*;

public class IsomorphicSetter<T, E> implements ExpressionVisitor<Void, E> {
    private final Dict<T> values;
    private final T value;

    public IsomorphicSetter(Dict<T> values, T value) {
        this.values = values;
        this.value = value;
    }

    public Void visit(Literal<E> expression) {
        values.literal = value;
        return null;
    }

    public Void visit(VariableReference<E> expression) {
        values.variableReference = value;
        return null;
    }

    public Void visit(Addition<E> expression) {
        values.addition = value;
        return null;
    }

    public Void visit(Subtraction<E> expression) {
        values.subtraction = value;
        return null;
    }

    public Void visit(Multiplication<E> expression) {
        values.multiplication = value;
        return null;
    }

    public Void visit(Division<E> expression) {
        values.division = value;
        return null;
    }

    public Void visit(Negation<E> expression) {
        values.negation = value;
        return null;
    }

    public Void visit(Modulo<E> expression) {
        values.modulo = value;
        return null;
    }

    public Void visit(Exponentiation<E> expression) {
        values.exponentiation = value;
        return null;
    }

    public Void visit(Equality<E> expression) {
        values.equality = value;
        return null;
    }

    public Void visit(Inequality<E> expression) {
        values.inequality = value;
        return null;
    }

    public Void visit(LessThan<E> expression) {
        values.lessThan = value;
        return null;
    }

    public Void visit(GreaterThan<E> expression) {
        values.greaterThan = value;
        return null;
    }

    public Void visit(LessThanOrEqual<E> expression) {
        values.lessThanOrEqual = value;
        return null;
    }

    public Void visit(GreaterThanOrEqual<E> expression) {
        values.greaterThanOrEqual = value;
        return null;
    }

    public Void visit(Conjunction<E> expression) {
        values.conjunction = value;
        return null;
    }

    public Void visit(Disjunction<E> expression) {
        values.disjunction = value;
        return null;
    }

    public Void visit(LogicalNot<E> expression) {
        values.logicalNot = value;
        return null;
    }

    public Void visit(Conditional<E> expression) {
        values.conditional = value;
        return null;
    }

    public Void visit(FunctionCall<E> expression) {
        values.functionCall = value;
        return null;
    }
}