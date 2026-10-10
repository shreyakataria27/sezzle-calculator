# Sezzle Calculator

A full-stack calculator application with a Java Spring Boot backend and a React TypeScript frontend. Supports full mathematical expressions with BODMAS operator precedence and bracket support.

## Features

- Full expression input with BODMAS evaluation (e.g., `3 + 4 * 2 = 11`)
- Bracket support for overriding precedence (e.g., `(3 + 4) * 2 = 14`)
- Keyboard and mouse input with visual button feedback
- Calculation history panel
- Responsive design with mobile support
- Auto-closing of unclosed parentheses

## Supported Operations

| Operation | Symbol | Example |
|---|---|---|
| Addition | `+` | `5 + 3 = 8` |
| Subtraction | `-` | `10 - 4 = 6` |
| Multiplication | `*` | `6 * 7 = 42` |
| Division | `/` | `15 / 4 = 3.75` |
| Exponentiation | `^` | `2 ^ 3 = 8` |
| Square Root | `sqrt()` | `sqrt(16) = 4` |
| Percentage | `%` | `200 % 15 = 30` (15% of 200) |
| Brackets | `( )` | `(3 + 4) * 2 = 14` |

## Prerequisites

- **Java 21+**
- **Maven 3.9+**
- **Node.js 18+** and **npm**

## Project Structure

```
sezzle/
├── pom.xml
├── src/main/java/com/sezzle/calculator/
│   ├── CalculatorApplication.java        # Spring Boot entry point
│   ├── config/
│   │   └── CorsConfig.java              # CORS configuration (externalized)
│   ├── controller/
│   │   └── CalculatorController.java     # REST endpoints
│   ├── service/
│   │   ├── ExpressionEvaluator.java      # Shunting Yard parser and evaluator
│   │   ├── Operation.java                # Operation interface (Strategy pattern)
│   │   ├── OperationRegistry.java        # Maps symbols to Operation implementations
│   │   └── operations/                   # One class per operation
│   │       ├── AddOperation.java
│   │       ├── SubtractOperation.java
│   │       ├── MultiplyOperation.java
│   │       ├── DivideOperation.java
│   │       ├── PowerOperation.java
│   │       ├── SqrtOperation.java
│   │       └── PercentageOperation.java
│   ├── dto/                              # Request/response records
│   └── exception/                        # Global error handling
├── src/test/java/                        # Unit and integration tests
├── frontend/
│   ├── package.json
│   ├── tsconfig.json
│   ├── src/
│   │   ├── App.tsx
│   │   ├── App.css
│   │   ├── components/
│   │   │   ├── Calculator.tsx            # Main calculator with expression builder
│   │   │   ├── Display.tsx               # Result and expression display
│   │   │   ├── Button.tsx                # Reusable button with active state
│   │   │   └── History.tsx               # Calculation history panel
│   │   └── services/
│   │       └── api.ts                    # Backend API client
│   └── index.html
```

## Setup and Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd sezzle
```

### 2. Verify Prerequisites

```bash
java -version    # Should be 21+
mvn --version    # Should be 3.9+
node --version   # Should be 18+
npm --version
```

### 3. Build and Test the Backend

```bash
# From the project root
mvn clean test
```

This downloads dependencies, compiles the code, and runs all backend tests. First run may take a few minutes.

### 4. Install Frontend Dependencies

```bash
cd frontend
npm install
```

### 5. Start the Backend (port 8080)

```bash
# From the project root (in terminal 1)
mvn spring-boot:run
```

Wait until you see `Started CalculatorApplication` in the output.

### 6. Start the Frontend (port 5173)

```bash
# From the frontend directory (in terminal 2)
cd frontend
npm run dev
```

### 7. Open the App

Navigate to [http://localhost:5173](http://localhost:5173) in your browser.

> Both services must be running simultaneously in separate terminals.

### Verify It Works

```bash
# Health check
curl http://localhost:8080/api/health

# Test a calculation
curl -X POST http://localhost:8080/api/evaluate \
  -H 'Content-Type: application/json' \
  -d '{"expression": "3 + 4 * 2"}'
```

## Configuration

### Backend

Configuration is in `src/main/resources/application.properties`:

| Property | Default | Description |
|---|---|---|
| `server.port` | `8080` | Backend server port |
| `cors.allowed-origins` | `http://localhost:5173` | Comma-separated allowed CORS origins |

### Frontend

Set the `VITE_API_BASE` environment variable to override the backend URL (defaults to `http://localhost:8080/api`):

```bash
VITE_API_BASE=https://api.example.com npm run dev
```

## Keyboard Shortcuts

| Key | Action |
|---|---|
| `0`-`9` | Enter digit |
| `.` | Decimal point |
| `+` `-` `*` `/` `^` `%` | Operators |
| `(` `)` | Brackets |
| `s` | Square root — inserts `sqrt(` |
| `Enter` or `=` | Evaluate expression |
| `Escape` or `Delete` | Clear |
| `Backspace` | Delete last character |

## Running Tests

### Backend

```bash
# Run all tests
mvn clean test

# Tests with coverage report (generated at target/site/jacoco/index.html)
mvn clean test
```

### TypeScript Type Check

```bash
cd frontend
npx tsc --noEmit
```

## API Reference

### Health Check

```
GET /api/health
```

Returns `OK` if the service is running.

### Evaluate Expression

```
POST /api/evaluate
Content-Type: application/json
```

**Request body:**

```json
{
  "expression": "3 + 4 * 2"
}
```

- `expression` — required, max 1000 characters
- Supports: numbers, operators (`+`, `-`, `*`, `/`, `^`, `%`), brackets, and `sqrt()`
- Follows BODMAS operator precedence

**Success response (200):**

```json
{
  "result": "11",
  "expression": "3 + 4 * 2 = 11"
}
```

**Error response (400):**

```json
{
  "status": 400,
  "message": "Mismatched parentheses",
  "errors": [],
  "timestamp": "2026-10-10T07:00:00.000Z"
}
```

## Architecture

### Backend Design Patterns

- **Strategy Pattern** — each operation (`Add`, `Divide`, `Sqrt`, etc.) implements the `Operation` interface
- **Factory/Registry** — `OperationRegistry` maps operator symbols and function names to their implementations
- **Shunting Yard Algorithm** — `ExpressionEvaluator` parses infix expressions to postfix, then evaluates with correct BODMAS precedence

### Adding a New Operation

1. Create a new class implementing `Operation` with `@Component` (e.g., `LogOperation.java`)
2. Register its symbol/function name in `OperationRegistry`
3. If it's a function (like `sqrt`), no other backend changes needed
4. Add a button in `Calculator.tsx`

### Error Handling

- `CalculatorException` — business logic errors (400)
- `MethodArgumentNotValidException` — validation errors (400)
- `HttpMessageNotReadableException` — malformed JSON (400)
- Catch-all `Exception` handler — unexpected errors (500, logged)

### Input Validation

- Expression length capped at 1000 characters
- Numbers must have at most one decimal point
- Square root of negative numbers returns an error
- Division by zero returns `Undefined`
- Mismatched parentheses are detected and reported
