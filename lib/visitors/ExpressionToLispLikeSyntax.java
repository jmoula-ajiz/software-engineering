package lib.visitors;

import java.util.stream.Collectors;

import lib.expression.*;
import port.IHandlerFactory;

public class ExpressionToLispLikeSyntax<E> implements ExpressionVisitor<String, E> {
    private final IHandlerFactory<E> handlers;

    public ExpressionToLispLikeSyntax(IHandlerFactory<E> handlers) {
        this.handlers = handlers;
    }

    private String apply(E expression) {
        return handlers.lispLikeSyntaxPrinter().apply(expression);
    }

    private String unary(String operator, E operand) {
        return "(" + operator + " " + apply(operand) + ")";
    }

    private String binary(String operator, E left, E right) {
        return "(" + operator + " " + apply(left) + " " + apply(right) + ")";
    }

    private String ternary(String operator, E first, E second, E third) {
        return "(" + operator + " " + apply(first) + " " + apply(second) + " " + apply(third) + ")";
    }

    public String visit(Literal<E> expression) {
        return expression.value;
    }

    public String visit(VariableReference<E> expression) {
        return expression.name;
    }

    public String visit(Addition<E> expression) {
        return binary("+", expression.left, expression.right);
    }

    public String visit(Subtraction<E> expression) {
        return binary("-", expression.left, expression.right);
    }

    public String visit(Multiplication<E> expression) {
        return binary("*", expression.left, expression.right);
    }

    public String visit(Division<E> expression) {
        return binary("/", expression.dividend, expression.divisor);
    }

    public String visit(Negation<E> expression) {
        return unary("neg", expression.operand);
    }

    public String visit(Modulo<E> expression) {
        return binary("mod", expression.left, expression.right);
    }

    public String visit(Exponentiation<E> expression) {
        return binary("pow", expression.base, expression.exponent);
    }

    public String visit(Equality<E> expression) {
        return binary("=", expression.left, expression.right);
    }

    public String visit(Inequality<E> expression) {
        return binary("!=", expression.left, expression.right);
    }

    public String visit(LessThan<E> expression) {
        return binary("<", expression.left, expression.right);
    }

    public String visit(GreaterThan<E> expression) {
        return binary(">", expression.left, expression.right);
    }

    public String visit(LessThanOrEqual<E> expression) {
        return binary("<=", expression.left, expression.right);
    }

    public String visit(GreaterThanOrEqual<E> expression) {
        return binary(">=", expression.left, expression.right);
    }

    public String visit(Conjunction<E> expression) {
        return binary("and", expression.left, expression.right);
    }

    public String visit(Disjunction<E> expression) {
        return binary("or", expression.left, expression.right);
    }

    public String visit(LogicalNot<E> expression) {
        return unary("not", expression.operand);
    }

    public String visit(Conditional<E> expression) {
        return ternary("if", expression.condition, expression.whenTrue, expression.whenFalse);
    }

    public String visit(FunctionCall<E> expression) {
        return "(" + apply(expression.callee)
            + (expression.arguments.isEmpty() ? "" : " " + expression.arguments.stream().map(this::apply).collect(Collectors.joining(" ")))
            + ")";
    }
}