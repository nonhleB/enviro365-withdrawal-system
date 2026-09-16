// Investor list is hardcoded here to match the seed data (data.sql) since
// there's no GET /api/investors endpoint yet - this keeps the dashboard
// functional without needing a login system, appropriate for this
// assessment's scope. Could be replaced with a real endpoint later.
const INVESTORS = [
  { id: 1, name: 'John Retiree' },
  { id: 2, name: 'Sarah Young' },
  { id: 3, name: 'Thabo Mokoena' },
  { id: 4, name: 'Lindiwe Dlamini' },
];

export default function InvestorSelector({ selectedId, onChange }) {
  return (
    <div className="investor-selector">
      <label htmlFor="investor-select">Investor</label>
      <select
        id="investor-select"
        value={selectedId}
        onChange={(e) => onChange(Number(e.target.value))}
      >
        {INVESTORS.map((inv) => (
          <option key={inv.id} value={inv.id}>
            {inv.name}
          </option>
        ))}
      </select>
    </div>
  );
}
