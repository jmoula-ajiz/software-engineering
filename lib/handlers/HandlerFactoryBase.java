package lib.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import lib.expression.Literal;
import lib.expression.VariableReference;
import lib.utils.Either;
import lib.utils.EitherVisitor;
import lib.visitors.ExpressionMapper;
import lib.visitors.IntegerEvaluationVisitor;
import port.ConstantFolder;

public abstract class HandlerFactoryBase<E> implements port.IHandlerFactory<E> {

    protected abstract Function<E, Either<Literal<E>, E>> isLiteral();

    protected abstract Function<E, Either<VariableReference<E>, E>> isVariable();

    protected abstract E mapWithVisitor(E expression, ExpressionMapper<E> visitor);

    protected abstract Integer evaluateWithVisitor(E expression, IntegerEvaluationVisitor<E> visitor);

    protected abstract E foldConstantOnce(E expression);

    @Override
    public Function<E, Boolean> literalChecker() {
        return expression -> isLiteral().apply(expression)
            .accept(new EitherVisitor<Literal<E>, E, Boolean>() {
                @Override
                public Boolean left(Literal<E> left) {
                    return true;
                }

                @Override
                public Boolean right(E right) {
                    return false;
                }
            });
    }

    @Override
    public Function<E, Boolean> variableChecker() {
        return expression -> isVariable().apply(expression)
            .accept(new EitherVisitor<VariableReference<E>, E, Boolean>() {
                @Override
                public Boolean left(VariableReference<E> left) {
                    return true;
                }

                @Override
                public Boolean right(E right) {
                    return false;
                }
            });
    }

    @Override
    public Function<E, E> constantFolderOnce() {
        return this::foldConstantOnce;
    }

    @Override
    public Function<E, E> constantFolder() {
        return new ConstantFolder<>(this);
    }

    @Override
    public Function<E, Integer> integerEvaluator(Map<String, Integer> variables,
            Map<String, Function<List<Integer>, Integer>> functions) {
        var evaluator = new IntegerEvaluationVisitor<E>(variables, functions, isVariable(), this::evaluateWithVisitor);
        return expression -> evaluateWithVisitor(expression, evaluator);
    }

    @Override
    public Function<E, List<String>> collectClassNamesVisitor() {
        var classNames = expressionClassNameExtractor();
        return new GlobalReduceVisitor<>(e -> new ArrayList<>(List.of(classNames.apply(e))), (left, right) -> {
            left.addAll(right);
            return left;
        }, this.expressionChildren());
    }

    @Override
    public Function<E, E> renameVariable(String oldName, String newName) {
        return new ExpressionMapper<E>(this,
            (expression, next) -> isVariable().apply(expression).accept(new EitherVisitor<>() {
                @Override
                public E left(VariableReference<E> left) {
                    if (left.name.equals(oldName)) {
                        return expressionFactory().variableReference(newName);
                    }
                    return expression;
                }

                @Override
                public E right(E right) {
                    return next.get();
                }
            }),
            this::mapWithVisitor
        );
    }
}