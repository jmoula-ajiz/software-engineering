package lib.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

import ds.BindingPower;
import ds.Dict;
import lib.dict.BindingPowersDict;
import lib.dict.ClassNamesDict;
import lib.expression.*;
import lib.utils.Either;
import lib.utils.Left;
import lib.utils.Right;
import lib.visitors.*;
import port.IExpressionDict;
import port.IHandlerFactory1;
import port.IExpressionFactory;
import port.State;

public class HandlerFactory extends HandlerFactoryBase<ExpressionV1> implements IHandlerFactory1<ExpressionV1> {
    @Override
    public IExpressionFactory<ExpressionV1> expressionFactory() {
        return new Factory();
    }

    @Override
    protected Function<ExpressionV1, Either<Literal<ExpressionV1>, ExpressionV1>> isLiteral() {
        return expression -> expression
                .accept(new FallbackVisitor<Either<Literal<ExpressionV1>, ExpressionV1>, ExpressionV1>(
                        _e -> new Right<>(expression)) {
                    public Either<Literal<ExpressionV1>, ExpressionV1> visit(Literal<ExpressionV1> e) {
                        return new Left<>(e);
                    }
                });
    };

    @Override
    protected Function<ExpressionV1, Either<VariableReference<ExpressionV1>, ExpressionV1>> isVariable() {
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

    @Override
    protected ExpressionV1 foldConstantOnce(ExpressionV1 expression) {
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

    @Override
    protected ExpressionV1 mapWithVisitor(ExpressionV1 expression, ExpressionMapper<ExpressionV1> visitor) {
        return expression.accept(visitor);
    }

    @Override
    protected Integer evaluateWithVisitor(ExpressionV1 expression, IntegerEvaluationVisitor<ExpressionV1> visitor) {
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