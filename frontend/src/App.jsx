import { useEffect, useState, useCallback } from 'react';
import InvestorSelector from './components/InvestorSelector';
import PortfolioDashboard from './components/PortfolioDashboard';
import WithdrawalForm from './components/WithdrawalForm';
import { getPortfolios } from './services/api';
import './App.css';

export default function App() {
  const [investorId, setInvestorId] = useState(1);
  const [portfolios, setPortfolios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);

  const loadPortfolios = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getPortfolios(investorId);
      setPortfolios(data);
      // Keep the selected portfolio's balance in sync after a withdrawal,
      // or clear the selection if the investor changed.
      setSelectedPortfolio((prev) => data.find((p) => p.portfolioId === prev?.portfolioId) ?? null);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  }, [investorId]);

  useEffect(() => {
    loadPortfolios();
  }, [loadPortfolios]);

  return (
    <div className="app">
      <header className="app__header">
        <div>
          <p className="app__eyebrow">Enviro365 Investments</p>
          <h1>Withdrawal Notices</h1>
        </div>
        <InvestorSelector selectedId={investorId} onChange={setInvestorId} />
      </header>

      <main className="app__main">
        <section>
          <h2>Portfolio dashboard</h2>
          <PortfolioDashboard
            portfolios={portfolios}
            loading={loading}
            error={error}
            onSelectPortfolio={setSelectedPortfolio}
            selectedPortfolioId={selectedPortfolio?.portfolioId}
          />
        </section>

        <section>
          <WithdrawalForm portfolio={selectedPortfolio} onWithdrawalCreated={loadPortfolios} />
        </section>
      </main>
    </div>
  );
}
