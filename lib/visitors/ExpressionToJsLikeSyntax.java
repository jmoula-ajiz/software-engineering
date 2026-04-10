package lib.visitors;

import java.util.function.Function;
import java.util.stream.Collectors;

import ds.BindingPower;
import lib.expression.*;
import port.IHandlerFactory;

public class ExpressionToJsLikeSyntax<E> extends ExpressionToCLikeSyntax<E> {
    public ExpressionToJsLikeSyntax(IHandlerFactory<E> handlers, Function<E, String> jsLikeSyntaxPrinter, E e) {
        super(handlers, jsLikeSyntaxPrinter, e);
    }
}