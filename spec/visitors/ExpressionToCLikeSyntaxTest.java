package spec.visitors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lib.expression.*;
import port.IHandlerFactory1;
import testsupport.HandlerTestFixtures;

class ExpressionToCLikeSyntaxTest extends TestBase<ExpressionV1> {

    private final IHandlerFactory1<ExpressionV1> handler = HandlerTestFixtures.v1Handler();
    
    ExpressionToCLikeSyntaxTest() {
        super(new TestSupport<>(HandlerTestFixtures.v1Handler()));
    }

    @Test
    void cLikeSyntaxUsesPriorityToControlParentheses() {
        assertEquals(
            "1 + 2 * 3",
            renderCLike(factory.addition(factory.literal("1"), factory.multiplication(factory.literal("2"), factory.literal("3"))))
        );
        assertEquals(
            "(1 + 2) * 3",
            renderCLike(factory.multiplication(factory.addition(factory.literal("1"), factory.literal("2")), factory.literal("3")))
        );
        assertEquals(
            "10 - (3 - 1)",
            renderCLike(factory.subtraction(factory.literal("10"), factory.subtraction(factory.literal("3"), factory.literal("1"))))
        );
    }

    @Test
    void cLikeSyntaxPrintsOperatorsAndCalls() {
        assertEquals(
            "x <= 10 ? f(1, y) : !ready || pow(2, 3)",
            renderCLike(factory.conditional(
                factory.lessThanOrEqual(factory.variableReference("x"), factory.literal("10")),
                factory.functionCall(factory.variableReference("f"), java.util.List.of(factory.literal("1"), factory.variableReference("y"))),
                factory.disjunction(factory.logicalNot(factory.variableReference("ready")), factory.exponentiation(factory.literal("2"), factory.literal("3")))
            ))
        );
    }

    protected final String renderCLike(ExpressionV1 expression) {
        return handler.cLikeSyntaxPrinter().apply(expression);
    }


    @Test
    void cLikeSyntaxUsesRightAssociativeBindingWhenNeeded() {
        assertEquals(
            "a ? b : c ? d : e",
            renderCLike(factory.conditional(
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
