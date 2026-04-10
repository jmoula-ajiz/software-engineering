package spec.visitors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ds.Dict;
import ds.Dict2;
import lib.expression.ExpressionV1;
import lib.expression.ExpressionV2;
import port.IExpressionFactory2;
import testsupport.HandlerTestFixtures;

class HandlerDictReaderTest extends TestBase<ExpressionV1> {

    HandlerDictReaderTest() {
        super(new TestSupport<>(HandlerTestFixtures.v1Handler()));
    }

    @Test
    void v1HandlerReadsThroughIExpressionDict() {
        var values = new Dict<>(
                "literal",
                "variable",
                "addition",
                "subtraction",
                "multiplication",
                "division",
                "negation",
                "modulo",
                "exponentiation",
                "equality",
                "inequality",
                "lessThan",
                "greaterThan",
                "lessThanOrEqual",
                "greaterThanOrEqual",
                "conjunction",
                "disjunction",
                "logicalNot",
                "conditional",
                "functionCall"
            );

        assertEquals("addition", HandlerTestFixtures.v1Handler().dictReader(values).apply(
            factory.addition(factory.literal("1"), factory.literal("2"))
        ));
    }
}

class HandlerDictReaderV2Test extends TestBase<ExpressionV2> {
    private final IExpressionFactory2<ExpressionV2> factory2 = HandlerTestFixtures.v2Handler().expressionFactory();

    HandlerDictReaderV2Test() {
        super(new TestSupport2<>(HandlerTestFixtures.v2Handler()));
    }

    @Test
    void v2HandlerReadsThroughIExpressionDict2() {
        var values = new Dict2<>(
                "literal",
                "variable",
                "addition",
                "subtraction",
                "multiplication",
                "division",
                "negation",
                "modulo",
                "exponentiation",
                "equality",
                "inequality",
                "lessThan",
                "greaterThan",
                "lessThanOrEqual",
                "greaterThanOrEqual",
                "conjunction",
                "disjunction",
                "logicalNot",
                "conditional",
                "functionCall",
                "lambdaExpression"
            );

        var handler = HandlerTestFixtures.v2Handler();
        var expr = factory2.lambdaExpression("x", factory2.variableReference("x"));

        assertEquals(handler.dictReader(handler.collectClassNamesDict()).apply(expr),
                handler.expressionClassNameExtractor().apply(expr));

        assertEquals("lambdaExpression", handler.dictReader(values).apply(expr));
    }
}