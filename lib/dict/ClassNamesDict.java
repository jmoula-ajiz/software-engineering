package lib.dict;

import ds.Dict;

public class ClassNamesDict extends Dict<String> {

    public ClassNamesDict() {
        super(
            "Literal",
            "VariableReference",
            "Addition",
            "Subtraction",
            "Multiplication",
            "Division",
            "Negation",
            "Modulo",
            "Exponentiation",
            "Equality",
            "Inequality",
            "LessThan",
            "GreaterThan",
            "LessThanOrEqual",
            "GreaterThanOrEqual",
            "Conjunction",
            "Disjunction",
            "LogicalNot",
            "Conditional",
            "FunctionCall"
        );
    }
}