interface DisplayProps {
  expression: string;
  error: string;
  lastEntry: string;
}

function Display({ expression, error, lastEntry }: DisplayProps) {
  return (
    <div className="display">
      {lastEntry && (
        <div className="display-last-entry">{lastEntry}</div>
      )}
      <div className="display-current">
        {error && (
          <div className="display-error">{error}</div>
        )}
        <div className="display-expression">{expression || ' '}</div>
      </div>
    </div>
  );
}

export default Display;
