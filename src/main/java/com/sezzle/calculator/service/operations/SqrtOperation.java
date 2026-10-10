package com.sezzle.calculator.service.operations;

import com.sezzle.calculator.exception.CalculatorException;
import com.sezzle.calculator.service.Operation;
import org.springframework.stereotype.Component;

@Component
public class SqrtOperation implements Operation {

    @Override
    public double execute(double operand1, Double operand2) {
        return Math.sqrt(operand1);
    }

    @Override
    public void validate(double operand1, Double operand2) {
        if (operand1 < 0) {
            throw new CalculatorException("Square root of a negative number is not allowed");
        }
    }
}
