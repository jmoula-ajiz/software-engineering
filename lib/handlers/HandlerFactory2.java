package lib.handlers;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import ds.BindingPower;
import ds.Dict2;
import lib.dict.BindingPowersDict2;
import lib.dict.ClassNamesDict2;
import lib.expression.*;
import lib.utils.Either;
import lib.utils.EitherVisitor;
import lib.utils.Left;
import lib.utils.LambdaCallFolder;
import lib.utils.Right;
import lib.visitors.*;
import port.IExpressionDict;
import port.IExpressionDict2;
import port.IExpressionFactory2;
import port.IHandlerFactory2;
import port.State;

public class HandlerFactory2 extends HandlerFactoryBase<ExpressionV2> implements IHandlerFactory2<ExpressionV2> {
    private final Factory2 f = new Factory2();
    private final LambdaCallFolder<ExpressionV2, LambdaExpression<ExpressionV2>> lambdaFolder;

    public HandlerFactory2() {
        lambdaFolder = new LambdaCallFolder<>(
                this::variableName,
                f::variableReference,
                this::asLambda,
                l -> l.parameter,
                l -> l.body,
                f::lambdaExpression,
                expressionChildren(),
                (ex, op) -> ex.accept(new MapChildV2(f, op)),
                this::asFunctionCall);
    }

    private String variableName(ExpressionV2 e) {
        return isVariable().apply(e).accept(new EitherVisitor<VariableReference<ExpressionV2>, ExpressionV2, String>() {
            @Override
            public String left(VariableReference<ExpressionV2> l) {
                return l.name;
            }

            @Override
            public String right(ExpressionV2 r) {
                return null;
            }
        });
    }

    private LambdaExpression<ExpressionV2> asLambda(ExpressionV2 e) {
        var inner = e.unwrap();
        if (inner instanceof LambdaExpression<ExpressionV2> lam) {
            return lam;
        }
        return null;
    }

    private FunctionCall<ExpressionV2> asFunctionCall(ExpressionV2 e) {
        var inner = e.unwrap();
        if (inner instanceof FunctionCall<ExpressionV2> call) {
            return call;
        }
        return null;
    }

    @Override
    public IExpressionFactory2<ExpressionV2> expressionFactory() {
        return f;
    }

    @Override
    protected Function<ExpressionV2, Either<Literal<ExpressionV2>, ExpressionV2>> isLiteral() {
        return expression -> expression.accept(new FallbackVisitorV2<Either<Literal<ExpressionV2>, ExpressionV2>, ExpressionV2>(
                _e -> new Right<>(expression)) {
            @Override
            public Either<Literal<ExpressionV2>, ExpressionV2> visit(Literal<ExpressionV2> e) {
                return new Left<>(e);
            }
        });
    }

    @Override
    protected Function<ExpressionV2, Either<VariableReference<ExpressionV2>, ExpressionV2>> isVariable() {
        return expression -> expression.accept(new FallbackVisitorV2<Either<VariableReference<ExpressionV2>, ExpressionV2>, ExpressionV2>(
                _e -> new Right<>(expression)) {
            @Override
            public Either<VariableReference<ExpressionV2>, ExpressionV2> visit(VariableReference<ExpressionV2> e) {
                return new Left<>(e);
            }
        });
    }

    @Override
    public Function<ExpressionV2, ExpressionV2> renameVariable(String oldName, String newName) {
        return new ExpressionMapperV2(this,
                (expression, next) -> isVariable().apply(expression).accept(new EitherVisitor<>() {
                    @Override
                    public ExpressionV2 left(VariableReference<ExpressionV2> left) {
                        if (left.name.equals(oldName)) {
                            return expressionFactory().variableReference(newName);
                        }
                        return expression;
                    }

                    @Override
                    public ExpressionV2 right(ExpressionV2 right) {
                        return next.get();
                    }
                }),
                this::mapWithVisitor);
    }

    @Override
    public Function<ExpressionV2, java.util.List<ExpressionV2>> expressionChildren() {
        var ch = new ExpressionChildrenV2<ExpressionV2>();
        return expression -> expression.accept(ch);
    }

    @Override
    protected ExpressionV2 foldConstantOnce(ExpressionV2 expression) {
        var folded = lambdaFolder.foldCall(expression);
        return folded.accept(new ConstantFolderOnceV2(f, folded, isLiteral()));
    }

    @Override
    public Function<ExpressionV2, ExpressionV2> expressionMapper(
            BiFunction<ExpressionV2, Supplier<ExpressionV2>, ExpressionV2> recurse) {
        return new ExpressionMapperV2(this, recurse, (e, visitor) -> e.accept(visitor));
    }

    @Override
    public Function<ExpressionV2, String> jsLikeSyntaxPrinter() {
        return expression -> expression.accept(new JsLikeV2(this, this.jsLikeSyntaxPrinter(), expression));
    }

    @Override
    public Function<ExpressionV2, String> lispLikeSyntaxPrinter() {
        var visitor = new LispLikeV2(this);
        return expression -> expression.accept(visitor);
    }

    @Override
    public <T> Function<ExpressionV2, T> dictReader(IExpressionDict2<T> values) {
        var visitor = new IsomorphicGetterV2<T, ExpressionV2>(values);
        return expression -> expression.accept(visitor);
    }

    @Override
    public Function<ExpressionV2, String> expressionClassNameExtractor() {
        return dictReader(new ClassNamesDict2());
    }

    @Override
    public Function<ExpressionV2, BindingPower> createBindingPowerHandler() {
        return dictReader(new BindingPowersDict2());
    }

    @Override
    protected ExpressionV2 mapWithVisitor(ExpressionV2 expression, ExpressionMapper<ExpressionV2> visitor) {
        return expression.accept(visitor);
    }

    @Override
    protected Integer evaluateWithVisitor(ExpressionV2 expression, IntegerEvaluationVisitor<ExpressionV2> visitor) {
        return expression.accept(visitor);
    }

    public <T> State<ExpressionV2, Dict2<T>, T> state() {
        return new State<ExpressionV2, Dict2<T>, T>() {
            @Override
            public Dict2<T> intial(T value) {
                return new Dict2<>(value);
            }

            @Override
            public Function<ExpressionV2, T> getter(Dict2<T> state) {
                return dictReader(state);
            }

            @Override
            public Consumer<ExpressionV2> setter(Dict2<T> state, T value) {
                return expression -> expression.accept(new IsomorphicSetterV2<>(state, value));
            }
        };
    }

    @Override
    public <T, R> R handleState(port.StateConsumer<ExpressionV2, T, R> handler) {
        return handler.consume(this.state());
    }

    public <T> Function<ExpressionV2, Dict2<T>> localReduceVisitor(T initial, BiFunction<T, ExpressionV2, T> reducer) {
        return new LocalReduceVisitor<ExpressionV2, Dict2<T>, T>(state(), initial, reducer, this.expressionChildren());
    }

    @Override
    public Function<ExpressionV2, IExpressionDict<Integer>> histogram() {
        var visitor = localReduceVisitor(0, (n, _e) -> n + 1);
        return expression -> visitor.apply(expression);
    }

    @Override
    public Function<ExpressionV2, IExpressionDict2<Integer>> histogram2() {
        var visitor = localReduceVisitor(0, (n, _e) -> n + 1);
        return expression -> visitor.apply(expression);
    }

    @Override
    public IExpressionDict2<String> collectClassNamesDict() {
        return new ClassNamesDict2();
    }
}
