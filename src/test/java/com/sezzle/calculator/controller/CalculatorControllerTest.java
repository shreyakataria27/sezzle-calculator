package com.sezzle.calculator.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void evaluateSimpleExpression() throws Exception {
        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\": \"3 + 4 * 2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("11"))
                .andExpect(jsonPath("$.expression").value("3 + 4 * 2 = 11"));
    }

    @Test
    void evaluateWithBrackets() throws Exception {
        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\": \"(3 + 4) * 2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("14"))
                .andExpect(jsonPath("$.expression").value("(3 + 4) * 2 = 14"));
    }

    @Test
    void evaluateWithSqrt() throws Exception {
        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\": \"sqrt(16) + 2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("6"));
    }

    @Test
    void evaluateDivisionByZeroReturnsUndefined() throws Exception {
        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\": \"10 / 0\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Undefined"));
    }

    @Test
    void evaluateSqrtNegativeReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\": \"sqrt(-4)\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Square root of a negative number is not allowed"));
    }

    @Test
    void evaluateMismatchedParensReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\": \"(3 + 4\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Mismatched parentheses"));
    }

    @Test
    void evaluateEmptyExpressionReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void evaluateMissingExpressionReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void evaluateDecimalResult() throws Exception {
        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\": \"10 / 4\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("2.5"));
    }

    @Test
    void evaluateChainedExpression() throws Exception {
        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\": \"3 + 4 - 2 + 5\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("10"));
    }

    @Test
    void evaluateExpressionTooLongReturnsBadRequest() throws Exception {
        String longExpr = "1+" .repeat(501);
        mockMvc.perform(post("/api/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"expression\": \"" + longExpr + "1\"}"))
                .andExpect(status().isBadRequest());
    }
}
