interface HistoryProps {
  entries: string[];
  onClear: () => void;
  onClose: () => void;
}

function History({ entries, onClear, onClose }: HistoryProps) {
  return (
    <div className="history">
      <div className="history-header">
        <span>History</span>
        <div className="history-actions">
          {entries.length > 0 && (
            <button className="history-action-btn" onClick={onClear}>Clear</button>
          )}
          <button className="history-action-btn" onClick={onClose}>Close</button>
        </div>
      </div>
      {entries.length === 0 ? (
        <div className="history-empty">No calculations yet</div>
      ) : (
        <ul className="history-list">
          {entries.map((entry, i) => (
            <li key={i} className="history-entry">{entry}</li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default History;
