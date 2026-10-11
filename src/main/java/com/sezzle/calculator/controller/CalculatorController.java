package com.sezzle.calculator.controller;

import com.sezzle.calculator.dto.ExpressionRequest;
import com.sezzle.calculator.dto.ExpressionResponse;
import com.sezzle.calculator.service.ExpressionEvaluator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CalculatorController {

    private final ExpressionEvaluator expressionEvaluator;

    public CalculatorController(ExpressionEvaluator expressionEvaluator) {
        this.expressionEvaluator = expressionEvaluator;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<ExpressionResponse> evaluate(@Valid @RequestBody ExpressionRequest request) {
        double result = expressionEvaluator.evaluate(request.expression());
        String formatted = formatResult(result);
        String expression = request.expression() + " = " + formatted;
        return ResponseEntity.ok(new ExpressionResponse(formatted, expression));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    private String formatResult(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return "Undefined";
        if (value == Math.floor(value) && Math.abs(value) < Long.MAX_VALUE) {
            return String.valueOf((long) value);
        }
        String formatted = String.format("%.7f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
        return formatted;
    }
}
