package lib.utils;

public interface EitherVisitor<T, V, R> {
    R left(T left);

    R right(V right);
}
