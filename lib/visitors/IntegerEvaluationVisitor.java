package lib.visitors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import lib.expression.*;
import lib.utils.Either;
import lib.utils.EitherVisitor;

public class IntegerEvaluationVisitor<E> implements ExpressionVisitor<Integer, E> {
    private final Map<String, Integer> variables;
    private final Map<String, Function<List<Integer>, Integer>> functions;
    private final Function<E, Either<VariableReference<E>, E>> isVariable;
    private final BiFunction<E, IntegerEvaluationVisitor<E>, Integer> acceptVisitor;

    public IntegerEvaluationVisitor(Map<String, Integer> variables, Map<String, Function<List<Integer>, Integer>> functions,
            Function<E, Either<VariableReference<E>, E>> isVariable, BiFunction<E, IntegerEvaluationVisitor<E>, Integer> acceptVisitor) {
        this.variables = variables;
        this.functions = functions;
        this.isVariable = isVariable;
        this.acceptVisitor = acceptVisitor;
    }

    private Integer evaluate(E expression) {
        return acceptVisitor.apply(expression, this);
    }

    private boolean truthy(int value) {
        return value != 0;
    }

    public Integer visit(Literal<E> expression) {
        return expression.asInt();
    }

    public Integer visit(VariableReference<E> expression) {
        if (!variables.containsKey(expression.name)) {
            throw new IllegalArgumentException("Unknown variable: " + expression.name);
        }
        return variables.get(expression.name);
    }

    public Integer visit(Addition<E> expression) {
        return evaluate(expression.left) + evaluate(expression.right);
    }

    public Integer visit(Subtraction<E> expression) {
        return evaluate(expression.left) - evaluate(expression.right);
    }

    public Integer visit(Multiplication<E> expression) {
        return evaluate(expression.left) * evaluate(expression.right);
    }

    public Integer visit(Division<E> expression) {
        return evaluate(expression.dividend) / evaluate(expression.divisor);
    }

    public Integer visit(Negation<E> expression) {
        return -evaluate(expression.operand);
    }

    public Integer visit(Modulo<E> expression) {
        return evaluate(expression.left) % evaluate(expression.right);
    }

    public Integer visit(Exponentiation<E> expression) {
        return (int) Math.pow(evaluate(expression.base), evaluate(expression.exponent));
    }

    public Integer visit(Equality<E> expression) {
        return evaluate(expression.left) == evaluate(expression.right) ? 1 : 0;
    }

    public Integer visit(Inequality<E> expression) {
        return evaluate(expression.left) != evaluate(expression.right) ? 1 : 0;
    }

    public Integer visit(LessThan<E> expression) {
        return evaluate(expression.left) < evaluate(expression.right) ? 1 : 0;
    }

    public Integer visit(GreaterThan<E> expression) {
        return evaluate(expression.left) > evaluate(expression.right) ? 1 : 0;
    }

    public Integer visit(LessThanOrEqual<E> expression) {
        return evaluate(expression.left) <= evaluate(expression.right) ? 1 : 0;
    }

    public Integer visit(GreaterThanOrEqual<E> expression) {
        return evaluate(expression.left) >= evaluate(expression.right) ? 1 : 0;
    }

    public Integer visit(Conjunction<E> expression) {
        return truthy(evaluate(expression.left)) && truthy(evaluate(expression.right)) ? 1 : 0;
    }

    public Integer visit(Disjunction<E> expression) {
        return truthy(evaluate(expression.left)) || truthy(evaluate(expression.right)) ? 1 : 0;
    }

    public Integer visit(LogicalNot<E> expression) {
        return truthy(evaluate(expression.operand)) ? 0 : 1;
    }

    public Integer visit(Conditional<E> expression) {
        return truthy(evaluate(expression.condition)) ? evaluate(expression.whenTrue) : evaluate(expression.whenFalse);
    }

    public Integer visit(FunctionCall<E> expression) {
        var calleeName = isVariable.apply(expression.callee).accept(new EitherVisitor<VariableReference<E>, E, String>() {
            public String left(VariableReference<E> left) {
                return left.name;
            }

            public String right(E e) {
                throw new IllegalArgumentException("Expected a variable reference");
            }
        });
        if (!functions.containsKey(calleeName)) {
            throw new IllegalArgumentException("Unknown function: " + calleeName);
        }

        var arguments = new ArrayList<Integer>();
        for (var argument : expression.arguments) {
            arguments.add(evaluate(argument));
        }
        return functions.get(calleeName).apply(arguments);
    }
}