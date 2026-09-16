const currency = (value) =>
  new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(value);

const PRODUCT_LABELS = {
  RETIREMENT_ANNUITY: 'Retirement Annuity',
  UNIT_TRUST: 'Unit Trust',
  SAVINGS_PLAN: 'Savings Plan',
  ENDOWMENT: 'Endowment',
};

export default function PortfolioDashboard({ portfolios, loading, error, onSelectPortfolio, selectedPortfolioId }) {
  if (loading) {
    return <p className="dashboard-status">Loading portfolios…</p>;
  }

  if (error) {
    return <p className="dashboard-status dashboard-status--error">Couldn't load portfolios: {error}</p>;
  }

  if (portfolios.length === 0) {
    return <p className="dashboard-status">This investor has no portfolios yet.</p>;
  }

  return (
    <div className="portfolio-grid">
      {portfolios.map((p) => (
        <button
          key={p.portfolioId}
          className={`portfolio-card${p.portfolioId === selectedPortfolioId ? ' portfolio-card--selected' : ''}`}
          onClick={() => onSelectPortfolio(p)}
        >
          <span className="portfolio-card__type">{PRODUCT_LABELS[p.productType] ?? p.productType}</span>
          <span className="portfolio-card__name">{p.productName}</span>
          <span className="portfolio-card__balance">{currency(p.balance)}</span>
          <span className="portfolio-card__meta">
            {p.investorName} · age {p.investorAge}
            {p.productType === 'RETIREMENT_ANNUITY' && p.investorAge <= 65 && (
              <span className="portfolio-card__flag"> · withdrawals locked until 65</span>
            )}
          </span>
        </button>
      ))}
    </div>
  );
}
