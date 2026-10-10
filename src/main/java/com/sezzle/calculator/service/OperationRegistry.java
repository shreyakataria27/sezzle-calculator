package com.sezzle.calculator.service;

import com.sezzle.calculator.exception.CalculatorException;
import com.sezzle.calculator.service.operations.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class OperationRegistry {

    private final Map<String, Operation> operators;
    private final Map<String, Operation> functions;

    public OperationRegistry(
            AddOperation add,
            SubtractOperation subtract,
            MultiplyOperation multiply,
            DivideOperation divide,
            PowerOperation power,
            PercentageOperation percentage,
            SqrtOperation sqrt
    ) {
        this.operators = Map.of(
                "+", add,
                "-", subtract,
                "*", multiply,
                "/", divide,
                "^", power,
                "%", percentage
        );
        this.functions = Map.of(
                "sqrt", sqrt
        );
    }

    public Operation getOperator(String symbol) {
        Operation op = operators.get(symbol);
        if (op == null) {
            throw new CalculatorException("Unknown operator: " + symbol);
        }
        return op;
    }

    public Operation getFunction(String name) {
        Operation op = functions.get(name);
        if (op == null) {
            throw new CalculatorException("Unknown function: " + name);
        }
        return op;
    }

    public Set<String> operatorSymbols() {
        return operators.keySet();
    }

    public Set<String> functionNames() {
        return functions.keySet();
    }
}
