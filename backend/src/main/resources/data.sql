USE portfolio_db;

-- =========================
-- CUSTOMERS
-- =========================

INSERT INTO customer (name, email, phone, risk_profile, investment_goal) VALUES
('Rahul Sharma', 'rahul.sharma@gmail.com', '9876543210', 'Aggressive', 'Long-term Wealth Creation'),
('Priya Nair', 'priya.nair@gmail.com', '9876543211', 'Moderate', 'Retirement Planning'),
('Amit Verma', 'amit.verma@gmail.com', '9876543212', 'Conservative', 'Capital Preservation'),
('Sneha Iyer', 'sneha.iyer@gmail.com', '9876543213', 'Aggressive', 'Financial Independence'),
('Arjun Patel', 'arjun.patel@gmail.com', '9876543214', 'Moderate', 'Child Education'),
('Neha Singh', 'neha.singh@gmail.com', '9876543215', 'Aggressive', 'Wealth Growth'),
('Vikram Reddy', 'vikram.reddy@gmail.com', '9876543216', 'Moderate', 'Home Purchase'),
('Kavya Menon', 'kavya.menon@gmail.com', '9876543217', 'Conservative', 'Regular Income'),
('Rohit Joshi', 'rohit.joshi@gmail.com', '9876543218', 'Aggressive', 'Early Retirement'),
('Ananya Das', 'ananya.das@gmail.com', '9876543219', 'Moderate', 'Tax Saving');

-- =========================
-- PORTFOLIOS
-- =========================

INSERT INTO portfolio (customer_id) VALUES
(1),(2),(3),(4),(5),
(6),(7),(8),(9),(10);

-- =========================
-- INVESTMENTS
-- =========================

INSERT INTO investment
(portfolio_id, asset_name, asset_type, ticker, quantity, purchase_price, current_price, purchase_date)
VALUES

-- Rahul Sharma
(1,'Reliance Industries','Stock','RELIANCE',25,2450,3100,'2023-02-14'),
(1,'TCS','Stock','TCS',18,3250,3900,'2022-08-10'),
(1,'Nifty 50 ETF','ETF','NIFTYBEES',120,210,278,'2023-06-01'),
(1,'HDFC Bank','Stock','HDFCBANK',30,1500,1825,'2024-01-12'),
(1,'Gold ETF','ETF','GOLDBEES',50,52,71,'2023-10-05'),

-- Priya Nair
(2,'ICICI Prudential Bluechip Fund','Mutual Fund','ICICIBLUE',150,62,78,'2022-05-15'),
(2,'Infosys','Stock','INFY',25,1420,1680,'2023-03-21'),
(2,'SBI ETF Nifty 50','ETF','SETFNIF50',100,190,240,'2023-11-12'),
(2,'Axis Bluechip Fund','Mutual Fund','AXISBLUE',80,48,61,'2024-01-15'),
(2,'Gold ETF','ETF','GOLDBEES',40,55,71,'2023-08-10'),

-- Amit Verma
(3,'Government Bond','Bond','GSEC2033',15,1000,1065,'2021-04-18'),
(3,'SBI Fixed Income Fund','Mutual Fund','SBIFI',200,22,24,'2022-01-20'),
(3,'HDFC Balanced Advantage Fund','Mutual Fund','HDFCBAL',90,58,69,'2023-04-30'),
(3,'Gold ETF','ETF','GOLDBEES',30,54,71,'2023-09-11'),
(3,'Power Grid','Stock','POWERGRID',50,220,340,'2022-12-15'),

-- Sneha Iyer
(4,'Zomato','Stock','ZOMATO',150,72,280,'2022-11-10'),
(4,'Tata Motors','Stock','TATAMOTORS',80,430,1085,'2023-05-21'),
(4,'Adani Ports','Stock','ADANIPORTS',40,720,1480,'2023-03-16'),
(4,'Nifty Next 50 ETF','ETF','JUNIORBEES',90,430,660,'2024-02-01'),
(4,'ICICI Bank','Stock','ICICIBANK',35,920,1485,'2023-08-18'),

-- Arjun Patel
(5,'Asian Paints','Stock','ASIANPAINT',18,2950,3415,'2022-10-08'),
(5,'SBI','Stock','SBIN',50,560,910,'2023-06-09'),
(5,'Parag Parikh Flexi Cap','Mutual Fund','PPFCF',110,51,77,'2022-12-11'),
(5,'Gold ETF','ETF','GOLDBEES',25,56,71,'2024-01-03'),
(5,'Nifty ETF','ETF','NIFTYBEES',80,215,278,'2023-07-15'),

-- Neha Singh
(6,'Tata Consultancy Services','Stock','TCS',20,3300,3900,'2022-09-10'),
(6,'Reliance Industries','Stock','RELIANCE',15,2550,3100,'2023-02-22'),
(6,'ICICI Bank','Stock','ICICIBANK',30,880,1485,'2022-12-18'),
(6,'Nifty ETF','ETF','NIFTYBEES',100,220,278,'2023-09-15'),
(6,'Gold ETF','ETF','GOLDBEES',40,57,71,'2024-01-20'),

-- Vikram Reddy
(7,'Kotak Equity Opportunities','Mutual Fund','KOTAKEQ',130,44,58,'2023-01-14'),
(7,'HDFC Bank','Stock','HDFCBANK',22,1550,1825,'2022-11-11'),
(7,'LIC','Stock','LICI',45,810,980,'2023-05-06'),
(7,'SBI ETF Nifty 50','ETF','SETFNIF50',120,195,240,'2023-10-09'),
(7,'Government Bond','Bond','GSEC2033',10,1000,1065,'2022-04-21'),

-- Kavya Menon
(8,'Government Bond','Bond','GSEC2033',30,1000,1065,'2021-09-09'),
(8,'Power Grid','Stock','POWERGRID',80,235,340,'2022-06-18'),
(8,'Gold ETF','ETF','GOLDBEES',60,50,71,'2022-08-08'),
(8,'HDFC Balanced Advantage Fund','Mutual Fund','HDFCBAL',150,55,69,'2023-04-04'),
(8,'SBI','Stock','SBIN',40,590,910,'2023-12-20'),

-- Rohit Joshi
(9,'Tata Motors','Stock','TATAMOTORS',120,520,1085,'2022-07-19'),
(9,'Infosys','Stock','INFY',30,1500,1680,'2023-02-18'),
(9,'Reliance Industries','Stock','RELIANCE',20,2500,3100,'2023-09-12'),
(9,'Nifty Next 50 ETF','ETF','JUNIORBEES',110,450,660,'2024-02-15'),
(9,'Adani Ports','Stock','ADANIPORTS',35,760,1480,'2023-11-22'),

-- Ananya Das
(10,'ELSS Tax Saver Fund','Mutual Fund','MIRAEELSS',140,39,54,'2022-04-10'),
(10,'ICICI Bank','Stock','ICICIBANK',28,890,1485,'2023-01-18'),
(10,'Infosys','Stock','INFY',22,1450,1680,'2023-06-05'),
(10,'Gold ETF','ETF','GOLDBEES',45,58,71,'2024-01-12'),
(10,'Nifty ETF','ETF','NIFTYBEES',90,225,278,'2023-08-08');