package spec.visitors;

import port.IExpressionFactory;

abstract class TestBase<E> {
    protected final TestSupport<E> testSupport;
    protected final IExpressionFactory<E> factory;

    TestBase(TestSupport<E> testSupport) {
        this.testSupport = testSupport;
        this.factory = testSupport.factory;
    }

    protected final String typeName(E expression) {
        return testSupport.v.expressionClassNameExtractor().apply(expression);
    }

    protected final String render(E expression) {
        return testSupport.v.jsLikeSyntaxPrinter().apply(expression);
    }

    protected final String renderJsLike(E expression) {
        return render(expression);
    }

    protected final String renderLispLike(E expression) {
        return testSupport.v.lispLikeSyntaxPrinter().apply(expression);
    }
}