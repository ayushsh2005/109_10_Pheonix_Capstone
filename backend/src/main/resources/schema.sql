CREATE TABLE IF NOT EXISTS customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    risk_profile VARCHAR(50),
    investment_goal VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'Active',
    notes VARCHAR(1000),
    target_allocation TEXT
);

-- Add missing columns to customer if they were not present in the initial JPA-created table
ALTER TABLE customer ADD COLUMN target_allocation TEXT;
ALTER TABLE customer MODIFY COLUMN notes VARCHAR(1000);

CREATE TABLE IF NOT EXISTS portfolio (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_portfolio_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS investment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    portfolio_id BIGINT NOT NULL,
    asset_name VARCHAR(100),
    asset_type VARCHAR(50),
    ticker VARCHAR(20),
    quantity DECIMAL(15,2),
    purchase_price DECIMAL(15,2),
    current_price DECIMAL(15,2),
    purchase_date DATE,

    CONSTRAINT fk_investment_portfolio
        FOREIGN KEY (portfolio_id)
        REFERENCES portfolio(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS trade (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    portfolio_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    investment_id BIGINT,
    asset_name VARCHAR(100) NOT NULL,
    asset_type VARCHAR(50),
    ticker VARCHAR(20),
    trade_type VARCHAR(10) NOT NULL,
    quantity DECIMAL(15,2) NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    trade_date DATE NOT NULL,
    realised_pl DECIMAL(15,2),

    CONSTRAINT fk_trade_portfolio
        FOREIGN KEY (portfolio_id)
        REFERENCES portfolio(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_trade_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer(id)
        ON DELETE CASCADE
);