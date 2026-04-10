package spec.visitors;

import static java.util.List.of;

import java.util.List;

import port.IExpressionFactory2;
import port.IHandlerFactory2;

class TestSupport2<E> extends TestSupport<E> {
    private final IExpressionFactory2<E> factory2;

    TestSupport2(IHandlerFactory2<E> v) {
        super(v);
        this.factory2 = v.expressionFactory();
    }

    @Override
    E sampleTraversalExpression() {
        return factory2.conditional(
            factory2.conjunction(
                factory2.lessThan(factory2.variableReference("x"), factory2.literal("10")),
                factory2.logicalNot(factory2.equality(factory2.literal("1"), factory2.literal("0")))
            ),
            factory2.addition(
                factory2.subtraction(factory2.literal("7"), factory2.literal("2")),
                factory2.multiplication(
                    factory2.division(factory2.literal("8"), factory2.literal("2")),
                    factory2.modulo(factory2.literal("9"), factory2.literal("4"))
                )
            ),
            factory2.functionCall(
                factory2.variableReference("f"),
                of(
                    factory2.exponentiation(factory2.literal("2"), factory2.literal("3")),
                    factory2.inequality(factory2.literal("5"), factory2.literal("6")),
                    factory2.greaterThan(factory2.literal("7"), factory2.literal("1")),
                    factory2.lessThanOrEqual(factory2.literal("2"), factory2.literal("2")),
                    factory2.greaterThanOrEqual(factory2.literal("3"), factory2.literal("3")),
                    factory2.disjunction(factory2.literal("0"), factory2.literal("1")),
                    factory2.lambdaExpression(
                        "n",
                        factory2.addition(factory2.variableReference("n"), factory2.literal("4"))
                    )
                )
            )
        );
    }

    @Override
    List<E> sampleNonVariableExpressions() {
        var expressions = super.sampleNonVariableExpressions();
        expressions.add(factory2.lambdaExpression("n", factory2.addition(factory2.variableReference("n"), factory2.literal("3"))));
        return expressions;
    }

    @Override
    List<Integer> expectedTraversalHistogramCounts() {
        return of(22, 3, 2, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
    }

    @Override
    List<String> expectedTraversalClassNames() {
        return of(
            "Conditional",
            "Conjunction",
            "LessThan",
            "VariableReference",
            "Literal",
            "LogicalNot",
            "Equality",
            "Literal",
            "Literal",
            "Addition",
            "Subtraction",
            "Literal",
            "Literal",
            "Multiplication",
            "Division",
            "Literal",
            "Literal",
            "Modulo",
            "Literal",
            "Literal",
            "FunctionCall",
            "VariableReference",
            "Exponentiation",
            "Literal",
            "Literal",
            "Inequality",
            "Literal",
            "Literal",
            "GreaterThan",
            "Literal",
            "Literal",
            "LessThanOrEqual",
            "Literal",
            "Literal",
            "GreaterThanOrEqual",
            "Literal",
            "Literal",
            "Disjunction",
            "Literal",
            "Literal",
            "LambdaExpression",
            "Addition",
            "VariableReference",
            "Literal"
        );
    }

    @Override
    String expectedRenamedTraversalRender() {
        return "y < 10 && !(1 == 0) ? 7 - 2 + 8 / 2 * (9 % 4) : f(pow(2, 3), 5 != 6, 7 > 1, 2 <= 2, 3 >= 3, 0 || 1, n => n + 4)";
    }
}