package spec.visitors;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lib.expression.*;
import testsupport.HandlerTestFixtures;

abstract class ExpressionToLispLikeSyntaxTestBase<E> extends TestBase<E> {
    ExpressionToLispLikeSyntaxTestBase(TestSupport<E> testSupport) {
        super(testSupport);
    }

    @Test
    void lispLikeSyntaxUsesPrefixForms() {
        assertEquals(
            "(+ 1 (* 2 3))",
            renderLispLike(factory.addition(factory.literal("1"), factory.multiplication(factory.literal("2"), factory.literal("3"))))
        );
        assertEquals(
            "(if (<= x 10) (f 1 y) (or (not ready) (pow 2 3)))",
            renderLispLike(factory.conditional(
                factory.lessThanOrEqual(factory.variableReference("x"), factory.literal("10")),
                factory.functionCall(factory.variableReference("f"), java.util.List.of(factory.literal("1"), factory.variableReference("y"))),
                factory.disjunction(factory.logicalNot(factory.variableReference("ready")), factory.exponentiation(factory.literal("2"), factory.literal("3")))
            ))
        );
    }

    @Test
    void lispLikeSyntaxPrintsEveryOperatorForm() {
        assertEquals("(- 3 1)", renderLispLike(factory.subtraction(factory.literal("3"), factory.literal("1"))));
        assertEquals("(/ 6 2)", renderLispLike(factory.division(factory.literal("6"), factory.literal("2"))));
        assertEquals("(neg 3)", renderLispLike(factory.negation(factory.literal("3"))));
        assertEquals("(mod 7 3)", renderLispLike(factory.modulo(factory.literal("7"), factory.literal("3"))));
        assertEquals("(= 1 1)", renderLispLike(factory.equality(factory.literal("1"), factory.literal("1"))));
        assertEquals("(!= 1 2)", renderLispLike(factory.inequality(factory.literal("1"), factory.literal("2"))));
        assertEquals("(< 1 2)", renderLispLike(factory.lessThan(factory.literal("1"), factory.literal("2"))));
        assertEquals("(> 2 1)", renderLispLike(factory.greaterThan(factory.literal("2"), factory.literal("1"))));
        assertEquals("(>= 2 2)", renderLispLike(factory.greaterThanOrEqual(factory.literal("2"), factory.literal("2"))));
        assertEquals("(and 1 1)", renderLispLike(factory.conjunction(factory.literal("1"), factory.literal("1"))));
    }

    @Test
    void lispLikeSyntaxPrintsLeavesAndCallShapes() {
        assertEquals("7", renderLispLike(factory.literal("7")));
        assertEquals("name", renderLispLike(factory.variableReference("name")));
        assertEquals("(sum 1 2)", renderLispLike(factory.functionCall(factory.variableReference("sum"), of(factory.literal("1"), factory.literal("2")))));
        assertEquals("(noop)", renderLispLike(factory.functionCall(factory.variableReference("noop"), of())));
    }
}

class ExpressionToLispLikeSyntaxTest extends ExpressionToLispLikeSyntaxTestBase<ExpressionV1> {
    ExpressionToLispLikeSyntaxTest() {
        super(new TestSupport<>(HandlerTestFixtures.v1Handler()));
    }
}

class ExpressionToLispLikeSyntaxV2Test extends ExpressionToLispLikeSyntaxTestBase<ExpressionV2> {
    ExpressionToLispLikeSyntaxV2Test() {
        super(new TestSupport<>(HandlerTestFixtures.v2Handler()));
    }
}

class ExpressionToLispLikeSyntaxV2Support2Test extends ExpressionToLispLikeSyntaxTestBase<ExpressionV2> {
    ExpressionToLispLikeSyntaxV2Support2Test() {
        super(new TestSupport2<>(HandlerTestFixtures.v2Handler()));
    }
}