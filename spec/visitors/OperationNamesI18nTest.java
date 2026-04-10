package spec.visitors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import lib.dict.OperationNamesI18n;
import lib.expression.ExpressionV1;
import port.IExpressionFactory;
import port.IHandlerFactory1;
import testsupport.HandlerTestFixtures;

class OperationNamesI18nTest {
    private final IHandlerFactory1<ExpressionV1> handler = HandlerTestFixtures.v1Handler();
    private final IExpressionFactory<ExpressionV1> factory = handler.expressionFactory();

    @Test
    void providesI18nDictionariesForSeveralLanguages() {
        assertNotNull(OperationNamesI18n.operationNamesByLanguage().get("en"));
        assertNotNull(OperationNamesI18n.operationNamesByLanguage().get("es"));
        assertNotNull(OperationNamesI18n.operationNamesByLanguage().get("fr"));
        assertNotNull(OperationNamesI18n.operationNamesByLanguage().get("de"));
        assertNotNull(OperationNamesI18n.operationNamesByLanguage().get("it"));
        assertNotNull(OperationNamesI18n.operationNamesByLanguage().get("pt"));
    }

    @Test
    void exposesTranslatedOperationNamesThroughGenericDictReader() {
        var english = handler.dictReader(OperationNamesI18n.operationNamesByLanguage().get("en"));
        var spanish = handler.dictReader(OperationNamesI18n.operationNamesByLanguage().get("es"));

        assertEquals("addition", english.apply(factory.addition(factory.literal("1"), factory.literal("2"))));
        assertEquals("functionCall", english.apply(factory.functionCall(factory.variableReference("f"), java.util.List.of(factory.literal("1")))));
        assertEquals("suma", spanish.apply(factory.addition(factory.literal("1"), factory.literal("2"))));
        assertEquals("llamadaFuncion", spanish.apply(factory.functionCall(factory.variableReference("f"), java.util.List.of(factory.literal("1")))));
        assertEquals("negacionLogica", spanish.apply(factory.logicalNot(factory.variableReference("x"))));
    }

    @Test
    void returnsNullForUnknownLanguage() {
        assertNull(OperationNamesI18n.operationNamesByLanguage().get("jp"));
    }
}