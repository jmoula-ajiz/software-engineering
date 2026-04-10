package port;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface State<E, S, T> {
    S intial(T value);
    Function<E, T> getter(S state);
    Consumer<E> setter(S state, T value);    
}
