package lib.utils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import lib.expression.FunctionCall;

public class LambdaCallFolder<E, L> {
    private final Function<E, String> variableName;
    private final Function<String, E> variableReference;
    private final Function<E, L> asLambda;
    private final Function<L, String> lambdaParameterName;
    private final Function<L, E> lambdaBody;
    private final BiFunction<String, E, E> lambdaExpression;
    private final Function<E, List<E>> expressionChildren;
    private final BiFunction<E, UnaryOperator<E>, E> mapChildren;
    private final Function<E, FunctionCall<E>> asFunctionCall;

    public LambdaCallFolder(
            Function<E, String> variableName,
            Function<String, E> variableReference,
            Function<E, L> asLambda,
            Function<L, String> lambdaParameterName,
            Function<L, E> lambdaBody,
            BiFunction<String, E, E> lambdaExpression,
            Function<E, List<E>> expressionChildren,
            BiFunction<E, UnaryOperator<E>, E> mapChildren,
            Function<E, FunctionCall<E>> asFunctionCall) {
        this.variableName = variableName;
        this.variableReference = variableReference;
        this.asLambda = asLambda;
        this.lambdaParameterName = lambdaParameterName;
        this.lambdaBody = lambdaBody;
        this.lambdaExpression = lambdaExpression;
        this.expressionChildren = expressionChildren;
        this.mapChildren = mapChildren;
        this.asFunctionCall = asFunctionCall;
    }

    public E foldCall(E expression) {
        var call = asFunctionCall.apply(expression);
        if (call == null) {
            return expression;
        }

        var lambda = asLambda.apply(call.callee);
        if (lambda == null || call.arguments.size() != 1) {
            return expression;
        }

        return substitute(lambdaBody.apply(lambda), lambdaParameterName.apply(lambda), call.arguments.get(0));
    }

    private E substitute(E expression, String parameterName, E replacement) {
        var currentVariableName = variableName.apply(expression);
        if (currentVariableName != null) {
            if (currentVariableName.equals(parameterName)) {
                return replacement;
            }
            return expression;
        }

        var lambda = asLambda.apply(expression);
        if (lambda != null) {
            var nestedParameterName = lambdaParameterName.apply(lambda);
            if (nestedParameterName.equals(parameterName)) {
                return expression;
            }

            var body = lambdaBody.apply(lambda);
            if (freeVariables(replacement).contains(nestedParameterName)) {
                var freshName = freshVariableName(
                    nestedParameterName,
                    List.of(usedNames(body), usedNames(replacement), Set.of(parameterName))
                );
                body = renameBoundVariable(body, nestedParameterName, freshName);
                nestedParameterName = freshName;
            }

            return lambdaExpression.apply(nestedParameterName, substitute(body, parameterName, replacement));
        }

        return mapChildren.apply(expression, current -> substitute(current, parameterName, replacement));
    }

    private E renameBoundVariable(E expression, String oldName, String newName) {
        var currentVariableName = variableName.apply(expression);
        if (currentVariableName != null) {
            if (currentVariableName.equals(oldName)) {
                return variableReference.apply(newName);
            }
            return expression;
        }

        var lambda = asLambda.apply(expression);
        if (lambda != null) {
            if (lambdaParameterName.apply(lambda).equals(oldName)) {
                return expression;
            }

            return lambdaExpression.apply(
                lambdaParameterName.apply(lambda),
                renameBoundVariable(lambdaBody.apply(lambda), oldName, newName)
            );
        }

        return mapChildren.apply(expression, current -> renameBoundVariable(current, oldName, newName));
    }

    private Set<String> freeVariables(E expression) {
        var currentVariableName = variableName.apply(expression);
        if (currentVariableName != null) {
            return new LinkedHashSet<>(Set.of(currentVariableName));
        }

        var lambda = asLambda.apply(expression);
        if (lambda != null) {
            var variables = new LinkedHashSet<>(freeVariables(lambdaBody.apply(lambda)));
            variables.remove(lambdaParameterName.apply(lambda));
            return variables;
        }

        var variables = new LinkedHashSet<String>();
        for (var child : expressionChildren.apply(expression)) {
            variables.addAll(freeVariables(child));
        }
        return variables;
    }

    private Set<String> usedNames(E expression) {
        var currentVariableName = variableName.apply(expression);
        if (currentVariableName != null) {
            return new LinkedHashSet<>(Set.of(currentVariableName));
        }

        var lambda = asLambda.apply(expression);
        if (lambda != null) {
            var names = new LinkedHashSet<>(usedNames(lambdaBody.apply(lambda)));
            names.add(lambdaParameterName.apply(lambda));
            return names;
        }

        var names = new LinkedHashSet<String>();
        for (var child : expressionChildren.apply(expression)) {
            names.addAll(usedNames(child));
        }
        return names;
    }

    private String freshVariableName(String baseName, List<Set<String>> usedNameSets) {
        var usedNames = new LinkedHashSet<String>();
        for (var names : usedNameSets) {
            usedNames.addAll(names);
        }

        var candidate = baseName;
        var suffix = 1;
        while (usedNames.contains(candidate)) {
            candidate = baseName + suffix;
            suffix += 1;
        }
        return candidate;
    }
}