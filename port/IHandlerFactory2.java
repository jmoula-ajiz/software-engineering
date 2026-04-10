package port;

import java.util.function.Function;

public interface IHandlerFactory2<E> extends IHandlerFactory<E> {

    IExpressionFactory2<E> expressionFactory();

    <T> Function<E, T> dictReader(IExpressionDict2<T> values);

    Function<E, IExpressionDict2<Integer>> histogram2();

    IExpressionDict2<String> collectClassNamesDict();
}