import { useState, useCallback, useEffect } from 'react';
import { evaluate } from '../services/api';
import Display from './Display';
import Button from './Button';
import History from './History';

const OPERATOR_MAP: Record<string, string> = {
  '+': '+',
  '−': '-',
  '×': '*',
  '÷': '/',
  '^': '^',
  '%': '%',
};

const KEY_TO_OP: Record<string, string> = {
  '+': '+',
  '-': '−',
  '*': '×',
  '/': '÷',
  '^': '^',
  '%': '%',
};

const KEY_TO_LABEL: Record<string, string> = {
  '*': '×',
  '/': '÷',
  '-': '−',
  Enter: '=',
  Escape: 'C',
  Delete: 'C',
  s: '√',
  '(': '(',
  ')': ')',
};

function Calculator() {
  const [expression, setExpression] = useState('0');
  const [error, setError] = useState('');
  const [history, setHistory] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [activeKey, setActiveKey] = useState<string | null>(null);
  const [showHistory, setShowHistory] = useState(false);
  const [justEvaluated, setJustEvaluated] = useState(false);

  const [showLastEntry, setShowLastEntry] = useState(true);

  const reset = useCallback(() => {
    setExpression('0');
    setError('');
    setShowLastEntry(false);
    setJustEvaluated(false);
  }, []);

  const appendToExpression = useCallback((value: string) => {
    setError('');
    if (justEvaluated) {
      setExpression(value);
        setJustEvaluated(false);
    } else {
      setExpression((prev) => (prev === '0' ? value : prev + value));
    }
  }, [justEvaluated]);

  const hasMaxDecimals = useCallback((expr: string) => {
    const lastNumber = expr.split(/[+\-*\/^%()]/).pop() || '';
    const dotIndex = lastNumber.indexOf('.');
    return dotIndex !== -1 && lastNumber.length - dotIndex > 7;
  }, []);

  const handleDigit = useCallback((digit: string) => {
    if (!justEvaluated && hasMaxDecimals(expression)) return;
    appendToExpression(digit);
  }, [appendToExpression, justEvaluated, expression, hasMaxDecimals]);

  const handleDecimal = useCallback(() => {
    appendToExpression('.');
  }, [appendToExpression]);

  const handleOperator = useCallback((displayOp: string) => {
    setError('');
    const apiOp = OPERATOR_MAP[displayOp] ?? displayOp;
    setExpression((prev) => prev + apiOp);
    setJustEvaluated(false);
  }, []);

  const handleBracket = useCallback((bracket: string) => {
    setError('');
    if (justEvaluated) {
      setExpression(bracket);
        setJustEvaluated(false);
    } else {
      setExpression((prev) => (prev === '0' ? bracket : prev + bracket));
    }
  }, [justEvaluated]);

  const handleSqrt = useCallback(() => {
    setError('');
    if (justEvaluated) {
      setExpression('sqrt(');
        setJustEvaluated(false);
    } else {
      setExpression((prev) => (prev === '0' ? 'sqrt(' : prev + 'sqrt('));
    }
  }, [justEvaluated]);

  const handleBackspace = useCallback(() => {
    setError('');
    if (justEvaluated) return;
    setExpression((prev) => prev.slice(0, -1));
  }, [justEvaluated]);

  const handleEquals = useCallback(async () => {
    if (!expression.trim() || expression === '0') return;

    let expr = expression;
    const openCount = (expr.match(/\(/g) || []).length;
    const closeCount = (expr.match(/\)/g) || []).length;
    if (openCount > closeCount) {
      expr += ')'.repeat(openCount - closeCount);
      setExpression(expr);
    }

    setError('');
    setLoading(true);
    try {
      const response = await evaluate(expr);
      setHistory((prev) => [response.expression, ...prev].slice(0, 20));
      setExpression(String(response.result));
      setShowLastEntry(true);
      setJustEvaluated(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Evaluation failed');
    } finally {
      setLoading(false);
    }
  }, [expression]);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      const key = e.key;
      const label = KEY_TO_LABEL[key] ?? key;
      setActiveKey(label);

      if (key >= '0' && key <= '9') {
        e.preventDefault();
        handleDigit(key);
      } else if (key === '.') {
        e.preventDefault();
        handleDecimal();
      } else if (key in KEY_TO_OP) {
        e.preventDefault();
        handleOperator(KEY_TO_OP[key]!);
      } else if (key === 'Enter' || key === '=') {
        e.preventDefault();
        handleEquals();
      } else if (key === 'Escape' || key === 'Delete') {
        e.preventDefault();
        reset();
      } else if (key === 'Backspace') {
        e.preventDefault();
        handleBackspace();
      } else if (key === 's') {
        e.preventDefault();
        handleSqrt();
      } else if (key === '(' || key === ')') {
        e.preventDefault();
        handleBracket(key);
      }
    };

    const handleKeyUp = () => {
      setActiveKey(null);
    };

    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('keyup', handleKeyUp);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      window.removeEventListener('keyup', handleKeyUp);
    };
  }, [handleDigit, handleDecimal, handleOperator, handleEquals, reset, handleSqrt, handleBackspace, handleBracket]);

  const displayExpression = expression
    .replace(/sqrt/g, '√')
    .replace(/\*/g, '×')
    .replace(/\//g, '÷')
    .replace(/-/g, '−');

  return (
    <div className="calculator-wrapper">
      <div className="calculator">
        <div className="calculator-toolbar">
          <button
            className={`history-toggle ${showHistory ? 'history-toggle-active' : ''}`}
            onClick={() => setShowHistory((prev) => !prev)}
            title="Toggle history"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="12" cy="12" r="10" />
              <polyline points="12 6 12 12 16 14" />
            </svg>
            <span>History</span>
          </button>
        </div>
        <Display expression={displayExpression || ' '} error={error} lastEntry={showLastEntry ? (history[0] ?? '') : ''} />

        <div className="button-grid">
          <Button label="C" onClick={reset} className="btn-clear" isActive={activeKey === 'C'} />
          <Button label="(" onClick={() => handleBracket('(')} className="btn-func" isActive={activeKey === '('} />
          <Button label=")" onClick={() => handleBracket(')')} className="btn-func" isActive={activeKey === ')'} />
          <Button label="÷" onClick={() => handleOperator('÷')} className="btn-op" isActive={activeKey === '÷'} />

          <Button label="7" onClick={() => handleDigit('7')} isActive={activeKey === '7'} />
          <Button label="8" onClick={() => handleDigit('8')} isActive={activeKey === '8'} />
          <Button label="9" onClick={() => handleDigit('9')} isActive={activeKey === '9'} />
          <Button label="×" onClick={() => handleOperator('×')} className="btn-op" isActive={activeKey === '×'} />

          <Button label="4" onClick={() => handleDigit('4')} isActive={activeKey === '4'} />
          <Button label="5" onClick={() => handleDigit('5')} isActive={activeKey === '5'} />
          <Button label="6" onClick={() => handleDigit('6')} isActive={activeKey === '6'} />
          <Button label="−" onClick={() => handleOperator('−')} className="btn-op" isActive={activeKey === '−'} />

          <Button label="1" onClick={() => handleDigit('1')} isActive={activeKey === '1'} />
          <Button label="2" onClick={() => handleDigit('2')} isActive={activeKey === '2'} />
          <Button label="3" onClick={() => handleDigit('3')} isActive={activeKey === '3'} />
          <Button label="+" onClick={() => handleOperator('+')} className="btn-op" isActive={activeKey === '+'} />

          <Button label="0" onClick={() => handleDigit('0')} isActive={activeKey === '0'} />
          <Button label="." onClick={handleDecimal} isActive={activeKey === '.'} />
          <Button label="^" onClick={() => handleOperator('^')} className="btn-func" isActive={activeKey === '^'} />
          <Button label="√" onClick={handleSqrt} className="btn-func" isActive={activeKey === '√'} />

          <Button
            label={loading ? '...' : '='}
            onClick={handleEquals}
            className="btn-equals"
            isActive={activeKey === '='}
          />
        </div>
      </div>

      {showHistory && (
        <History
          entries={history}
          onClear={() => setHistory([])}
          onClose={() => setShowHistory(false)}
        />
      )}
    </div>
  );
}

export default Calculator;
