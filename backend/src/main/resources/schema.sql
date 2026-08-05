CREATE TABLE IF NOT EXISTS customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    risk_profile VARCHAR(50),
    investment_goal VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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