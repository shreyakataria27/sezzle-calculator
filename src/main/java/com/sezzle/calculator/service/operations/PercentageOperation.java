package com.sezzle.calculator.service.operations;

import com.sezzle.calculator.exception.CalculatorException;
import com.sezzle.calculator.service.Operation;
import org.springframework.stereotype.Component;

@Component
public class PercentageOperation implements Operation {

    @Override
    public double execute(double operand1, Double operand2) {
        return operand1 * operand2 / 100.0;
    }

    @Override
    public void validate(double operand1, Double operand2) {
        if (operand2 != null && operand2 < 0) {
            throw new CalculatorException("Percentage value cannot be negative");
        }
    }
}
