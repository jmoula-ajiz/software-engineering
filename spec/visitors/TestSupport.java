package spec.visitors;

import static java.util.List.of;

import java.util.ArrayList;
import java.util.List;

import ds.Dict;
import lib.dict.ClassNamesDict;
import lib.expression.*;
import port.IHandlerFactory;
import port.IExpressionDict;
import port.IExpressionFactory;

class TestSupport<E> {
    final IHandlerFactory<E> v;
    final IExpressionFactory<E> factory;
    final Dict<String> values = new ClassNamesDict();

    TestSupport(IHandlerFactory<E> v) {
        this.v = v;
        this.factory = v.expressionFactory();
    }

    E sampleTraversalExpression() {
        return factory.conditional(
            factory.conjunction(
                factory.lessThan(factory.variableReference("x"), factory.literal("10")),
                factory.logicalNot(factory.equality(factory.literal("1"), factory.literal("0")))
            ),
            factory.addition(
                factory.subtraction(factory.literal("7"), factory.literal("2")),
                factory.multiplication(
                    factory.division(factory.literal("8"), factory.literal("2")),
                    factory.modulo(factory.literal("9"), factory.literal("4"))
                )
            ),
            factory.functionCall(
                factory.variableReference("f"),
                of(
                    factory.exponentiation(factory.literal("2"), factory.literal("3")),
                    factory.inequality(factory.literal("5"), factory.literal("6")),
                    factory.greaterThan(factory.literal("7"), factory.literal("1")),
                    factory.lessThanOrEqual(factory.literal("2"), factory.literal("2")),
                    factory.greaterThanOrEqual(factory.literal("3"), factory.literal("3")),
                    factory.disjunction(factory.literal("0"), factory.literal("1")),
                    factory.negation(factory.literal("4"))
                )
            )
        );
    }

    List<E> sampleNonVariableExpressions() {
        var expressions = new ArrayList<E>();
        expressions.add(factory.addition(factory.literal("1"), factory.literal("2")));
        expressions.add(factory.subtraction(factory.literal("3"), factory.literal("1")));
        expressions.add(factory.multiplication(factory.literal("2"), factory.literal("3")));
        expressions.add(factory.division(factory.literal("6"), factory.literal("2")));
        expressions.add(factory.negation(factory.literal("3")));
        expressions.add(factory.modulo(factory.literal("7"), factory.literal("3")));
        expressions.add(factory.exponentiation(factory.literal("2"), factory.literal("3")));
        expressions.add(factory.equality(factory.literal("1"), factory.literal("1")));
        expressions.add(factory.inequality(factory.literal("1"), factory.literal("2")));
        expressions.add(factory.lessThan(factory.literal("1"), factory.literal("2")));
        expressions.add(factory.greaterThan(factory.literal("2"), factory.literal("1")));
        expressions.add(factory.lessThanOrEqual(factory.literal("2"), factory.literal("2")));
        expressions.add(factory.greaterThanOrEqual(factory.literal("2"), factory.literal("2")));
        expressions.add(factory.conjunction(factory.literal("1"), factory.literal("1")));
        expressions.add(factory.disjunction(factory.literal("0"), factory.literal("1")));
        expressions.add(factory.logicalNot(factory.literal("0")));
        expressions.add(factory.conditional(factory.literal("1"), factory.literal("2"), factory.literal("3")));
        expressions.add(factory.functionCall(factory.variableReference("sum"), of(factory.literal("1"), factory.literal("2"))));
        return expressions;
    }

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
            "Negation",
            "Literal"
        );
    }

    List<Integer> expectedTraversalHistogramCounts() {
        return of(22, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
    }

    List<Integer> histogramCounts(IExpressionDict<Integer> histogram) {
        return of(
            histogram.literal(),
            histogram.variableReference(),
            histogram.addition(),
            histogram.subtraction(),
            histogram.multiplication(),
            histogram.division(),
            histogram.negation(),
            histogram.modulo(),
            histogram.exponentiation(),
            histogram.equality(),
            histogram.inequality(),
            histogram.lessThan(),
            histogram.greaterThan(),
            histogram.lessThanOrEqual(),
            histogram.greaterThanOrEqual(),
            histogram.conjunction(),
            histogram.disjunction(),
            histogram.logicalNot(),
            histogram.conditional(),
            histogram.functionCall()
        );
    }

    String expectedRenamedTraversalRender() {
        return "y < 10 && !(1 == 0) ? 7 - 2 + 8 / 2 * (9 % 4) : f(pow(2, 3), 5 != 6, 7 > 1, 2 <= 2, 3 >= 3, 0 || 1, -4)";
    }
}