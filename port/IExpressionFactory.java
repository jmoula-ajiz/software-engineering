package port;

import java.util.List;

public interface IExpressionFactory<E> {
    E literal(String value);

    E variableReference(String name);

    E addition(E left, E right);

    E subtraction(E left, E right);

    E multiplication(E left, E right);

    E division(E dividend, E divisor);

    E negation(E operand);

    E modulo(E left, E right);

    E exponentiation(E base, E exponent);

    E equality(E left, E right);

    E inequality(E left, E right);

    E lessThan(E left, E right);

    E greaterThan(E left, E right);

    E lessThanOrEqual(E left, E right);

    E greaterThanOrEqual(E left, E right);

    E conjunction(E left, E right);

    E disjunction(E left, E right);

    E logicalNot(E operand);

    E conditional(E condition, E whenTrue, E whenFalse);

    E functionCall(E callee, List<E> arguments);
}