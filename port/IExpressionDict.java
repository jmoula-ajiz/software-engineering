package port;

public interface IExpressionDict<T> {
    T literal();
    T variableReference();
    T addition();
    T subtraction();
    T multiplication();
    T division();
    T negation();
    T modulo();
    T exponentiation();
    T equality();
    T inequality();
    T lessThan();
    T greaterThan();
    T lessThanOrEqual();
    T greaterThanOrEqual();
    T conjunction();
    T disjunction();
    T logicalNot();
    T conditional();
    T functionCall();
}
