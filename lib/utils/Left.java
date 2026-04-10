package lib.utils;

public class Left<T, V> implements Either<T, V> {
    private final T value;

    public Left(T value) {
        this.value = value;
    }

    @Override
    public <R> R accept(EitherVisitor<T, V, R> visitor) {
        return visitor.left(value);
    }
    
}
