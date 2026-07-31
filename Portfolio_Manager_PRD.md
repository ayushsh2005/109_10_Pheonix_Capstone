# Product Requirements Document (PRD)

# Investment Portfolio Management System

## 1. Product Overview

The Investment Portfolio Management System is designed for an investment manager who collects funds from multiple individual investors and invests them into different assets on their behalf.

The system allows the investment manager to manage customers, track their investments, monitor portfolio performance, view asset allocation, and provide investment suggestions.

## 2. Business Context

The investment manager acts as a middleman between individual investors and financial markets.

Flow:

Individual Investors -> Investment Manager -> Investments -> Returns

The system provides visibility into:
- Customer investments
- Portfolio value
- Performance metrics
- Asset allocation
- Investment insights

## 3. Goals

### Primary Goals

- Manage multiple customers and their investments.
- Provide CRUD operations for customers and investments.
- Calculate portfolio performance.
- Display percentage-wise asset allocation.
- Provide investment suggestions and insights.

## 4. Users

### Investment Manager

The main user of the system.

Responsibilities:
- Create and manage customers.
- Record customer investments.
- Monitor portfolio performance.
- Analyse allocation.
- Review suggestions.

### Future Users

Investors may get access to view their own portfolio.

## 5. Functional Requirements

## Customer Management

The system should support:

### Create Customer
Store customer information.

Fields:
- Customer ID
- Name
- Email
- Phone Number
- Risk Profile
- Investment Goal

### View Customers
Investment manager can view all customers.

### Update Customer
Customer details can be modified.

### Delete Customer
Customers can be removed when required.

---

## Investment Management

Investment manager can manage customer investments.

Investment fields:

- Investment ID
- Customer ID
- Asset Name
- Asset Type
- Ticker Symbol
- Quantity
- Purchase Price
- Current Price
- Purchase Date

Supported operations:
- Add investment
- View investments
- Update investment
- Delete investment

---

## Portfolio Performance

The system should calculate:

### Individual Customer Performance

Metrics:
- Total investment amount
- Current portfolio value
- Profit/Loss
- Return percentage

Example:

Investment: €50,000

Current Value: €60,000

Profit: €10,000

Return: 20%

---

## Dashboard

The dashboard should provide:

### Overall Summary

- Total customers
- Total assets managed
- Overall portfolio value
- Overall profit/loss

### Allocation View

Display asset distribution:

Example:

Stocks: 60%
Bonds: 25%
Cash: 10%
Others: 5%

Visualisation:
- Pie charts
- Bar charts

---

## Investment Suggestions

The system should provide basic portfolio insights.

Examples:

### Diversification Suggestion

If a customer has high concentration in one asset category:

"Portfolio has high exposure to technology stocks. Consider diversification."

### Risk Suggestion

Compare customer risk profile with current portfolio allocation.

Example:

Risk Profile:
Conservative

Current Allocation:
90% stocks

Suggestion:
"Consider reducing high-risk assets."

---

# 6. Non Functional Requirements

## Performance

- APIs should respond quickly.
- Database operations should be optimized.

## Maintainability

The application should follow clean architecture principles.

Structure:

Controller
|
Service
|
Repository
|
Database

## Documentation

The system should provide API documentation using Swagger/OpenAPI.

---

# 7. Database Design

## Customer Table

Fields:
- id
- name
- email
- phone
- risk_profile
- investment_goal
- created_date


## Portfolio Table

Fields:
- id
- customer_id
- created_date


## Investment Table

Fields:
- id
- portfolio_id
- asset_name
- asset_type
- ticker
- quantity
- purchase_price
- current_price
- purchase_date

---

# 8. API Requirements

## Customer APIs

POST /customers

GET /customers

GET /customers/{id}

PUT /customers/{id}

DELETE /customers/{id}


## Investment APIs

POST /customers/{id}/investments

GET /customers/{id}/investments

PUT /investments/{id}

DELETE /investments/{id}


## Dashboard APIs

GET /dashboard/summary

GET /dashboard/allocation

GET /customers/{id}/performance

---

# 9. MVP Scope

Must Have:

- Customer CRUD
- Investment CRUD
- Multiple customer support
- Portfolio performance calculation
- Allocation dashboard
- Basic suggestions

---

# 10. Future Enhancements

- Customer login
- Live market data integration
- AI-powered investment recommendations
- Automated reports
- Risk scoring
- Notifications
- Transaction history

---

# 11. Success Criteria

The project will be successful when:

- Investment manager can manage multiple customers.
- Customer investments can be tracked.
- Portfolio performance can be calculated.
- Dashboard provides clear insights.
- Suggestions help identify portfolio improvements.
