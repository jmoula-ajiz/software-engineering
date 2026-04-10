package port;

import java.util.List;

public interface IExpressionFactory2<E> extends IExpressionFactory<E> {

    E lambdaExpression(String parameterName, E body);
}