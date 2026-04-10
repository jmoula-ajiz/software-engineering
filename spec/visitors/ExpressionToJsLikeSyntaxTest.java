package spec.visitors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lib.expression.*;
import testsupport.HandlerTestFixtures;

abstract class ExpressionToJsLikeSyntaxTestBase<E> extends TestBase<E> {
    ExpressionToJsLikeSyntaxTestBase(TestSupport<E> testSupport) {
        super(testSupport);
    }

    @Test
    void jsLikeSyntaxUsesPriorityToControlParentheses() {
        assertEquals(
            "1 + 2 * 3",
            renderJsLike(factory.addition(factory.literal("1"), factory.multiplication(factory.literal("2"), factory.literal("3"))))
        );
        assertEquals(
            "(1 + 2) * 3",
            renderJsLike(factory.multiplication(factory.addition(factory.literal("1"), factory.literal("2")), factory.literal("3")))
        );
        assertEquals(
            "10 - (3 - 1)",
            renderJsLike(factory.subtraction(factory.literal("10"), factory.subtraction(factory.literal("3"), factory.literal("1"))))
        );
    }

    @Test
    void jsLikeSyntaxPrintsOperatorsAndCalls() {
        assertEquals(
            "x <= 10 ? f(1, y) : !ready || pow(2, 3)",
            renderJsLike(factory.conditional(
                factory.lessThanOrEqual(factory.variableReference("x"), factory.literal("10")),
                factory.functionCall(factory.variableReference("f"), java.util.List.of(factory.literal("1"), factory.variableReference("y"))),
                factory.disjunction(factory.logicalNot(factory.variableReference("ready")), factory.exponentiation(factory.literal("2"), factory.literal("3")))
            ))
        );
    }

    @Test
    void jsLikeSyntaxUsesRightAssociativeBindingWhenNeeded() {
        assertEquals(
            "a ? b : c ? d : e",
            renderJsLike(factory.conditional(
                factory.variableReference("a"),
                factory.variableReference("b"),
                factory.conditional(
                    factory.variableReference("c"),
                    factory.variableReference("d"),
                    factory.variableReference("e")
                )
            ))
        );
    }
}

class ExpressionToJsLikeSyntaxTest extends ExpressionToJsLikeSyntaxTestBase<ExpressionV1> {
    ExpressionToJsLikeSyntaxTest() {
        super(new TestSupport<>(HandlerTestFixtures.v1Handler()));
    }
}