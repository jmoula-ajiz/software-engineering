package port;

import java.util.function.Function;

public interface IHandlerFactory1<E> extends IHandlerFactory<E> {

    <T> Function<E, T> dictReader(IExpressionDict<T> values);

    Function<E, String> cLikeSyntaxPrinter();
}