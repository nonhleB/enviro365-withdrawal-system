import axios from 'axios';

// Relative base URL - Vite's dev server proxy (vite.config.js) forwards
// /api/* to the Spring Boot backend on :8080, so this works in dev without
// any CORS configuration needed.
const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

export const getPortfolios = (investorId) =>
  api.get(`/portfolios/investor/${investorId}`).then((res) => res.data);

export const createWithdrawal = (portfolioId, amount) =>
  api.post('/withdrawals', { portfolioId, amount }).then((res) => res.data);

export const getWithdrawalHistory = (portfolioId) =>
  api.get(`/withdrawals/portfolio/${portfolioId}`).then((res) => res.data);

export const getCsvExportUrl = (investorId) =>
  `/api/withdrawals/export?investorId=${investorId}`;

export default api;
