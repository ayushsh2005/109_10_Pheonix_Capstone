-- ============================================================
-- Seed data for portfolio_db
-- Safe to re-run: INSERT IGNORE skips rows whose PK already exists.
-- DDL lives in schema.sql; this file is INSERT-only.
-- ============================================================

-- ── Customers ────────────────────────────────────────────────
INSERT IGNORE INTO customer
    (id, name, email, phone, risk_profile, investment_goal, notes, status, created_date, target_allocation)
VALUES
(1,  'Rahul Sharma', 'rahul.sharma@gmail.com', '9876543210', 'Aggressive',  'Long-term Wealth Creation', 'Prefers diversified equity exposure.',          'Active',   '2026-08-05 08:01:59', NULL),
(2,  'Priya Nair',   'priya.nair@gmail.com',   '9876543211', 'Moderate',    'Retirement Planning',       'Focus on stable income assets.',               'Active',   '2026-08-05 08:01:59', NULL),
(3,  'Amit Verma',   'amit.verma@gmail.com',   '9876543212', 'Conservative','Retirement Planning',       'Very low risk tolerance.',                     'Active',   '2026-08-05 08:01:59', NULL),
(4,  'Sneha Iyer',   'sneha.iyer@gmail.com',   '9876543213', 'Aggressive',  'Financial Independence',    'Growth-oriented, open to sector bets.',        'Active',   '2026-08-05 08:01:59', NULL),
(5,  'Arjun Patel',  'arjun.patel@gmail.com',  '9876543214', 'Moderate',    'Child Education',           'Targets education fund over 7 years.',         'Active',   '2026-08-05 08:01:59', NULL),
(6,  'Neha Singh',   'neha.singh@gmail.com',   '9876543215', 'Aggressive',  'Wealth Growth',             'Aggressive growth with periodic rebalancing.', 'Active',   '2026-08-05 08:01:59', NULL),
(7,  'Vikram Reddy', 'vikram.reddy@gmail.com', '9876543216', 'Moderate',    'Home Purchase',             'Income plus moderate growth mix.',             'Active',   '2026-08-05 08:01:59', NULL),
(8,  'Kavya Menon',  'kavya.menon@gmail.com',  '9876543217', 'Conservative','Regular Income',            'Prefers capital preservation.',                'Active',   '2026-08-05 08:01:59', NULL),
(9,  'Rohit Joshi',  'rohit.joshi@gmail.com',  '9876543218', 'Aggressive',  'Early Retirement',          'Long horizon, high conviction positions.',     'Active',   '2026-08-05 08:01:59', NULL),
(10, 'Ananya Das',   'ananya.das@gmail.com',   '9876543219', 'Moderate',    'Tax Saving',                'Tax planning focused.',                        'Archived', '2026-08-05 08:01:59', NULL);

-- ── Portfolios (one per customer) ────────────────────────────
INSERT IGNORE INTO portfolio (id, customer_id, created_date) VALUES
(1,  1, '2026-08-05 08:01:59'),
(2,  2, '2026-08-05 08:01:59'),
(3,  3, '2026-08-05 08:01:59'),
(4,  4, '2026-08-05 08:01:59'),
(5,  5, '2026-08-05 08:01:59'),
(6,  6, '2026-08-05 08:01:59'),
(7,  7, '2026-08-05 08:01:59'),
(8,  8, '2026-08-05 08:01:59'),
(9,  9, '2026-08-05 08:01:59'),
(10, 10,'2026-08-05 08:01:59');

-- ── Investments ───────────────────────────────────────────────
-- Note: investment id=11 (Amit Verma - Government Bond 15 units) was
-- sold on 2026-08-05 and is therefore absent from the live dataset.
INSERT IGNORE INTO investment
    (id, portfolio_id, asset_name, asset_type, ticker, quantity, purchase_price, current_price, purchase_date)
VALUES
-- Rahul Sharma  (portfolio 1)
(1,  1, 'Reliance Industries',             'Stock',       'RELIANCE',  25.00, 2450.00, 3100.00, '2023-02-14'),
(2,  1, 'TCS',                             'Stock',       'TCS',       18.00, 3250.00, 3900.00, '2022-08-10'),
(3,  1, 'Nifty 50 ETF',                   'ETF',         'NIFTYBEES', 120.00,  210.00,  278.00, '2023-06-01'),
(4,  1, 'HDFC Bank',                       'Stock',       'HDFCBANK',  30.00, 1500.00, 1825.00, '2024-01-12'),
(5,  1, 'Gold ETF',                        'ETF',         'GOLDBEES',  50.00,   52.00,   71.00, '2023-10-05'),
-- Priya Nair    (portfolio 2)
(6,  2, 'ICICI Prudential Bluechip Fund',  'Mutual Fund', 'ICICIBLUE', 150.00,   62.00,   78.00, '2022-05-15'),
(7,  2, 'Infosys',                         'Stock',       'INFY',      25.00, 1420.00, 1680.00, '2023-03-21'),
(8,  2, 'SBI ETF Nifty 50',               'ETF',         'SETFNIF50', 100.00,  190.00,  240.00, '2023-11-12'),
(9,  2, 'Axis Bluechip Fund',              'Mutual Fund', 'AXISBLUE',  80.00,   48.00,   61.00, '2024-01-15'),
(10, 2, 'Gold ETF',                        'ETF',         'GOLDBEES',  40.00,   55.00,   71.00, '2023-08-10'),
-- Amit Verma    (portfolio 3) - id 11 sold, starts at 12
(12, 3, 'SBI Fixed Income Fund',           'Mutual Fund', 'SBIFI',    200.00,   22.00,   24.00, '2022-01-20'),
(13, 3, 'HDFC Balanced Advantage Fund',    'Mutual Fund', 'HDFCBAL',  90.00,   58.00,   69.00, '2023-04-30'),
(14, 3, 'Gold ETF',                        'ETF',         'GOLDBEES',  30.00,   54.00,   71.00, '2023-09-11'),
(15, 3, 'Power Grid',                      'Stock',       'POWERGRID', 50.00,  220.00,  340.00, '2022-12-15'),
-- Sneha Iyer    (portfolio 4)
(16, 4, 'Zomato',                          'Stock',       'ZOMATO',   150.00,   72.00,  280.00, '2022-11-10'),
(17, 4, 'Tata Motors',                     'Stock',       'TATAMOTORS',80.00,  430.00, 1085.00, '2023-05-21'),
(18, 4, 'Adani Ports',                     'Stock',       'ADANIPORTS',40.00,  720.00, 1480.00, '2023-03-16'),
(19, 4, 'Nifty Next 50 ETF',              'ETF',         'JUNIORBEES',90.00,  430.00,  660.00, '2024-02-01'),
(20, 4, 'ICICI Bank',                      'Stock',       'ICICIBANK', 35.00,  920.00, 1485.00, '2023-08-18'),
-- Arjun Patel   (portfolio 5)
(21, 5, 'Asian Paints',                    'Stock',       'ASIANPAINT',18.00, 2950.00, 3415.00, '2022-10-08'),
(22, 5, 'SBI',                             'Stock',       'SBIN',      50.00,  560.00,  910.00, '2023-06-09'),
(23, 5, 'Parag Parikh Flexi Cap',          'Mutual Fund', 'PPFCF',    110.00,   51.00,   77.00, '2022-12-11'),
(24, 5, 'Gold ETF',                        'ETF',         'GOLDBEES',  25.00,   56.00,   71.00, '2024-01-03'),
(25, 5, 'Nifty ETF',                       'ETF',         'NIFTYBEES', 80.00,  215.00,  278.00, '2023-07-15'),
-- Neha Singh    (portfolio 6)
(26, 6, 'Tata Consultancy Services',       'Stock',       'TCS',       20.00, 3300.00, 3900.00, '2022-09-10'),
(27, 6, 'Reliance Industries',             'Stock',       'RELIANCE',  15.00, 2550.00, 3100.00, '2023-02-22'),
(28, 6, 'ICICI Bank',                      'Stock',       'ICICIBANK', 30.00,  880.00, 1485.00, '2022-12-18'),
(29, 6, 'Nifty ETF',                       'ETF',         'NIFTYBEES', 100.00, 220.00,  278.00, '2023-09-15'),
(30, 6, 'Gold ETF',                        'ETF',         'GOLDBEES',  40.00,   57.00,   71.00, '2024-01-20'),
-- Vikram Reddy  (portfolio 7)
(31, 7, 'Kotak Equity Opportunities',      'Mutual Fund', 'KOTAKEQ',  130.00,   44.00,   58.00, '2023-01-14'),
(32, 7, 'HDFC Bank',                       'Stock',       'HDFCBANK',  22.00, 1550.00, 1825.00, '2022-11-11'),
(33, 7, 'LIC',                             'Stock',       'LICI',      45.00,  810.00,  980.00, '2023-05-06'),
(34, 7, 'SBI ETF Nifty 50',               'ETF',         'SETFNIF50', 120.00, 195.00,  240.00, '2023-10-09'),
(35, 7, 'Government Bond',                 'Bond',        'GSEC2033',  10.00, 1000.00, 1065.00, '2022-04-21'),
-- Kavya Menon   (portfolio 8)
(36, 8, 'Government Bond',                 'Bond',        'GSEC2033',  30.00, 1000.00, 1065.00, '2021-09-09'),
(37, 8, 'Power Grid',                      'Stock',       'POWERGRID', 80.00,  235.00,  340.00, '2022-06-18'),
(38, 8, 'Gold ETF',                        'ETF',         'GOLDBEES',  60.00,   50.00,   71.00, '2022-08-08'),
(39, 8, 'HDFC Balanced Advantage Fund',    'Mutual Fund', 'HDFCBAL',  150.00,   55.00,   69.00, '2023-04-04'),
(40, 8, 'SBI',                             'Stock',       'SBIN',      40.00,  590.00,  910.00, '2023-12-20'),
-- Rohit Joshi   (portfolio 9)
(41, 9, 'Tata Motors',                     'Stock',       'TATAMOTORS',120.00, 520.00, 1085.00, '2022-07-19'),
(42, 9, 'Infosys',                         'Stock',       'INFY',      30.00, 1500.00, 1680.00, '2023-02-18'),
(43, 9, 'Reliance Industries',             'Stock',       'RELIANCE',  20.00, 2500.00, 3100.00, '2023-09-12'),
(44, 9, 'Nifty Next 50 ETF',              'ETF',         'JUNIORBEES',110.00, 450.00,  660.00, '2024-02-15'),
(45, 9, 'Adani Ports',                     'Stock',       'ADANIPORTS',35.00,  760.00, 1480.00, '2023-11-22'),
-- Ananya Das    (portfolio 10)
(46, 10,'ELSS Tax Saver Fund',             'Mutual Fund', 'MIRAEELSS', 140.00,  39.00,   54.00, '2022-04-10'),
(47, 10,'ICICI Bank',                      'Stock',       'ICICIBANK', 28.00,  890.00, 1485.00, '2023-01-18'),
(48, 10,'Infosys',                         'Stock',       'INFY',      22.00, 1450.00, 1680.00, '2023-06-05'),
(49, 10,'Gold ETF',                        'ETF',         'GOLDBEES',  45.00,   58.00,   71.00, '2024-01-12'),
(50, 10,'Nifty ETF',                       'ETF',         'NIFTYBEES', 90.00,  225.00,  278.00, '2023-08-08');

-- ── Trades ────────────────────────────────────────────────────
-- Sell: Amit Verma sold 15 units of Government Bond (investment id=11)
INSERT IGNORE INTO trade
    (id, portfolio_id, customer_id, investment_id, asset_name, asset_type, ticker,
     trade_type, quantity, price, trade_date, realised_pl)
VALUES
(1, 3, 3, 11, 'Government Bond', 'Bond', 'GSEC2033', 'Sell', 15.00, 1065.00, '2026-08-05', 975.00);
