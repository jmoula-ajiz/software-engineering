package ds;

import port.IExpressionDict;
import port.IExpressionDict2;

public class Dict2<T> extends Dict<T> implements IExpressionDict2<T> {
    public T lambdaExpression;

    public Dict2(
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
            T functionCall,
            T lambdaExpression) {
        super(
                literal,
                variableReference,
                addition,
                subtraction,
                multiplication,
                division,
                negation,
                modulo,
                exponentiation,
                equality,
                inequality,
                lessThan,
                greaterThan,
                lessThanOrEqual,
                greaterThanOrEqual,
                conjunction,
                disjunction,
                logicalNot,
                conditional,
                functionCall);
        this.lambdaExpression = lambdaExpression;
    }

    public Dict2(T value) {
        super(value);
        this.lambdaExpression = value;
    }

    public Dict2(IExpressionDict<T> dict, T value) {
        super(dict);
        this.lambdaExpression = value;
    }

    @Override
    public T lambdaExpression() {
        return lambdaExpression;
    }
}