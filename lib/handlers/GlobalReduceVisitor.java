package lib.handlers;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import ds.Dict;
import lib.expression.*;
import lib.visitors.ExpressionChildren;
import lib.visitors.IsomorphicGetter;
import port.IHandlerFactory;

public class GlobalReduceVisitor<T, E> implements Function<E, T> {

    private final Function<E, T> getter;
    private final BinaryOperator<T> reducer;
    private final Function<E, List<E>> children;

    GlobalReduceVisitor(Function<E, T> getter, BinaryOperator<T> reducer, Function<E, List<E>> children) {
        this.getter = getter;
        this.reducer = reducer;
        this.children = children;
    }
    public T apply(E e) {
        var result = getter.apply(e);
        for (var child : children.apply(e)) {
            result = reducer.apply(result, apply(child));
        }
        return result;
    }
}