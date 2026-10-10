const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api';

export interface ExpressionResponse {
  result: string;
  expression: string;
}

interface ErrorResponse {
  message: string;
}

export async function evaluate(expression: string): Promise<ExpressionResponse> {
  const response = await fetch(`${API_BASE}/evaluate`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ expression }),
  });

  const data: ExpressionResponse | ErrorResponse = await response.json();

  if (!response.ok) {
    throw new Error((data as ErrorResponse).message || 'Evaluation failed');
  }

  return data as ExpressionResponse;
}
