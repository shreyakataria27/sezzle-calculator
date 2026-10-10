package com.sezzle.calculator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ExpressionRequest(
        @NotBlank(message = "Expression is required")
        @Size(max = 1000, message = "Expression must not exceed 1000 characters")
        String expression
) {
}
