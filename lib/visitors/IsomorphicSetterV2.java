package lib.visitors;

import ds.Dict2;
import lib.expression.*;

public class IsomorphicSetterV2<T, E> implements ExpressionVisitorV2<Void, E> {
    private final Dict2<T> values;
    private final T value;

    public IsomorphicSetterV2(Dict2<T> values, T value) {
        this.values = values;
        this.value = value;
    }

    public Void visit(Literal<E> e) {
        values.literal = value;
        return null;
    }

    public Void visit(VariableReference<E> e) {
        values.variableReference = value;
        return null;
    }

    public Void visit(Addition<E> e) {
        values.addition = value;
        return null;
    }

    public Void visit(Subtraction<E> e) {
        values.subtraction = value;
        return null;
    }

    public Void visit(Multiplication<E> e) {
        values.multiplication = value;
        return null;
    }

    public Void visit(Division<E> e) {
        values.division = value;
        return null;
    }

    public Void visit(Negation<E> e) {
        values.negation = value;
        return null;
    }

    public Void visit(Modulo<E> e) {
        values.modulo = value;
        return null;
    }

    public Void visit(Exponentiation<E> e) {
        values.exponentiation = value;
        return null;
    }

    public Void visit(Equality<E> e) {
        values.equality = value;
        return null;
    }

    public Void visit(Inequality<E> e) {
        values.inequality = value;
        return null;
    }

    public Void visit(LessThan<E> e) {
        values.lessThan = value;
        return null;
    }

    public Void visit(GreaterThan<E> e) {
        values.greaterThan = value;
        return null;
    }

    public Void visit(LessThanOrEqual<E> e) {
        values.lessThanOrEqual = value;
        return null;
    }

    public Void visit(GreaterThanOrEqual<E> e) {
        values.greaterThanOrEqual = value;
        return null;
    }

    public Void visit(Conjunction<E> e) {
        values.conjunction = value;
        return null;
    }

    public Void visit(Disjunction<E> e) {
        values.disjunction = value;
        return null;
    }

    public Void visit(LogicalNot<E> e) {
        values.logicalNot = value;
        return null;
    }

    public Void visit(Conditional<E> e) {
        values.conditional = value;
        return null;
    }

    public Void visit(FunctionCall<E> e) {
        values.functionCall = value;
        return null;
    }

    @Override
    public Void visitLambda(LambdaExpression<E> e) {
        values.lambdaExpression = value;
        return null;
    }
}
