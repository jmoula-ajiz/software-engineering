package testsupport;

import lib.expression.ExpressionV1;
import lib.handlers.HandlerFactory;
import port.IHandlerFactory1;

public final class HandlerTestFixtures {
    private HandlerTestFixtures() {
    }

    public static IHandlerFactory1<ExpressionV1> v1Handler() {
        return new HandlerFactory();
    }
}