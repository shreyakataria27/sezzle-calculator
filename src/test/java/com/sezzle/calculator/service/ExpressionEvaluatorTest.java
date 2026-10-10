package com.sezzle.calculator.service;

import com.sezzle.calculator.exception.CalculatorException;
import com.sezzle.calculator.service.operations.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionEvaluatorTest {

    private ExpressionEvaluator evaluator;

    @BeforeEach
    void setUp() {
        OperationRegistry registry = new OperationRegistry(
                new AddOperation(),
                new SubtractOperation(),
                new MultiplyOperation(),
                new DivideOperation(),
                new PowerOperation(),
                new PercentageOperation(),
                new SqrtOperation()
        );
        evaluator = new ExpressionEvaluator(registry);
    }

    @Test
    void simpleAddition() {
        assertEquals(8.0, evaluator.evaluate("3 + 5"));
    }

    @Test
    void simpleSubtraction() {
        assertEquals(7.0, evaluator.evaluate("10 - 3"));
    }

    @Test
    void simpleMultiplication() {
        assertEquals(42.0, evaluator.evaluate("6 * 7"));
    }

    @Test
    void simpleDivision() {
        assertEquals(5.0, evaluator.evaluate("15 / 3"));
    }

    @Test
    void bodmasMultiplicationBeforeAddition() {
        assertEquals(11.0, evaluator.evaluate("3 + 4 * 2"));
    }

    @Test
    void bodmasDivisionBeforeSubtraction() {
        assertEquals(5.0, evaluator.evaluate("10 - 10 / 2"));
    }

    @Test
    void bodmasComplexExpression() {
        assertEquals(14.0, evaluator.evaluate("2 + 3 * 4"));
    }

    @Test
    void bracketsOverridePrecedence() {
        assertEquals(14.0, evaluator.evaluate("(3 + 4) * 2"));
    }

    @Test
    void nestedBrackets() {
        assertEquals(20.0, evaluator.evaluate("((2 + 3) * (1 + 3))"));
    }

    @Test
    void exponentiation() {
        assertEquals(8.0, evaluator.evaluate("2 ^ 3"));
    }

    @Test
    void exponentiationRightAssociative() {
        assertEquals(512.0, evaluator.evaluate("2 ^ 3 ^ 2"));
    }

    @Test
    void sqrtFunction() {
        assertEquals(4.0, evaluator.evaluate("sqrt(16)"));
    }

    @Test
    void sqrtInExpression() {
        assertEquals(7.0, evaluator.evaluate("3 + sqrt(16)"));
    }

    @Test
    void percentage() {
        assertEquals(30.0, evaluator.evaluate("200 % 15"));
    }

    @Test
    void chainedAdditionSubtraction() {
        assertEquals(12.0, evaluator.evaluate("3 + 4 - 2 + 7"));
    }

    @Test
    void chainedWithPrecedence() {
        assertEquals(16.0, evaluator.evaluate("3 + 4 * 2 + 5"));
    }

    @Test
    void negativeNumber() {
        assertEquals(-2.0, evaluator.evaluate("-5 + 3"));
    }

    @Test
    void negativeNumberInBrackets() {
        assertEquals(6.0, evaluator.evaluate("(-2 + 5) * 2"));
    }

    @Test
    void decimalNumbers() {
        assertEquals(5.5, evaluator.evaluate("2.5 + 3"));
    }

    @Test
    void singleNumber() {
        assertEquals(42.0, evaluator.evaluate("42"));
    }

    @Test
    void divisionByZeroReturnsInfinity() {
        assertEquals(Double.POSITIVE_INFINITY, evaluator.evaluate("10 / 0"));
    }

    @Test
    void zeroDividedByZeroReturnsNaN() {
        assertTrue(Double.isNaN(evaluator.evaluate("0 / 0")));
    }

    @Test
    void multipleDecimalPointsThrows() {
        assertThrows(CalculatorException.class, () -> evaluator.evaluate("1.2.3 + 1"));
    }

    @Test
    void sqrtNegativeThrows() {
        CalculatorException ex = assertThrows(CalculatorException.class, () -> evaluator.evaluate("sqrt(-4)"));
        assertEquals("Square root of a negative number is not allowed", ex.getMessage());
    }

    @Test
    void mismatchedOpenParenThrows() {
        assertThrows(CalculatorException.class, () -> evaluator.evaluate("(3 + 4"));
    }

    @Test
    void mismatchedCloseParenThrows() {
        assertThrows(CalculatorException.class, () -> evaluator.evaluate("3 + 4)"));
    }

    @Test
    void emptyExpressionThrows() {
        assertThrows(CalculatorException.class, () -> evaluator.evaluate(""));
    }

    @Test
    void unknownFunctionThrows() {
        assertThrows(CalculatorException.class, () -> evaluator.evaluate("foo(5)"));
    }

    @Test
    void invalidCharacterThrows() {
        assertThrows(CalculatorException.class, () -> evaluator.evaluate("3 & 4"));
    }

    @ParameterizedTest
    @CsvSource({
            "'1 + 2 + 3 + 4', 10.0",
            "'10 - 2 * 3', 4.0",
            "'(10 - 2) * 3', 24.0",
            "'2 ^ 2 + 1', 5.0",
            "'sqrt(9) * 3', 9.0",
            "'100 / 4 / 5', 5.0",
    })
    void parameterizedExpressions(String expression, double expected) {
        assertEquals(expected, evaluator.evaluate(expression), 1e-10);
    }
}
