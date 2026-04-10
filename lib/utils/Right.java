package lib.utils;

public class Right<T, V> implements Either<T, V> {
    private final V value;

    public Right(V value) {
        this.value = value;
    }

    @Override
    public <R> R accept(EitherVisitor<T, V, R> visitor) {
        return visitor.right(value);
    }
    
}
