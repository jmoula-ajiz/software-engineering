package lib.handlers;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;

import ds.Dict;
import lib.expression.*;
import lib.visitors.ExpressionChildren;
import lib.visitors.IsomorphicGetter;
import lib.visitors.IsomorphicSetter;
import port.IHandlerFactory;
import port.State;
import port.StateConsumer;

public class LocalReduceVisitor<E, S, T> implements Function<E, S> {
    private final Consumer<E> setter;
    private final S values;
    private final Function<E, List<E>> children;

    public LocalReduceVisitor(State<E, S, T> state, T initial, BiFunction<T, E, T> reducer, Function<E, List<E>> children) {
        values = state.intial(initial);
        var getterFunction = state.getter(values);
        this.setter = e -> state.setter(values, reducer.apply(getterFunction.apply(e), e)).accept(e);
        this.children = children;
    }

    public S apply(E e) {
        accept(e);
        return values;
    }

    private void accept(E e) {
        setter.accept(e);
        for (var child : children.apply(e)) {
            accept(child);
        }
    }
}