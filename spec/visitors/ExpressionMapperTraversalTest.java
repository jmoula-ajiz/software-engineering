package spec.visitors;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lib.expression.ExpressionV1;
import testsupport.HandlerTestFixtures;

abstract class ExpressionMapperTraversalTestBase<E> extends TestBase<E> {
    ExpressionMapperTraversalTestBase(TestSupport<E> testSupport) {
        super(testSupport);
    }

    @Test
    void mapperClonesEveryExpressionShape() {
        var mapper = testSupport.v.expressionMapper((expression, produce) -> produce.get());

        for (var expression : testSupport.sampleNonVariableExpressions()) {
            assertEquals(render(expression), render(mapper.apply(expression)));
            assertEquals(typeName(expression), typeName(mapper.apply(expression)));
        }

        var traversal = testSupport.sampleTraversalExpression();
        var traversalClone = mapper.apply(traversal);
        assertEquals(render(traversal), render(traversalClone));
        assertEquals(typeName(traversal), typeName(traversalClone));
    }

    @Test
    void constantFolderTraversesNestedExpressionArguments() {
        var folder = testSupport.v.constantFolder();
        var expression = factory.functionCall(
            factory.variableReference("sum"),
            of(
                factory.addition(factory.literal("1"), factory.literal("2")),
                factory.conditional(factory.literal("1"), factory.literal("4"), factory.literal("5")),
                factory.disjunction(factory.literal("0"), factory.literal("1")),
                factory.negation(factory.literal("3"))
            )
        );

        assertEquals("sum(3, 4, 1, -3)", render(folder.apply(expression)));
    }
}

class ExpressionMapperTraversalTest extends ExpressionMapperTraversalTestBase<ExpressionV1> {
    ExpressionMapperTraversalTest() {
        super(new TestSupport<>(HandlerTestFixtures.v1Handler()));
    }
}