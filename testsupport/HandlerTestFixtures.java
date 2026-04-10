package testsupport;

import lib.expression.ExpressionV1;
import lib.expression.ExpressionV2;
import lib.handlers.HandlerFactory;
import lib.handlers.HandlerFactory2;
import port.IHandlerFactory1;
import port.IHandlerFactory2;

public final class HandlerTestFixtures {
    private HandlerTestFixtures() {
    }

    public static IHandlerFactory1<ExpressionV1> v1Handler() {
        return new HandlerFactory();
    }

    public static IHandlerFactory2<ExpressionV2> v2Handler() {
        return new HandlerFactory2();
    }
}