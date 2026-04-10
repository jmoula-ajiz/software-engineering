package lib.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import lib.expression.*;

public class ExpressionChildren<E> implements ExpressionVisitor<List<E>, E> {

    public List<E> visit(Literal<E> expression) { return List.of(); }
    public List<E> visit(VariableReference<E> expression) { return List.of(); }
    public List<E> visit(Addition<E> expression) { return List.of(expression.left, expression.right); }
    public List<E> visit(Subtraction<E> expression) { return List.of(expression.left, expression.right); }
    public List<E> visit(Multiplication<E> expression) { return List.of(expression.left, expression.right); }
    public List<E> visit(Division<E> expression) { return List.of(expression.dividend, expression.divisor); }
    public List<E> visit(Negation<E> expression) { return List.of(expression.operand); }
    public List<E> visit(Modulo<E> expression) { return List.of(expression.left, expression.right); }
    public List<E> visit(Exponentiation<E> expression) { return List.of(expression.base, expression.exponent); }
    public List<E> visit(Equality<E> expression) { return List.of(expression.left, expression.right); }
    public List<E> visit(Inequality<E> expression) { return List.of(expression.left, expression.right); }
    public List<E> visit(LessThan<E> expression) { return List.of(expression.left, expression.right); }
    public List<E> visit(GreaterThan<E> expression) { return List.of(expression.left, expression.right); }
    public List<E> visit(LessThanOrEqual<E> expression) { return List.of(expression.left, expression.right); }
    public List<E> visit(GreaterThanOrEqual<E> expression) { return List.of(expression.left, expression.right); }
    public List<E> visit(Conjunction<E> expression) { return List.of(expression.left, expression.right); }
    public List<E> visit(Disjunction<E> expression) { return List.of(expression.left, expression.right); }
    public List<E> visit(LogicalNot<E> expression) { return List.of(expression.operand); }
    public List<E> visit(Conditional<E> expression) { return List.of(expression.condition, expression.whenTrue, expression.whenFalse); }

    public List<E> visit(FunctionCall<E> expression) {
        var children = new ArrayList<E>(expression.arguments.size() + 1);
        children.add(expression.callee);
        children.addAll(expression.arguments);
        return children;
    }
}