interface HistoryProps {
  entries: string[];
  onClear: () => void;
}

function History({ entries, onClear }: HistoryProps) {
  if (entries.length === 0) return null;

  return (
    <div className="history">
      <div className="history-header">
        <span>History</span>
        <button className="history-clear" onClick={onClear}>Clear</button>
      </div>
      <ul className="history-list">
        {entries.map((entry, i) => (
          <li key={i} className="history-entry">{entry}</li>
        ))}
      </ul>
    </div>
  );
}

export default History;
