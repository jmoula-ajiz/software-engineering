package lib.visitors;

import java.util.function.Function;
import java.util.stream.Collectors;

import ds.BindingPower;
import lib.expression.*;
import port.IHandlerFactory;

public class ExpressionToCLikeSyntax<E> implements ExpressionVisitor<String, E> {
    private final Function<E, BindingPower> bindingPowers;
    private final Function<E, String> cLikeSyntaxPrinter;
    private final E e;

    public ExpressionToCLikeSyntax(IHandlerFactory<E> handlers, Function<E, String> cLikeSyntaxPrinter, E e) {
        this.bindingPowers = handlers.createBindingPowerHandler();
        this.cLikeSyntaxPrinter = cLikeSyntaxPrinter;
        this.e = e;
    }

    private String apply(E expression) {
        return cLikeSyntaxPrinter.apply(expression);
    }

    private String renderChild(E child, BindingPower parentBinding, boolean isRightChild) {
        var rendered = apply(child);
        var childBinding = bindingPower(child);
        if (childBinding.priority() < parentBinding.priority()) {
            return "(" + rendered + ")";
        }
        if (childBinding.priority() == parentBinding.priority()) {
            var needsParentheses = isRightChild ? !parentBinding.isRightAssociative() : parentBinding.isRightAssociative();
            if (needsParentheses) {
                return "(" + rendered + ")";
            }
        }
        return rendered;
    }

    private String infix(E left, String operator, E right, BindingPower parentBinding) {
        return renderChild(left, parentBinding, false)
            + " " + operator + " "
            + renderChild(right, parentBinding, true);
    }

    private String prefix(String operator, E operand, BindingPower parentBinding) {
        return operator + renderChild(operand, parentBinding, true);
    }

    private String call(FunctionCall<E> expression) {
        return renderChild(expression.callee, bindingPower(e), false)
            + "(" + expression.arguments.stream().map(this::apply).collect(Collectors.joining(", ")) + ")";
    }

    private BindingPower bindingPower(E expression) {
        return bindingPowers.apply(expression);
    }

    public String visit(Literal<E> expression) {
        return expression.value;
    }

    public String visit(VariableReference<E> expression) {
        return expression.name;
    }

    public String visit(Addition<E> expression) {
        return infix(expression.left, "+", expression.right, bindingPower(e));
    }

    public String visit(Subtraction<E> expression) {
        return infix(expression.left, "-", expression.right, bindingPower(e));
    }

    public String visit(Multiplication<E> expression) {
        return infix(expression.left, "*", expression.right, bindingPower(e));
    }

    public String visit(Division<E> expression) {
        return infix(expression.dividend, "/", expression.divisor, bindingPower(e));
    }

    public String visit(Negation<E> expression) {
        return prefix("-", expression.operand, bindingPower(e));
    }

    public String visit(Modulo<E> expression) {
        return infix(expression.left, "%", expression.right, bindingPower(e));
    }

    public String visit(Exponentiation<E> expression) {
        return "pow(" + apply(expression.base) + ", " + apply(expression.exponent) + ")";
    }

    public String visit(Equality<E> expression) {
        return infix(expression.left, "==", expression.right, bindingPower(e));
    }

    public String visit(Inequality<E> expression) {
        return infix(expression.left, "!=", expression.right, bindingPower(e));
    }

    public String visit(LessThan<E> expression) {
        return infix(expression.left, "<", expression.right, bindingPower(e));
    }

    public String visit(GreaterThan<E> expression) {
        return infix(expression.left, ">", expression.right, bindingPower(e));
    }

    public String visit(LessThanOrEqual<E> expression) {
        return infix(expression.left, "<=", expression.right, bindingPower(e));
    }

    public String visit(GreaterThanOrEqual<E> expression) {
        return infix(expression.left, ">=", expression.right, bindingPower(e));
    }

    public String visit(Conjunction<E> expression) {
        return infix(expression.left, "&&", expression.right, bindingPower(e));
    }

    public String visit(Disjunction<E> expression) {
        return infix(expression.left, "||", expression.right, bindingPower(e));
    }

    public String visit(LogicalNot<E> expression) {
        return prefix("!", expression.operand, bindingPower(e));
    }

    public String visit(Conditional<E> expression) {
        var binding = bindingPower(e);
        return renderChild(expression.condition, binding, false)
            + " ? " + apply(expression.whenTrue)
            + " : " + renderChild(expression.whenFalse, binding, true);
    }

    public String visit(FunctionCall<E> expression) {
        return call(expression);
    }
}