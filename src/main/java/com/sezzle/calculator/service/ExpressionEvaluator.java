package com.sezzle.calculator.service;

import com.sezzle.calculator.exception.CalculatorException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExpressionEvaluator {

    private static final Map<String, Integer> PRECEDENCE = Map.of(
            "+", 1,
            "-", 1,
            "*", 2,
            "/", 2,
            "%", 2,
            "^", 3
    );

    private static final Set<String> RIGHT_ASSOCIATIVE = Set.of("^");

    private final OperationRegistry registry;

    public ExpressionEvaluator(OperationRegistry registry) {
        this.registry = registry;
    }

    public double evaluate(String expression) {
        List<String> tokens = tokenize(expression);
        List<String> postfix = toPostfix(tokens);
        return evaluatePostfix(postfix);
    }

    List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        String expr = expression.replaceAll("\\s+", "");
        int i = 0;

        while (i < expr.length()) {
            char c = expr.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                int start = i;
                tokens.add(parseNumber(expr, start));
                i = advancePastNumber(expr, start);
            } else if (Character.isLetter(c)) {
                StringBuilder func = new StringBuilder();
                while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
                    func.append(expr.charAt(i));
                    i++;
                }
                String name = func.toString().toLowerCase();
                if (!registry.functionNames().contains(name)) {
                    throw new CalculatorException("Unknown function: " + name);
                }
                tokens.add(name);
            } else if (c == '(' || c == ')') {
                tokens.add(String.valueOf(c));
                i++;
            } else if (c == '-') {
                boolean isUnary = tokens.isEmpty()
                        || tokens.getLast().equals("(")
                        || PRECEDENCE.containsKey(tokens.getLast());
                if (isUnary) {
                    i++;
                    if (i >= expr.length() || (!Character.isDigit(expr.charAt(i)) && expr.charAt(i) != '.')) {
                        throw new CalculatorException("Invalid expression: unexpected '-'");
                    }
                    int start = i;
                    tokens.add("-" + parseNumber(expr, start));
                    i = advancePastNumber(expr, start);
                } else {
                    tokens.add("-");
                    i++;
                }
            } else if (PRECEDENCE.containsKey(String.valueOf(c))) {
                tokens.add(String.valueOf(c));
                i++;
            } else {
                throw new CalculatorException("Invalid character: '" + c + "'");
            }
        }

        if (tokens.isEmpty()) {
            throw new CalculatorException("Expression is empty");
        }
        return tokens;
    }

    List<String> toPostfix(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();

        for (String token : tokens) {
            if (isNumber(token)) {
                output.add(token);
            } else if (registry.functionNames().contains(token)) {
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                if (stack.isEmpty()) {
                    throw new CalculatorException("Mismatched parentheses");
                }
                stack.pop();
                if (!stack.isEmpty() && registry.functionNames().contains(stack.peek())) {
                    output.add(stack.pop());
                }
            } else if (PRECEDENCE.containsKey(token)) {
                while (!stack.isEmpty()
                        && !stack.peek().equals("(")
                        && PRECEDENCE.containsKey(stack.peek())
                        && (PRECEDENCE.get(stack.peek()) > PRECEDENCE.get(token)
                            || (PRECEDENCE.get(stack.peek()).equals(PRECEDENCE.get(token))
                                && !RIGHT_ASSOCIATIVE.contains(token)))) {
                    output.add(stack.pop());
                }
                stack.push(token);
            }
        }

        while (!stack.isEmpty()) {
            String top = stack.pop();
            if (top.equals("(")) {
                throw new CalculatorException("Mismatched parentheses");
            }
            output.add(top);
        }
        return output;
    }

    private double evaluatePostfix(List<String> postfix) {
        Deque<Double> stack = new ArrayDeque<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (registry.functionNames().contains(token)) {
                if (stack.isEmpty()) {
                    throw new CalculatorException("Invalid expression");
                }
                double a = stack.pop();
                Operation op = registry.getFunction(token);
                op.validate(a, null);
                double result = op.execute(a, null);
                stack.push(result);
            } else if (PRECEDENCE.containsKey(token)) {
                if (stack.size() < 2) {
                    throw new CalculatorException("Invalid expression");
                }
                double b = stack.pop();
                double a = stack.pop();
                Operation op = registry.getOperator(token);
                op.validate(a, b);
                double result = op.execute(a, b);
                stack.push(result);
            }
        }

        if (stack.size() != 1) {
            throw new CalculatorException("Invalid expression");
        }
        return stack.pop();
    }

    private String parseNumber(String expr, int start) {
        StringBuilder num = new StringBuilder();
        boolean hasDecimal = false;
        int i = start;
        while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
            if (expr.charAt(i) == '.') {
                if (hasDecimal) {
                    throw new CalculatorException("Invalid number: multiple decimal points");
                }
                hasDecimal = true;
            }
            num.append(expr.charAt(i));
            i++;
        }
        return num.toString();
    }

    private int advancePastNumber(String expr, int start) {
        int i = start;
        while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
            i++;
        }
        return i;
    }

    private boolean isNumber(String token) {
        if (token == null || token.isEmpty()) return false;
        int start = token.charAt(0) == '-' ? 1 : 0;
        if (start >= token.length()) return false;
        boolean hasDecimal = false;
        for (int i = start; i < token.length(); i++) {
            char c = token.charAt(i);
            if (c == '.') {
                if (hasDecimal) return false;
                hasDecimal = true;
            } else if (!Character.isDigit(c)) {
                return false;
            }
        }
        return start < token.length() && token.charAt(token.length() - 1) != '.';
    }
}
