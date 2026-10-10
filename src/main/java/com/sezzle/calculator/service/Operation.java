package com.sezzle.calculator.service;

public interface Operation {

    double execute(double operand1, Double operand2);

    void validate(double operand1, Double operand2);
}
