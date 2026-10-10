package com.sezzle.calculator.service.operations;

import com.sezzle.calculator.service.Operation;
import org.springframework.stereotype.Component;

@Component
public class SubtractOperation implements Operation {

    @Override
    public double execute(double operand1, Double operand2) {
        return operand1 - operand2;
    }

    @Override
    public void validate(double operand1, Double operand2) {
    }
}
