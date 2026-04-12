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
import port.IExpressionDict;
import port.IExpressionDict2;
import port.IExpressionFactory2;
import port.IHandlerFactory;
import port.IHandlerFactory2;
import port.State;

public class HandlerFactory2 extends HandlerFactoryBase<ExpressionV2> implements IHandlerFactory2<ExpressionV2> {
    private final Factory2 f = new Factory2();
    private final LambdaCallFolder<ExpressionV2, LambdaExpression<ExpressionV2>> lambdaFolder;

    public HandlerFactory2() {
        lambdaFolder = new LambdaCallFolder<>(this::variableName, f::variableReference, this::asLambda, l -> l.parameter, l -> l.body, f::lambdaExpression,
                expressionChildren(), (ex, op) -> ex.accept(new W.Mc(f, op)), this::asFunctionCall);
    }

    private String variableName(ExpressionV2 e) {
        return isVariable().apply(e).accept(new EitherVisitor<VariableReference<ExpressionV2>, ExpressionV2, String>() {
            @Override public String left(VariableReference<ExpressionV2> l) { return l.name; }
            @Override public String right(ExpressionV2 r) { return null; }
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

    @Override
    public IExpressionFactory2<ExpressionV2> expressionFactory() { return f; }

    @Override
    protected Function<ExpressionV2, Either<Literal<ExpressionV2>, ExpressionV2>> isLiteral() {
        return expression -> expression.accept(new W.Fb<Either<Literal<ExpressionV2>, ExpressionV2>, ExpressionV2>(_e -> new Right<>(expression)) {
            @Override public Either<Literal<ExpressionV2>, ExpressionV2> visit(Literal<ExpressionV2> e) { return new Left<>(e); }
        });
    }

    @Override
    protected Function<ExpressionV2, Either<VariableReference<ExpressionV2>, ExpressionV2>> isVariable() {
        return expression -> expression.accept(new W.Fb<Either<VariableReference<ExpressionV2>, ExpressionV2>, ExpressionV2>(_e -> new Right<>(expression)) {
            @Override public Either<VariableReference<ExpressionV2>, ExpressionV2> visit(VariableReference<ExpressionV2> e) { return new Left<>(e); }
        });
    }

    @Override
    public Function<ExpressionV2, ExpressionV2> renameVariable(String oldName, String newName) {
        return new W.Em(this, (expression, next) -> isVariable().apply(expression).accept(new EitherVisitor<>() {
            @Override public ExpressionV2 left(VariableReference<ExpressionV2> left) {
                return left.name.equals(oldName) ? expressionFactory().variableReference(newName) : expression;
            }
            @Override public ExpressionV2 right(ExpressionV2 right) { return next.get(); }
        }), this::mapWithVisitor);
    }

    @Override
    public Function<ExpressionV2, List<ExpressionV2>> expressionChildren() {
        var ch = new W.Ec<ExpressionV2>();
        return expression -> expression.accept(ch);
    }

    @Override
    protected ExpressionV2 foldConstantOnce(ExpressionV2 expression) {
        var folded = foldCallChain(expression);
        return folded.accept(new W.Cf(f, folded, isLiteral()));
    }

    private ExpressionV2 foldCallChain(ExpressionV2 e) {
        ExpressionV2 n = lambdaFolder.foldCall(e);
        return n == e ? e : foldCallChain(n);
    }

    @Override
    public Function<ExpressionV2, ExpressionV2> expressionMapper(BiFunction<ExpressionV2, Supplier<ExpressionV2>, ExpressionV2> recurse) {
        return new W.Em(this, recurse, (e, visitor) -> e.accept(visitor));
    }

    @Override
    public Function<ExpressionV2, String> jsLikeSyntaxPrinter() {
        return expression -> expression.accept(new W.Js(this, this.jsLikeSyntaxPrinter(), expression));
    }

    @Override
    public Function<ExpressionV2, String> lispLikeSyntaxPrinter() {
        var v = new W.Ls(this);
        return expression -> expression.accept(v);
    }

    @Override
    public <T> Function<ExpressionV2, T> dictReader(IExpressionDict2<T> values) {
        var visitor = new W.Ig<T, ExpressionV2>(values);
        return expression -> expression.accept(visitor);
    }

    @Override
    public Function<ExpressionV2, String> expressionClassNameExtractor() { return dictReader(new W.CN()); }

    @Override
    public Function<ExpressionV2, BindingPower> createBindingPowerHandler() { return dictReader(new W.BP()); }

    @Override
    protected ExpressionV2 mapWithVisitor(ExpressionV2 expression, ExpressionMapper<ExpressionV2> visitor) { return expression.accept(visitor); }

    @Override
    protected Integer evaluateWithVisitor(ExpressionV2 expression, IntegerEvaluationVisitor<ExpressionV2> visitor) { return expression.accept(visitor); }

    public <T> State<ExpressionV2, Dict2<T>, T> state() {
        return new State<ExpressionV2, Dict2<T>, T>() {
            @Override public Dict2<T> intial(T value) { return new Dict2<>(value); }
            @Override public Function<ExpressionV2, T> getter(Dict2<T> state) { return dictReader(state); }
            @Override public Consumer<ExpressionV2> setter(Dict2<T> state, T value) {
                return expression -> expression.accept(new W.Is<>(state, value));
            }
        };
    }

    @Override
    public <T, R> R handleState(port.StateConsumer<ExpressionV2, T, R> handler) { return handler.consume(this.state()); }

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
    public IExpressionDict2<String> collectClassNamesDict() { return new W.CN(); }
}

final class W {
    private W() {}

    public abstract static class Fb<R, E> extends FallbackVisitor<R, E> implements ExpressionVisitorV2<R, E> {
        private final Function<Expression<E>, R> t;
        public Fb(Function<Expression<E>, R> f) { super(f); this.t = f; }
        @Override public R visitLambda(LambdaExpression<E> e) { return t.apply(e); }
    }

    public static final class Em extends ExpressionMapper<ExpressionV2> implements ExpressionVisitorV2<ExpressionV2, ExpressionV2> {
        private final IExpressionFactory2<ExpressionV2> f2;
        public Em(IHandlerFactory<ExpressionV2> h, BiFunction<ExpressionV2, Supplier<ExpressionV2>, ExpressionV2> r, BiFunction<ExpressionV2, ExpressionMapper<ExpressionV2>, ExpressionV2> a) {
            super(h, r, a);
            f2 = (IExpressionFactory2<ExpressionV2>) h.expressionFactory();
        }
        @Override public ExpressionV2 visitLambda(LambdaExpression<ExpressionV2> e) { return f2.lambdaExpression(e.parameter, apply(e.body)); }
    }

    public static final class Ec<E> extends ExpressionChildren<E> implements ExpressionVisitorV2<List<E>, E> {
        @Override public List<E> visitLambda(LambdaExpression<E> e) { return List.of(e.body); }
    }

    public static final class Cf implements ExpressionVisitorV2<ExpressionV2, ExpressionV2> {
        private final ConstantFolderOnce<ExpressionV2> i;
        private final ExpressionV2 r;
        public Cf(IExpressionFactory2<ExpressionV2> f, ExpressionV2 root, Function<ExpressionV2, Either<Literal<ExpressionV2>, ExpressionV2>> il) {
            r = root;
            i = new ConstantFolderOnce<>(f, root, il);
        }
        public ExpressionV2 visit(Literal<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(VariableReference<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(Addition<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(Subtraction<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(Multiplication<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(Division<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(Negation<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(Modulo<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(Exponentiation<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(Equality<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(Inequality<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(LessThan<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(GreaterThan<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(LessThanOrEqual<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(GreaterThanOrEqual<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(Conjunction<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(Disjunction<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(LogicalNot<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(Conditional<ExpressionV2> e) { return i.visit(e); }
        public ExpressionV2 visit(FunctionCall<ExpressionV2> e) { return i.visit(e); }
        @Override public ExpressionV2 visitLambda(LambdaExpression<ExpressionV2> e) { return r; }
    }

    public static final class Ig<T, E> implements ExpressionVisitorV2<T, E> {
        private final IExpressionDict2<T> v;
        public Ig(IExpressionDict2<T> x) { v = x; }
        public T visit(Literal<E> e) { return v.literal(); }
        public T visit(VariableReference<E> e) { return v.variableReference(); }
        public T visit(Addition<E> e) { return v.addition(); }
        public T visit(Subtraction<E> e) { return v.subtraction(); }
        public T visit(Multiplication<E> e) { return v.multiplication(); }
        public T visit(Division<E> e) { return v.division(); }
        public T visit(Negation<E> e) { return v.negation(); }
        public T visit(Modulo<E> e) { return v.modulo(); }
        public T visit(Exponentiation<E> e) { return v.exponentiation(); }
        public T visit(Equality<E> e) { return v.equality(); }
        public T visit(Inequality<E> e) { return v.inequality(); }
        public T visit(LessThan<E> e) { return v.lessThan(); }
        public T visit(GreaterThan<E> e) { return v.greaterThan(); }
        public T visit(LessThanOrEqual<E> e) { return v.lessThanOrEqual(); }
        public T visit(GreaterThanOrEqual<E> e) { return v.greaterThanOrEqual(); }
        public T visit(Conjunction<E> e) { return v.conjunction(); }
        public T visit(Disjunction<E> e) { return v.disjunction(); }
        public T visit(LogicalNot<E> e) { return v.logicalNot(); }
        public T visit(Conditional<E> e) { return v.conditional(); }
        public T visit(FunctionCall<E> e) { return v.functionCall(); }
        @Override public T visitLambda(LambdaExpression<E> e) { return v.lambdaExpression(); }
    }

    public static final class Is<T, E> implements ExpressionVisitorV2<Void, E> {
        private final Dict2<T> d;
        private final T x;
        public Is(Dict2<T> d, T x) { this.d = d; this.x = x; }
        public Void visit(Literal<E> e) { d.literal = x; return null; }
        public Void visit(VariableReference<E> e) { d.variableReference = x; return null; }
        public Void visit(Addition<E> e) { d.addition = x; return null; }
        public Void visit(Subtraction<E> e) { d.subtraction = x; return null; }
        public Void visit(Multiplication<E> e) { d.multiplication = x; return null; }
        public Void visit(Division<E> e) { d.division = x; return null; }
        public Void visit(Negation<E> e) { d.negation = x; return null; }
        public Void visit(Modulo<E> e) { d.modulo = x; return null; }
        public Void visit(Exponentiation<E> e) { d.exponentiation = x; return null; }
        public Void visit(Equality<E> e) { d.equality = x; return null; }
        public Void visit(Inequality<E> e) { d.inequality = x; return null; }
        public Void visit(LessThan<E> e) { d.lessThan = x; return null; }
        public Void visit(GreaterThan<E> e) { d.greaterThan = x; return null; }
        public Void visit(LessThanOrEqual<E> e) { d.lessThanOrEqual = x; return null; }
        public Void visit(GreaterThanOrEqual<E> e) { d.greaterThanOrEqual = x; return null; }
        public Void visit(Conjunction<E> e) { d.conjunction = x; return null; }
        public Void visit(Disjunction<E> e) { d.disjunction = x; return null; }
        public Void visit(LogicalNot<E> e) { d.logicalNot = x; return null; }
        public Void visit(Conditional<E> e) { d.conditional = x; return null; }
        public Void visit(FunctionCall<E> e) { d.functionCall = x; return null; }
        @Override public Void visitLambda(LambdaExpression<E> e) { d.lambdaExpression = x; return null; }
    }

    public static final class Mc implements ExpressionVisitorV2<ExpressionV2, ExpressionV2> {
        private final IExpressionFactory2<ExpressionV2> f;
        private final UnaryOperator<ExpressionV2> o;
        public Mc(IExpressionFactory2<ExpressionV2> f, UnaryOperator<ExpressionV2> o) { this.f = f; this.o = o; }
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
        @Override public ExpressionV2 visitLambda(LambdaExpression<ExpressionV2> e) { return f.lambdaExpression(e.parameter, o.apply(e.body)); }
    }

    public static final class Js extends ExpressionToJsLikeSyntax<ExpressionV2> implements ExpressionVisitorV2<String, ExpressionV2> {
        private final IHandlerFactory<ExpressionV2> h;
        public Js(IHandlerFactory<ExpressionV2> h, Function<ExpressionV2, String> p, ExpressionV2 e) { super(h, p, e); this.h = h; }
        @Override public String visitLambda(LambdaExpression<ExpressionV2> e) { return e.parameter + " => " + e.body.accept(new Js(h, h.jsLikeSyntaxPrinter(), e.body)); }
    }

    public static final class Ls extends ExpressionToLispLikeSyntax<ExpressionV2> implements ExpressionVisitorV2<String, ExpressionV2> {
        private final IHandlerFactory<ExpressionV2> h;
        public Ls(IHandlerFactory<ExpressionV2> x) { super(x); h = x; }
        @Override public String visitLambda(LambdaExpression<ExpressionV2> e) { return "(lambda (" + e.parameter + ") " + e.body.accept(new Ls(h)) + ")"; }
    }

    public static final class CN extends Dict2<String> {
        public CN() { super(new ClassNamesDict(), "LambdaExpression"); }
    }
    public static final class BP extends Dict2<BindingPower> {
        public BP() { super(new BindingPowersDict(), new BindingPower(30, true)); }
    }
}
