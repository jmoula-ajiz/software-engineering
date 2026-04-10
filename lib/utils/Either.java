package lib.utils;

public interface Either<T, V> {
    <R> R accept(EitherVisitor<T, V, R> visitor);
}

