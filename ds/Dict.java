package ds;

import port.IExpressionDict;

public class Dict<T> implements IExpressionDict<T> {
    public T literal;
    public T variableReference;
    public T addition;
    public T subtraction;
    public T multiplication;
    public T division;
    public T negation;
    public T modulo;
    public T exponentiation;
    public T equality;
    public T inequality;
    public T lessThan;
    public T greaterThan;
    public T lessThanOrEqual;
    public T greaterThanOrEqual;
    public T conjunction;
    public T disjunction;
    public T logicalNot;
    public T conditional;
    public T functionCall;

    public Dict(
            T literal,
            T variableReference,
            T addition,
            T subtraction,
            T multiplication,
            T division,
            T negation,
            T modulo,
            T exponentiation,
            T equality,
            T inequality,
            T lessThan,
            T greaterThan,
            T lessThanOrEqual,
            T greaterThanOrEqual,
            T conjunction,
            T disjunction,
            T logicalNot,
            T conditional,
            T functionCall) {
        this.literal = literal;
        this.variableReference = variableReference;
        this.addition = addition;
        this.subtraction = subtraction;
        this.multiplication = multiplication;
        this.division = division;
        this.negation = negation;
        this.modulo = modulo;
        this.exponentiation = exponentiation;
        this.equality = equality;
        this.inequality = inequality;
        this.lessThan = lessThan;
        this.greaterThan = greaterThan;
        this.lessThanOrEqual = lessThanOrEqual;
        this.greaterThanOrEqual = greaterThanOrEqual;
        this.conjunction = conjunction;
        this.disjunction = disjunction;
        this.logicalNot = logicalNot;
        this.conditional = conditional;
        this.functionCall = functionCall;
    }
    public Dict(IExpressionDict<T> dict) {
        this.literal = dict.literal();
        this.variableReference = dict.variableReference();
        this.addition = dict.addition();
        this.subtraction = dict.subtraction();
        this.multiplication = dict.multiplication();
        this.division = dict.division();
        this.negation = dict.negation();
        this.modulo = dict.modulo();
        this.exponentiation = dict.exponentiation();
        this.equality = dict.equality();
        this.inequality = dict.inequality();
        this.lessThan = dict.lessThan();
        this.greaterThan = dict.greaterThan();
        this.lessThanOrEqual = dict.lessThanOrEqual();
        this.greaterThanOrEqual = dict.greaterThanOrEqual();
        this.conjunction = dict.conjunction();
        this.disjunction = dict.disjunction();
        this.logicalNot = dict.logicalNot();
        this.conditional = dict.conditional();
        this.functionCall = dict.functionCall();
    }

    public Dict(T value) {
        this(
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value,
                value);
    }

    @Override
    public T literal() {
        return literal;
    }

    @Override
    public T variableReference() {
        return variableReference;
    }

    @Override
    public T addition() {
        return addition;
    }

    @Override
    public T subtraction() {
        return subtraction;
    }

    @Override
    public T multiplication() {
        return multiplication;
    }

    @Override
    public T division() {
        return division;
    }

    @Override
    public T negation() {
        return negation;
    }

    @Override
    public T modulo() {
        return modulo;
    }

    @Override
    public T exponentiation() {
        return exponentiation;
    }

    @Override
    public T equality() {
        return equality;
    }

    @Override
    public T inequality() {
        return inequality;
    }

    @Override
    public T lessThan() {
        return lessThan;
    }

    @Override
    public T greaterThan() {
        return greaterThan;
    }

    @Override
    public T lessThanOrEqual() {
        return lessThanOrEqual;
    }

    @Override
    public T greaterThanOrEqual() {
        return greaterThanOrEqual;
    }

    @Override
    public T conjunction() {
        return conjunction;
    }

    @Override
    public T disjunction() {
        return disjunction;
    }

    @Override
    public T logicalNot() {
        return logicalNot;
    }

    @Override
    public T conditional() {
        return conditional;
    }

    @Override
    public T functionCall() {
        return functionCall;
    }
}