import { useState } from 'react';
import { createWithdrawal } from '../services/api';

const currency = (value) =>
  new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(value);

export default function WithdrawalForm({ portfolio, onWithdrawalCreated }) {
  const [amount, setAmount] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  if (!portfolio) {
    return (
      <div className="withdrawal-form withdrawal-form--empty">
        <p>Select a portfolio above to request a withdrawal.</p>
      </div>
    );
  }

  const maxAllowed = portfolio.balance * 0.9;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    const numericAmount = Number(amount);

    // Client-side check purely for immediate feedback - the backend re-runs
    // the full business rule validation regardless (never trust the client).
    if (!amount || numericAmount <= 0) {
      setError('Enter an amount greater than zero.');
      return;
    }

    setSubmitting(true);
    try {
      const result = await createWithdrawal(portfolio.portfolioId, numericAmount);
      setSuccess(result);
      setAmount('');
      onWithdrawalCreated?.(result);
    } catch (err) {
      const message = err.response?.data?.message || 'Something went wrong. Please try again.';
      setError(message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form className="withdrawal-form" onSubmit={handleSubmit}>
      <h3>Request a withdrawal</h3>
      <p className="withdrawal-form__context">
        {portfolio.productName} · balance {currency(portfolio.balance)} · max per withdrawal {currency(maxAllowed)}
      </p>

      <label htmlFor="withdrawal-amount">Amount (ZAR)</label>
      <input
        id="withdrawal-amount"
        type="number"
        min="0.01"
        step="0.01"
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
        placeholder="0.00"
        disabled={submitting}
      />

      <button type="submit" disabled={submitting}>
        {submitting ? 'Submitting…' : 'Submit withdrawal'}
      </button>

      {error && <p className="withdrawal-form__message withdrawal-form__message--error">{error}</p>}
      {success && (
        <p className="withdrawal-form__message withdrawal-form__message--success">
          Approved. New balance: {currency(success.resultingBalance)}
        </p>
      )}
    </form>
  );
}
