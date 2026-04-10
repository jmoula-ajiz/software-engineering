package port;

public interface StateConsumer<E, T, R> {
    <S> R consume(State<E, S, T> state);
}

