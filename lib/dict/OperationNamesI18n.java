package lib.dict;

import java.util.LinkedHashMap;
import java.util.Map;

import ds.Dict;

public final class OperationNamesI18n {
    private static final Map<String, Dict<String>> OPERATION_NAMES_BY_LANGUAGE = createOperationNamesByLanguage();

    private OperationNamesI18n() {
    }

    public static Map<String, Dict<String>> operationNamesByLanguage() {
        return OPERATION_NAMES_BY_LANGUAGE;
    }

    private static Map<String, Dict<String>> createOperationNamesByLanguage() {
        var languages = new LinkedHashMap<String, Dict<String>>();
        languages.put("en", english());
        languages.put("es", spanish());
        languages.put("fr", french());
        languages.put("de", german());
        languages.put("it", italian());
        languages.put("pt", portuguese());
        return Map.copyOf(languages);
    }

    private static Dict<String> english() {
        return new Dict<>(
            "literal",
            "variableReference",
            "addition",
            "subtraction",
            "multiplication",
            "division",
            "negation",
            "modulo",
            "exponentiation",
            "equality",
            "inequality",
            "lessThan",
            "greaterThan",
            "lessThanOrEqual",
            "greaterThanOrEqual",
            "conjunction",
            "disjunction",
            "logicalNot",
            "conditional",
            "functionCall"
        );
    }

    private static Dict<String> spanish() {
        return new Dict<>(
            "literal",
            "referenciaVariable",
            "suma",
            "resta",
            "multiplicacion",
            "division",
            "negacion",
            "modulo",
            "potencia",
            "igualdad",
            "desigualdad",
            "menorQue",
            "mayorQue",
            "menorOIgual",
            "mayorOIgual",
            "conjuncion",
            "disyuncion",
            "negacionLogica",
            "condicional",
            "llamadaFuncion"
        );
    }

    private static Dict<String> french() {
        return new Dict<>(
            "litteral",
            "referenceVariable",
            "addition",
            "soustraction",
            "multiplication",
            "division",
            "negation",
            "modulo",
            "exponentiation",
            "egalite",
            "inegalite",
            "inferieurA",
            "superieurA",
            "inferieurOuEgal",
            "superieurOuEgal",
            "conjonction",
            "disjonction",
            "nonLogique",
            "conditionnel",
            "appelDeFonction"
        );
    }

    private static Dict<String> german() {
        return new Dict<>(
            "literal",
            "variablenReferenz",
            "addition",
            "subtraktion",
            "multiplikation",
            "division",
            "negation",
            "modulo",
            "potenzierung",
            "gleichheit",
            "ungleichheit",
            "kleinerAls",
            "groesserAls",
            "kleinerOderGleich",
            "groesserOderGleich",
            "konjunktion",
            "disjunktion",
            "logischesNicht",
            "bedingung",
            "funktionsAufruf"
        );
    }

    private static Dict<String> italian() {
        return new Dict<>(
            "letterale",
            "riferimentoVariabile",
            "addizione",
            "sottrazione",
            "moltiplicazione",
            "divisione",
            "negazione",
            "modulo",
            "esponenziazione",
            "uguaglianza",
            "disuguaglianza",
            "minoreDi",
            "maggioreDi",
            "minoreOUguale",
            "maggioreOUguale",
            "congiunzione",
            "disgiunzione",
            "nonLogico",
            "condizionale",
            "chiamataFunzione"
        );
    }

    private static Dict<String> portuguese() {
        return new Dict<>(
            "literal",
            "referenciaVariavel",
            "adicao",
            "subtracao",
            "multiplicacao",
            "divisao",
            "negacao",
            "modulo",
            "exponenciacao",
            "igualdade",
            "desigualdade",
            "menorQue",
            "maiorQue",
            "menorOuIgual",
            "maiorOuIgual",
            "conjuncao",
            "disjuncao",
            "naoLogico",
            "condicional",
            "chamadaFuncao"
        );
    }
}