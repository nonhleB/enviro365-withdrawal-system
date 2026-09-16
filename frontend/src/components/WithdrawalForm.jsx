import { useState, useMemo } from 'react';
import { createWithdrawal } from '../services/api';

const currency = (value) =>
  new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(value);

export default function WithdrawalForm({ portfolio, onWithdrawalCreated }) {
  const [amount, setAmount] = useState('');
  const [touched, setTouched] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [serverError, setServerError] = useState(null);
  const [success, setSuccess] = useState(null);

  const maxAllowed = portfolio ? portfolio.balance * 0.9 : 0;
  const isRetirementLocked =
    portfolio?.productType === 'RETIREMENT_ANNUITY' && portfolio.investorAge <= 65;

  // UI validation: computed on every render so the button and inline hint
  // stay in sync as the person types, rather than only checking on submit.
  const validationError = useMemo(() => {
    if (isRetirementLocked) {
      return `Retirement withdrawals unlock at age 66 (currently ${portfolio?.investorAge}).`;
    }
    if (amount === '') {
      return null; // don't show "required" until they've interacted with the field
    }
    const numeric = Number(amount);
    if (Number.isNaN(numeric) || numeric <= 0) {
      return 'Amount must be greater than zero.';
    }
    if (numeric > maxAllowed) {
      return `Amount exceeds the 90% limit of ${currency(maxAllowed)}.`;
    }
    return null;
  }, [amount, maxAllowed, isRetirementLocked, portfolio]);

  const canSubmit = portfolio && amount !== '' && !validationError && !submitting;

  if (!portfolio) {
    return (
      <div className="withdrawal-form withdrawal-form--empty">
        <p>Select a portfolio above to request a withdrawal.</p>
      </div>
    );
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setTouched(true);
    setServerError(null);
    setSuccess(null);

    if (!canSubmit) return;

    setSubmitting(true);
    try {
      const result = await createWithdrawal(portfolio.portfolioId, Number(amount));
      setSuccess(result);
      setAmount('');
      setTouched(false);
      onWithdrawalCreated?.(result);
    } catch (err) {
      setServerError(err.response?.data?.message || 'Something went wrong. Please try again.');
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
        onBlur={() => setTouched(true)}
        placeholder="0.00"
        disabled={submitting || isRetirementLocked}
        aria-invalid={Boolean(touched && validationError)}
      />

      {touched && validationError && (
        <p className="withdrawal-form__message withdrawal-form__message--error">{validationError}</p>
      )}

      <button type="submit" disabled={!canSubmit}>
        {submitting ? 'Submitting…' : 'Submit withdrawal'}
      </button>

      {serverError && (
        <p className="withdrawal-form__message withdrawal-form__message--error">{serverError}</p>
      )}
      {success && (
        <p className="withdrawal-form__message withdrawal-form__message--success">
          Approved. New balance: {currency(success.resultingBalance)}
        </p>
      )}
    </form>
  );
}
