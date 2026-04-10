package lib.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.*;

import ds.BindingPower;
import ds.Dict;
import lib.dict.BindingPowersDict;
import lib.dict.ClassNamesDict;
import lib.expression.*;
import lib.utils.Either;
import lib.utils.EitherVisitor;
import lib.utils.Left;
import lib.utils.Right;
import lib.visitors.*;
import port.ConstantFolder;
import port.IExpressionDict;
import port.IHandlerFactory1;
import port.IExpressionFactory;
import port.State;

public class HandlerFactory implements IHandlerFactory1<ExpressionV1> {
    @Override
    public IExpressionFactory<ExpressionV1> expressionFactory() {
        return new Factory();
    }

    @Override
    public Function<ExpressionV1, Boolean> literalChecker() {
        return expression -> isLiteral().apply(expression)
            .accept(new EitherVisitor<Literal<ExpressionV1>, ExpressionV1, Boolean>() {
                @Override
                public Boolean left(Literal<ExpressionV1> left) {
                    return true;
                }

                @Override
                public Boolean right(ExpressionV1 right) {
                    return false;
                }
            });
    }

    @Override
    public Function<ExpressionV1, Boolean> variableChecker() {
        return expression -> isVariable().apply(expression)
            .accept(new EitherVisitor<VariableReference<ExpressionV1>, ExpressionV1, Boolean>() {
                @Override
                public Boolean left(VariableReference<ExpressionV1> left) {
                    return true;
                }

                @Override
                public Boolean right(ExpressionV1 right) {
                    return false;
                }
            });
    }

    @Override
    public Function<ExpressionV1, ExpressionV1> constantFolderOnce() {
        return this::foldConstantOnce;
    }

    @Override
    public Function<ExpressionV1, ExpressionV1> constantFolder() {
        return new ConstantFolder<>(this);
    }

    @Override
    public Function<ExpressionV1, Integer> integerEvaluator(Map<String, Integer> variables,
            Map<String, Function<List<Integer>, Integer>> functions) {
        var evaluator = new IntegerEvaluationVisitor<ExpressionV1>(variables, functions, isVariable(), this::evaluateWithVisitor);
        return expression -> evaluateWithVisitor(expression, evaluator);
    }

    @Override
    public Function<ExpressionV1, List<String>> collectClassNamesVisitor() {
        var classNames = expressionClassNameExtractor();
        return new GlobalReduceVisitor<>(e -> new ArrayList<>(List.of(classNames.apply(e))), (left, right) -> {
            left.addAll(right);
            return left;
        }, this.expressionChildren());
    }

    @Override
    public Function<ExpressionV1, ExpressionV1> renameVariable(String oldName, String newName) {
        return new ExpressionMapper<ExpressionV1>(this,
            (expression, next) -> isVariable().apply(expression).accept(new EitherVisitor<>() {
                @Override
                public ExpressionV1 left(VariableReference<ExpressionV1> left) {
                    if (left.name.equals(oldName)) {
                        return expressionFactory().variableReference(newName);
                    }
                    return expression;
                }

                @Override
                public ExpressionV1 right(ExpressionV1 right) {
                    return next.get();
                }
            }),
            this::mapWithVisitor
        );
    }

    private Function<ExpressionV1, Either<Literal<ExpressionV1>, ExpressionV1>> isLiteral() {
        return expression -> expression
                .accept(new FallbackVisitor<Either<Literal<ExpressionV1>, ExpressionV1>, ExpressionV1>(
                        _e -> new Right<>(expression)) {
                    public Either<Literal<ExpressionV1>, ExpressionV1> visit(Literal<ExpressionV1> e) {
                        return new Left<>(e);
                    }
                });
    };

    private Function<ExpressionV1, Either<VariableReference<ExpressionV1>, ExpressionV1>> isVariable() {
        return expression -> expression
                .accept(new FallbackVisitor<Either<VariableReference<ExpressionV1>, ExpressionV1>, ExpressionV1>(
                        _e -> new Right<>(expression)) {
                    public Either<VariableReference<ExpressionV1>, ExpressionV1> visit(
                            VariableReference<ExpressionV1> e) {
                        return new Left<>(e);
                    }
                });
    };

    @Override
    public Function<ExpressionV1, List<ExpressionV1>> expressionChildren() {
        var children = new ExpressionChildren<ExpressionV1>();
        return expression -> expression.accept(children);
    }

    private ExpressionV1 foldConstantOnce(ExpressionV1 expression) {
        return expression.accept(new ConstantFolderOnce<>(expressionFactory(), expression, isLiteral()));
    }

    @Override
    public Function<ExpressionV1, ExpressionV1> expressionMapper(
            BiFunction<ExpressionV1, Supplier<ExpressionV1>, ExpressionV1> recurse) {
        var mapper = new ExpressionMapper<ExpressionV1>(this, recurse, (e, visitor) -> e.accept(visitor));
        return expression -> expression.accept(mapper);
    }

    @Override
    public Function<ExpressionV1, String> jsLikeSyntaxPrinter() {
        return expression -> expression
                .accept(new ExpressionToJsLikeSyntax<>(this, this.jsLikeSyntaxPrinter(), expression));
    }

    @Override
    public Function<ExpressionV1, String> cLikeSyntaxPrinter() {
        return expression -> expression
                .accept(new ExpressionToCLikeSyntax<>(this, this.cLikeSyntaxPrinter(), expression));
    }

    @Override
    public Function<ExpressionV1, String> lispLikeSyntaxPrinter() {
        var visitor = new ExpressionToLispLikeSyntax<>(this);
        return expression -> expression.accept(visitor);
    }

    @Override
    public <T> Function<ExpressionV1, T> dictReader(IExpressionDict<T> values) {
        var visitor = new IsomorphicGetter<T, ExpressionV1>(values);
        return expression -> expression.accept(visitor);
    }

    @Override
    public Function<ExpressionV1, String> expressionClassNameExtractor() {
        return dictReader(new ClassNamesDict());
    }

    @Override
    public Function<ExpressionV1, BindingPower> createBindingPowerHandler() {
        return dictReader(new BindingPowersDict());
    }

    private ExpressionV1 mapWithVisitor(ExpressionV1 expression, ExpressionMapper<ExpressionV1> visitor) {
        return expression.accept(visitor);
    }

    private Integer evaluateWithVisitor(ExpressionV1 expression, IntegerEvaluationVisitor<ExpressionV1> visitor) {
        return expression.accept(visitor);
    }

    public <T> State<ExpressionV1, Dict<T>, T> state() {
        return new State<ExpressionV1, Dict<T>, T>() {
            public Dict<T> intial(T value) {
                return new Dict<T>(value);
            };

            public Function<ExpressionV1, T> getter(Dict<T> state) {
                return dictReader(state);
            };

            public Consumer<ExpressionV1> setter(Dict<T> state, T value) {
                var visitor = new IsomorphicSetter<T, ExpressionV1>(state, value);
                return expression -> expression.accept(visitor);
            };
        };
    }

    public <T, R> R handleState(port.StateConsumer<ExpressionV1, T, R> handler) {
        return handler.consume(this.state());
    };

    public <T> Function<ExpressionV1, Dict<T>> localReduceVisitor(T initial, BiFunction<T, ExpressionV1, T> reducer) {

        return new LocalReduceVisitor<ExpressionV1, Dict<T>, T>(state(), initial, reducer, this.expressionChildren());
    }

    @Override
    public Function<ExpressionV1, IExpressionDict<Integer>> histogram() {
        var visitor = localReduceVisitor(0, (n, _e) -> n + 1);
        return expression -> visitor.apply(expression);
    }
}