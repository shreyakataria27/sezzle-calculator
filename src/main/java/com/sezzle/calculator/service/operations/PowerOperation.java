package com.sezzle.calculator.service.operations;

import com.sezzle.calculator.service.Operation;
import org.springframework.stereotype.Component;

@Component
public class PowerOperation implements Operation {

    @Override
    public double execute(double operand1, Double operand2) {
        return Math.pow(operand1, operand2);
    }

    @Override
    public void validate(double operand1, Double operand2) {
    }
}
