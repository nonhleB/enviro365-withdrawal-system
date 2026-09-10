-- Seed data for the in-memory H2 database.
-- Spring Boot runs this automatically on every startup (after Hibernate
-- creates the schema from the entities, thanks to
-- spring.jpa.defer-datasource-initialization=true in application.properties).
--
-- Column names follow Hibernate's default naming strategy: camelCase Java
-- fields become snake_case columns (e.g. dateOfBirth -> date_of_birth).

-- ===== PRODUCTS =====
INSERT INTO products (id, name, type) VALUES
    (1, 'Retirement Annuity RA-2026', 'RETIREMENT_ANNUITY'),
    (2, 'Balanced Unit Trust', 'UNIT_TRUST'),
    (3, 'Flexible Savings Plan', 'SAVINGS_PLAN'),
    (4, '5-Year Endowment', 'ENDOWMENT');

-- ===== INVESTORS =====
-- Two are over 65 (eligible for retirement withdrawals), two are under 65
-- (NOT eligible) - this lets us test the age business rule immediately.
INSERT INTO investors (id, first_name, last_name, date_of_birth, email) VALUES
    (1, 'John', 'Retiree', '1955-03-12', 'john.retiree@example.com'),
    (2, 'Sarah', 'Young', '1990-07-22', 'sarah.young@example.com'),
    (3, 'Thabo', 'Mokoena', '1958-11-05', 'thabo.mokoena@example.com'),
    (4, 'Lindiwe', 'Dlamini', '1985-02-18', 'lindiwe.dlamini@example.com');

-- ===== PORTFOLIOS =====
INSERT INTO portfolios (id, investor_id, product_id, balance) VALUES
    (1, 1, 1, 850000.00),  -- John's retirement annuity
    (2, 2, 2, 120000.00),  -- Sarah's unit trust
    (3, 3, 1, 620000.00),  -- Thabo's retirement annuity
    (4, 4, 3, 45000.00),   -- Lindiwe's savings plan
    (5, 2, 4, 78000.00);   -- Sarah's endowment

-- ===== WITHDRAWAL NOTICES (historical, for the history table demo) =====
INSERT INTO withdrawal_notices (id, portfolio_id, amount, notice_date, status, rejection_reason, resulting_balance) VALUES
    (1, 2, 5000.00, '2026-06-15', 'APPROVED', NULL, 120000.00),
    (2, 1, 900000.00, '2026-05-01', 'REJECTED', 'Withdrawal amount exceeds available balance', NULL);
