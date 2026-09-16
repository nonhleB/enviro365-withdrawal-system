import { useEffect, useState } from 'react';
import { getWithdrawalHistory, getCsvExportUrl } from '../services/api';

const currency = (value) =>
  new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(value);

const STATUS_LABELS = {
  APPROVED: 'Approved',
  PENDING: 'Pending',
  REJECTED: 'Rejected',
};

export default function WithdrawalHistory({ portfolio, investorId, refreshKey }) {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!portfolio) {
      setHistory([]);
      return;
    }
    setLoading(true);
    setError(null);
    getWithdrawalHistory(portfolio.portfolioId)
      .then(setHistory)
      .catch((err) => setError(err.response?.data?.message || err.message))
      .finally(() => setLoading(false));
  }, [portfolio, refreshKey]);

  return (
    <section className="history">
      <div className="history__header">
        <h2>Withdrawal history</h2>
        {investorId && (
          <a
            className="history__export"
            href={getCsvExportUrl(investorId)}
            download
          >
            Download CSV statement
          </a>
        )}
      </div>

      {!portfolio && <p className="dashboard-status">Select a portfolio to see its withdrawal history.</p>}

      {portfolio && loading && <p className="dashboard-status">Loading history…</p>}

      {portfolio && error && (
        <p className="dashboard-status dashboard-status--error">Couldn't load history: {error}</p>
      )}

      {portfolio && !loading && !error && history.length === 0 && (
        <p className="dashboard-status">No withdrawals yet for this portfolio.</p>
      )}

      {portfolio && !loading && !error && history.length > 0 && (
        <table className="history__table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Amount</th>
              <th>Status</th>
              <th>Resulting balance</th>
            </tr>
          </thead>
          <tbody>
            {history
              .slice()
              .sort((a, b) => new Date(b.noticeDate) - new Date(a.noticeDate))
              .map((h) => (
                <tr key={h.id}>
                  <td>{h.noticeDate}</td>
                  <td>{currency(h.amount)}</td>
                  <td>
                    <span className={`status-pill status-pill--${h.status.toLowerCase()}`}>
                      {STATUS_LABELS[h.status] ?? h.status}
                    </span>
                  </td>
                  <td>{h.resultingBalance != null ? currency(h.resultingBalance) : '—'}</td>
                </tr>
              ))}
          </tbody>
        </table>
      )}
    </section>
  );
}
