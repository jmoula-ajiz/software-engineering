package spec.visitors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import lib.expression.ExpressionV1;
import testsupport.HandlerTestFixtures;

abstract class HandlerPredicatesAndBindingTestBase<E> extends TestBase<E> {
    HandlerPredicatesAndBindingTestBase(TestSupport<E> testSupport) {
        super(testSupport);
    }

    @Test
    void literalAndVariableCheckersRecognizeNodeKinds() {
        assertTrue(testSupport.v.literalChecker().apply(factory.literal("8")));
        assertFalse(testSupport.v.literalChecker().apply(factory.addition(factory.literal("1"), factory.literal("2"))));
        assertTrue(testSupport.v.variableChecker().apply(factory.variableReference("x")));
        assertFalse(testSupport.v.variableChecker().apply(factory.negation(factory.literal("3"))));
    }

    @Test
    void bindingPowerHandlerUsesExpectedPriorities() {
        var bindingPower = testSupport.v.createBindingPowerHandler();
        var addition = bindingPower.apply(factory.addition(factory.literal("1"), factory.literal("2")));
        var exponentiation = bindingPower.apply(factory.exponentiation(factory.literal("2"), factory.literal("3")));
        var conditional = bindingPower.apply(testSupport.sampleTraversalExpression());

        assertEquals(10, addition.priority());
        assertFalse(addition.isRightAssociative());
        assertEquals(40, exponentiation.priority());
        assertTrue(exponentiation.isRightAssociative());
        assertEquals(1, conditional.priority());
    }
}

class HandlerPredicatesAndBindingTest extends HandlerPredicatesAndBindingTestBase<ExpressionV1> {
    HandlerPredicatesAndBindingTest() {
        super(new TestSupport<>(HandlerTestFixtures.v1Handler()));
    }
}