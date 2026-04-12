package lib.handlers;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import ds.BindingPower;
import ds.Dict2;
import lib.dict.BindingPowersDict;
import lib.dict.ClassNamesDict;
import lib.expression.*;
import lib.utils.Either;
import lib.utils.EitherVisitor;
import lib.utils.Left;
import lib.utils.LambdaCallFolder;
import lib.utils.Right;
import lib.visitors.*;
import port.IExpressionDict2;
import port.IExpressionFactory2;
import port.IHandlerFactory;
import port.IHandlerFactory2;
import port.State;
import port.StateConsumer;

public class HandlerFactory2 extends HandlerFactoryBase<ExpressionV2> implements IHandlerFactory2<ExpressionV2> {
    private final Factory2 f = new Factory2();
    private final LambdaCallFolder<ExpressionV2, LambdaExpression<ExpressionV2>> m;

    public HandlerFactory2() {
        m = new LambdaCallFolder<>(this::variableName, f::variableReference, this::asLambda, l -> l.parameter, l -> l.body, f::lambdaExpression,
                expressionChildren(), (ex, op) -> ex.accept(new W.P(f, op)), this::asFunctionCall);
    }

    private String variableName(ExpressionV2 e) {
        return isVariable().apply(e).accept(new EitherVisitor<VariableReference<ExpressionV2>, ExpressionV2, String>() {
            public String left(VariableReference<ExpressionV2> l) { return l.name; }
            public String right(ExpressionV2 r) { return null; }
        });
    }

    private LambdaExpression<ExpressionV2> asLambda(ExpressionV2 e) {
        var i = e.unwrap();
        return i instanceof LambdaExpression<ExpressionV2> lam ? lam : null;
    }

    private FunctionCall<ExpressionV2> asFunctionCall(ExpressionV2 e) {
        var i = e.unwrap();
        return i instanceof FunctionCall<ExpressionV2> c ? c : null;
    }

    public IExpressionFactory2<ExpressionV2> expressionFactory() { return f; }

    protected Function<ExpressionV2, Either<Literal<ExpressionV2>, ExpressionV2>> isLiteral() {
        return expression -> expression.accept(new W.A<Either<Literal<ExpressionV2>, ExpressionV2>, ExpressionV2>(_e -> new Right<>(expression)) {
            public Either<Literal<ExpressionV2>, ExpressionV2> visit(Literal<ExpressionV2> e) { return new Left<>(e); }
        });
    }

    protected Function<ExpressionV2, Either<VariableReference<ExpressionV2>, ExpressionV2>> isVariable() {
        return expression -> expression.accept(new W.A<Either<VariableReference<ExpressionV2>, ExpressionV2>, ExpressionV2>(_e -> new Right<>(expression)) {
            public Either<VariableReference<ExpressionV2>, ExpressionV2> visit(VariableReference<ExpressionV2> e) { return new Left<>(e); }
        });
    }

    public Function<ExpressionV2, ExpressionV2> renameVariable(String oldName, String newName) {
        return new W.E(this, (expression, next) -> isVariable().apply(expression).accept(new EitherVisitor<>() {
            public ExpressionV2 left(VariableReference<ExpressionV2> left) {
                return left.name.equals(oldName) ? expressionFactory().variableReference(newName) : expression;
            }
            public ExpressionV2 right(ExpressionV2 right) { return next.get(); }
        }), this::mapWithVisitor);
    }

    public Function<ExpressionV2, List<ExpressionV2>> expressionChildren() {
        var ch = new W.C<ExpressionV2>();
        return expression -> expression.accept(ch);
    }

    protected ExpressionV2 foldConstantOnce(ExpressionV2 expression) {
        var folded = foldCallChain(expression);
        return folded.accept(new W.K(f, folded, isLiteral()));
    }

    private ExpressionV2 foldCallChain(ExpressionV2 e) {
        ExpressionV2 n = m.foldCall(e);
        return n == e ? e : foldCallChain(n);
    }

    public Function<ExpressionV2, ExpressionV2> expressionMapper(BiFunction<ExpressionV2, Supplier<ExpressionV2>, ExpressionV2> recurse) {
        return new W.E(this, recurse, (e, visitor) -> e.accept(visitor));
    }

    public Function<ExpressionV2, String> jsLikeSyntaxPrinter() {
        return expression -> expression.accept(new W.J(this, this.jsLikeSyntaxPrinter(), expression));
    }

    public Function<ExpressionV2, String> lispLikeSyntaxPrinter() {
        var v = new W.L(this);
        return expression -> expression.accept(v);
    }

    public <T> Function<ExpressionV2, T> dictReader(IExpressionDict2<T> values) {
        var visitor = new W.G<T, ExpressionV2>(values);
        return expression -> expression.accept(visitor);
    }

    public Function<ExpressionV2, String> expressionClassNameExtractor() { return dictReader(new W.N()); }

    public Function<ExpressionV2, BindingPower> createBindingPowerHandler() { return dictReader(new W.B()); }

    protected ExpressionV2 mapWithVisitor(ExpressionV2 expression, ExpressionMapper<ExpressionV2> visitor) { return expression.accept(visitor); }

    protected Integer evaluateWithVisitor(ExpressionV2 expression, IntegerEvaluationVisitor<ExpressionV2> visitor) { return expression.accept(visitor); }

    public <T> State<ExpressionV2, Dict2<T>, T> state() {
        return new State<ExpressionV2, Dict2<T>, T>() {
            public Dict2<T> intial(T value) { return new Dict2<>(value); }
            public Function<ExpressionV2, T> getter(Dict2<T> state) { return dictReader(state); }
            public Consumer<ExpressionV2> setter(Dict2<T> state, T value) {
                return expression -> expression.accept(new W.S<T, ExpressionV2>(state, value));
            }
        };
    }

    public <T, R> R handleState(StateConsumer<ExpressionV2, T, R> handler) { return handler.consume(this.state()); }

    public <T> Function<ExpressionV2, Dict2<T>> localReduceVisitor(T initial, BiFunction<T, ExpressionV2, T> reducer) {
        return new LocalReduceVisitor<ExpressionV2, Dict2<T>, T>(state(), initial, reducer, this.expressionChildren());
    }

    private Function<ExpressionV2, Dict2<Integer>> h() { return localReduceVisitor(0, (n, _e) -> n + 1); }

    @SuppressWarnings("unchecked")
    public Function<ExpressionV2, port.IExpressionDict<Integer>> histogram() { return (Function) h(); }

    @SuppressWarnings("unchecked")
    public Function<ExpressionV2, IExpressionDict2<Integer>> histogram2() { return (Function) h(); }

    public IExpressionDict2<String> collectClassNamesDict() { return new W.N(); }
}

final class W {
    abstract static class A<R, E> extends FallbackVisitor<R, E> implements ExpressionVisitorV2<R, E> {
        private final Function<Expression<E>, R> t;
        A(Function<Expression<E>, R> f) { super(f); t = f; }
        public R visitLambda(LambdaExpression<E> e) { return t.apply(e); }
    }

    static final class E extends ExpressionMapper<ExpressionV2> implements ExpressionVisitorV2<ExpressionV2, ExpressionV2> {
        private final IExpressionFactory2<ExpressionV2> f2;
        E(IHandlerFactory<ExpressionV2> h, BiFunction<ExpressionV2, Supplier<ExpressionV2>, ExpressionV2> r, BiFunction<ExpressionV2, ExpressionMapper<ExpressionV2>, ExpressionV2> a) {
            super(h, r, a);
            f2 = (IExpressionFactory2<ExpressionV2>) h.expressionFactory();
        }
        public ExpressionV2 visitLambda(LambdaExpression<ExpressionV2> e) { return f2.lambdaExpression(e.parameter, apply(e.body)); }
    }

    static final class C<E> extends ExpressionChildren<E> implements ExpressionVisitorV2<List<E>, E> {
        public List<E> visitLambda(LambdaExpression<E> e) { return List.of(e.body); }
    }

    static final class K extends ConstantFolderOnce<ExpressionV2> implements ExpressionVisitorV2<ExpressionV2, ExpressionV2> {
        private final ExpressionV2 r;
        K(IExpressionFactory2<ExpressionV2> f, ExpressionV2 root, Function<ExpressionV2, Either<Literal<ExpressionV2>, ExpressionV2>> il) {
            super(f, root, il);
            r = root;
        }
        public ExpressionV2 visitLambda(LambdaExpression<ExpressionV2> e) { return r; }
    }

    static final class G<T, E> extends IsomorphicGetter<T, E> implements ExpressionVisitorV2<T, E> {
        private final IExpressionDict2<T> d;
        G(IExpressionDict2<T> g) { super(g); d = g; }
        public T visitLambda(LambdaExpression<E> e) { return d.lambdaExpression(); }
    }

    static final class S<T, E> extends IsomorphicSetter<T, E> implements ExpressionVisitorV2<Void, E> {
        private final Dict2<T> d;
        private final T v;
        S(Dict2<T> s, T x) { super(s, x); d = s; v = x; }
        public Void visitLambda(LambdaExpression<E> e) { d.lambdaExpression = v; return null; }
    }

    static final class P implements ExpressionVisitorV2<ExpressionV2, ExpressionV2> {
        private final IExpressionFactory2<ExpressionV2> f;
        private final UnaryOperator<ExpressionV2> o;
        P(IExpressionFactory2<ExpressionV2> f, UnaryOperator<ExpressionV2> o) { this.f = f; this.o = o; }
        public ExpressionV2 visit(Literal<ExpressionV2> e) { return f.literal(e.value); }
        public ExpressionV2 visit(VariableReference<ExpressionV2> e) { return f.variableReference(e.name); }
        public ExpressionV2 visit(Addition<ExpressionV2> e) { return f.addition(o.apply(e.left), o.apply(e.right)); }
        public ExpressionV2 visit(Subtraction<ExpressionV2> e) { return f.subtraction(o.apply(e.left), o.apply(e.right)); }
        public ExpressionV2 visit(Multiplication<ExpressionV2> e) { return f.multiplication(o.apply(e.left), o.apply(e.right)); }
        public ExpressionV2 visit(Division<ExpressionV2> e) { return f.division(o.apply(e.dividend), o.apply(e.divisor)); }
        public ExpressionV2 visit(Negation<ExpressionV2> e) { return f.negation(o.apply(e.operand)); }
        public ExpressionV2 visit(Modulo<ExpressionV2> e) { return f.modulo(o.apply(e.left), o.apply(e.right)); }
        public ExpressionV2 visit(Exponentiation<ExpressionV2> e) { return f.exponentiation(o.apply(e.base), o.apply(e.exponent)); }
        public ExpressionV2 visit(Equality<ExpressionV2> e) { return f.equality(o.apply(e.left), o.apply(e.right)); }
        public ExpressionV2 visit(Inequality<ExpressionV2> e) { return f.inequality(o.apply(e.left), o.apply(e.right)); }
        public ExpressionV2 visit(LessThan<ExpressionV2> e) { return f.lessThan(o.apply(e.left), o.apply(e.right)); }
        public ExpressionV2 visit(GreaterThan<ExpressionV2> e) { return f.greaterThan(o.apply(e.left), o.apply(e.right)); }
        public ExpressionV2 visit(LessThanOrEqual<ExpressionV2> e) { return f.lessThanOrEqual(o.apply(e.left), o.apply(e.right)); }
        public ExpressionV2 visit(GreaterThanOrEqual<ExpressionV2> e) { return f.greaterThanOrEqual(o.apply(e.left), o.apply(e.right)); }
        public ExpressionV2 visit(Conjunction<ExpressionV2> e) { return f.conjunction(o.apply(e.left), o.apply(e.right)); }
        public ExpressionV2 visit(Disjunction<ExpressionV2> e) { return f.disjunction(o.apply(e.left), o.apply(e.right)); }
        public ExpressionV2 visit(LogicalNot<ExpressionV2> e) { return f.logicalNot(o.apply(e.operand)); }
        public ExpressionV2 visit(Conditional<ExpressionV2> e) { return f.conditional(o.apply(e.condition), o.apply(e.whenTrue), o.apply(e.whenFalse)); }
        public ExpressionV2 visit(FunctionCall<ExpressionV2> e) { return f.functionCall(o.apply(e.callee), e.arguments.stream().map(o).toList()); }
        public ExpressionV2 visitLambda(LambdaExpression<ExpressionV2> e) { return f.lambdaExpression(e.parameter, o.apply(e.body)); }
    }

    static final class J extends ExpressionToJsLikeSyntax<ExpressionV2> implements ExpressionVisitorV2<String, ExpressionV2> {
        private final IHandlerFactory<ExpressionV2> h;
        J(IHandlerFactory<ExpressionV2> h, Function<ExpressionV2, String> p, ExpressionV2 e) { super(h, p, e); this.h = h; }
        public String visitLambda(LambdaExpression<ExpressionV2> e) { return e.parameter + " => " + e.body.accept(new J(h, h.jsLikeSyntaxPrinter(), e.body)); }
    }

    static final class L extends ExpressionToLispLikeSyntax<ExpressionV2> implements ExpressionVisitorV2<String, ExpressionV2> {
        private final IHandlerFactory<ExpressionV2> h;
        L(IHandlerFactory<ExpressionV2> x) { super(x); h = x; }
        public String visitLambda(LambdaExpression<ExpressionV2> e) { return "(lambda (" + e.parameter + ") " + e.body.accept(new L(h)) + ")"; }
    }

    static final class N extends Dict2<String> {
        N() { super(new ClassNamesDict(), "LambdaExpression"); }
    }
    static final class B extends Dict2<BindingPower> {
        B() { super(new BindingPowersDict(), new BindingPower(30, true)); }
    }
}
