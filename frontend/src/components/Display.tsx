interface DisplayProps {
  expression: string;
  value: string;
  error: string;
}

function Display({ expression, value, error }: DisplayProps) {
  return (
    <div className="display">
      <div className={`display-result ${error ? 'display-error' : ''}`}>
        {error || value || '0'}
      </div>
      <div className="display-expression">{expression || ' '}</div>
    </div>
  );
}

export default Display;
