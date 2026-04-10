package spec.visitors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ds.Dict;
import lib.expression.ExpressionV1;
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
