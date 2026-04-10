package spec.visitors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import lib.expression.Expression;
import lib.expression.ExpressionV1;
import lib.expression.ExpressionV2;
import testsupport.HandlerTestFixtures;

abstract class IntegerEvaluationVisitorTestBase<E> extends TestBase<E> {
    IntegerEvaluationVisitorTestBase(TestSupport<E> testSupport) {
        super(testSupport);
    }

    @Test
    void evaluatesConstantArithmetic() {
        var evaluator = testSupport.v.integerEvaluator(Map.of(), Map.of());

        assertEquals(
            4,
            evaluator.apply(factory.addition(factory.literal("3"), factory.literal("1")))
        );
    }

    @Test
    void failsQuickOnUnknownVariable() {
        var evaluator = testSupport.v.integerEvaluator(Map.of(), Map.of());

        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> evaluator.apply(factory.addition(factory.literal("3"), factory.variableReference("x")))
        );

        assertEquals("Unknown variable: x", exception.getMessage());
    }

    @Test
    void canUseProvidedVariablesAndFunctions() {
        var evaluator = testSupport.v.integerEvaluator(
            Map.of("x", 3),
            Map.of("inc", values -> values.get(0) + 1)
        );

        assertEquals(3, evaluator.apply(factory.variableReference("x")));
        assertEquals(
            4,
            evaluator.apply(factory.functionCall(factory.variableReference("inc"), List.of(factory.literal("3"))))
        );
    }

    @Test
    void evaluatesEveryOperator() {
        var evaluator = testSupport.v.integerEvaluator(
            Map.of("x", 8, "threshold", 4),
            Map.of("sum", values -> values.stream().mapToInt(Integer::intValue).sum())
        );

        assertEquals(10, evaluator.apply(factory.addition(factory.literal("8"), factory.literal("2"))));
        assertEquals(6, evaluator.apply(factory.subtraction(factory.literal("8"), factory.literal("2"))));
        assertEquals(16, evaluator.apply(factory.multiplication(factory.literal("8"), factory.literal("2"))));
        assertEquals(4, evaluator.apply(factory.division(factory.literal("8"), factory.literal("2"))));
        assertEquals(-8, evaluator.apply(factory.negation(factory.literal("8"))));
        assertEquals(0, evaluator.apply(factory.modulo(factory.literal("8"), factory.literal("2"))));
        assertEquals(64, evaluator.apply(factory.exponentiation(factory.literal("8"), factory.literal("2"))));
        assertEquals(1, evaluator.apply(factory.equality(factory.literal("8"), factory.literal("8"))));
        assertEquals(1, evaluator.apply(factory.inequality(factory.literal("8"), factory.literal("2"))));
        assertEquals(1, evaluator.apply(factory.lessThan(factory.literal("2"), factory.literal("8"))));
        assertEquals(1, evaluator.apply(factory.greaterThan(factory.literal("8"), factory.literal("2"))));
        assertEquals(1, evaluator.apply(factory.lessThanOrEqual(factory.literal("2"), factory.literal("2"))));
        assertEquals(1, evaluator.apply(factory.greaterThanOrEqual(factory.literal("8"), factory.literal("8"))));
        assertEquals(1, evaluator.apply(factory.conjunction(factory.literal("1"), factory.literal("2"))));
        assertEquals(1, evaluator.apply(factory.disjunction(factory.literal("0"), factory.literal("2"))));
        assertEquals(1, evaluator.apply(factory.logicalNot(factory.literal("0"))));
        assertEquals(22, evaluator.apply(factory.conditional(factory.literal("0"), factory.literal("11"), factory.literal("22"))));
        assertEquals(12, evaluator.apply(factory.functionCall(factory.variableReference("sum"), List.of(factory.literal("8"), factory.literal("2"), factory.literal("2")))));
        assertEquals(8, evaluator.apply(factory.variableReference("x")));
    }

    @Test
    void evaluatesVariablesFunctionsAndConditionalsTogether() {
        var evaluator = testSupport.v.integerEvaluator(
            Map.of("threshold", 4, "fallback", 9),
            Map.of(
                "max", values -> Math.max(values.get(0), values.get(1)),
                "fallback", values -> values.get(0) + 100
            )
        );

        var expression = factory.conditional(
            factory.variableReference("threshold"),
            factory.addition(
                factory.functionCall(factory.variableReference("max"), List.of(factory.literal("3"), factory.variableReference("threshold"))),
                factory.multiplication(factory.literal("2"), factory.literal("5"))
            ),
            factory.functionCall(factory.variableReference("fallback"), List.of(factory.literal("0")))
        );

        assertEquals(14, evaluator.apply(expression));
    }

    @Test
    void evaluatesFalseComparisonAndLogicalBranches() {
        var evaluator = testSupport.v.integerEvaluator(Map.of("x", 0), Map.of());

        assertEquals(0, evaluator.apply(factory.equality(factory.literal("8"), factory.literal("2"))));
        assertEquals(0, evaluator.apply(factory.inequality(factory.literal("8"), factory.literal("8"))));
        assertEquals(0, evaluator.apply(factory.lessThan(factory.literal("8"), factory.literal("2"))));
        assertEquals(0, evaluator.apply(factory.greaterThan(factory.literal("2"), factory.literal("8"))));
        assertEquals(0, evaluator.apply(factory.lessThanOrEqual(factory.literal("8"), factory.literal("2"))));
        assertEquals(0, evaluator.apply(factory.greaterThanOrEqual(factory.literal("2"), factory.literal("8"))));
        assertEquals(0, evaluator.apply(factory.conjunction(factory.literal("0"), factory.literal("2"))));
        assertEquals(0, evaluator.apply(factory.conjunction(factory.literal("1"), factory.literal("0"))));
        assertEquals(0, evaluator.apply(factory.disjunction(factory.literal("0"), factory.literal("0"))));
        assertEquals(1, evaluator.apply(factory.disjunction(factory.literal("1"), factory.literal("0"))));
        assertEquals(0, evaluator.apply(factory.logicalNot(factory.literal("1"))));
        assertEquals(11, evaluator.apply(factory.conditional(factory.literal("1"), factory.literal("11"), factory.literal("22"))));
        assertEquals(0, evaluator.apply(factory.variableReference("x")));
    }

    @Test
    void failsOnUnknownFunction() {
        var evaluator = testSupport.v.integerEvaluator(Map.of(), Map.of());

        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> evaluator.apply(factory.functionCall(factory.variableReference("missing"), List.of(factory.literal("1"))))
        );

        assertEquals("Unknown function: missing", exception.getMessage());
    }

    @Test
    void failsOnNonIntegerLiteral() {
        var evaluator = testSupport.v.integerEvaluator(Map.of(), Map.of());

        assertThrows(NumberFormatException.class, () -> evaluator.apply(factory.literal("nan")));
    }

    @Test
    void failsOnInvalidFunctionCallee() {
        var evaluator = testSupport.v.integerEvaluator(Map.of(), Map.of("f", values -> values.get(0)));

        var exception = assertThrows(
            IllegalArgumentException.class,
            () -> evaluator.apply(factory.functionCall(factory.addition(factory.literal("1"), factory.literal("2")), List.of(factory.literal("3"))))
        );

        assertEquals("Expected a variable reference", exception.getMessage());
    }
}

class IntegerEvaluationVisitorTest extends IntegerEvaluationVisitorTestBase<ExpressionV1> {
    IntegerEvaluationVisitorTest() {
        super(new TestSupport<>(HandlerTestFixtures.v1Handler()));
    }
}

class IntegerEvaluationVisitorV2Test extends IntegerEvaluationVisitorTestBase<ExpressionV2> {
    IntegerEvaluationVisitorV2Test() {
        super(new TestSupport<>(HandlerTestFixtures.v2Handler()));
    }
}

class IntegerEvaluationVisitorV2Support2Test extends IntegerEvaluationVisitorTestBase<ExpressionV2> {
    IntegerEvaluationVisitorV2Support2Test() {
        super(new TestSupport2<>(HandlerTestFixtures.v2Handler()));
    }
}