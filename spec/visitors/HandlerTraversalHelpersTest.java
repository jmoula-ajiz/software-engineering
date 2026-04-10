package spec.visitors;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import lib.expression.ExpressionV1;
import port.State;
import port.StateConsumer;
import testsupport.HandlerTestFixtures;

abstract class HandlerTraversalHelpersTestBase<E> extends TestBase<E> {
    HandlerTraversalHelpersTestBase(TestSupport<E> testSupport) {
        super(testSupport);
    }

    @Test
    void histogramCountsNodeKindsAcrossTraversalExpression() {
        var histogram = testSupport.v.histogram().apply(testSupport.sampleTraversalExpression());

        assertEquals(testSupport.expectedTraversalHistogramCounts(), testSupport.histogramCounts(histogram));
    }

    @Test
    void collectClassNamesVisitorTraversesExpressionInPreOrder() {
        var classNames = testSupport.v.collectClassNamesVisitor().apply(testSupport.sampleTraversalExpression());

        assertEquals(testSupport.expectedTraversalClassNames(), classNames);
    }

    @Test
    void handleStateExposesGetterSetterAndInitialValue() {
        var values = testSupport.v.handleState(new StateConsumer<E, Integer, List<Integer>>() {
            @Override
            public <S> List<Integer> consume(State<E, S, Integer> state) {
                var counts = state.intial(0);
                state.setter(counts, 5).accept(factory.literal("1"));
                state.setter(counts, 2).accept(factory.addition(factory.literal("1"), factory.literal("2")));

                return List.of(
                    state.getter(counts).apply(factory.literal("9")),
                    state.getter(counts).apply(factory.addition(factory.literal("3"), factory.literal("4"))),
                    state.getter(counts).apply(factory.variableReference("x"))
                );
            }
        });

        assertEquals(of(5, 2, 0), values);
    }
}

class HandlerTraversalHelpersTest extends HandlerTraversalHelpersTestBase<ExpressionV1> {
    HandlerTraversalHelpersTest() {
        super(new TestSupport<>(HandlerTestFixtures.v1Handler()));
    }
}