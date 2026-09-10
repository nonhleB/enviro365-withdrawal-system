# Enviro365 Investments – Withdrawal Notice System

Junior Developer Assessment (eTalente, 2026) — full-stack system for Enviro365
Investments' automated withdrawal notice process.

> Status: 🚧 Day 2 — entities, repositories, and seed data. This README will
> be filled in properly on Day 7 with setup instructions, API docs, AI usage
> disclosure, and screenshots.

## Domain model (Day 2)

- **Investor** — a person holding investments. Stores `dateOfBirth` (not a
  static age) so age is always correct when calculated.
- **Product** — an investment product type (Retirement Annuity, Unit Trust,
  Savings Plan, Endowment). `RETIREMENT_ANNUITY` triggers the age > 65 rule.
- **Portfolio** — links one Investor to one Product, holds the balance.
- **WithdrawalNotice** — a withdrawal request against a Portfolio, with
  status (PENDING/APPROVED/REJECTED) and a snapshot of the resulting balance.

Seed data (`data.sql`) loads 4 investors (2 over 65, 2 under 65), 4 products,
5 portfolios, and 2 historical withdrawal notices on every app startup, so
there's real data to work with immediately.

## Stack
- **Backend:** Java 17, Spring Boot 3.3, Spring Data JPA, H2 (in-memory)
- **Frontend:** React
- **Build tools:** Maven (backend), npm/Vite (frontend)

## Structure
```
enviro365-project/
├── backend/     # Spring Boot REST API
├── frontend/    # React UI
└── README.md
```

## Day-by-day build log
- **Day 1:** ✅ Environment setup, project scaffolding
- **Day 2:** ✅ Entities, H2 config, repositories, seed data
- **Day 3:** Business rules, services, controllers
- **Day 4:** DTOs, global exception handling, validation, CSV export
- **Day 5:** React dashboard + withdrawal form
- **Day 6:** History table, CSV download, tests
- **Day 7:** Polish, docs, screenshots
