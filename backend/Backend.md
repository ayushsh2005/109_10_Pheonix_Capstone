# Backend Documentation

# Investment Portfolio Management System

## Overview

The backend of the Investment Portfolio Management System is developed using **Java Spring Boot** and **MySQL**.

It provides REST APIs that allow an Investment Manager to manage customers, portfolios, and investments while calculating portfolio performance and asset allocation.

The backend follows a layered architecture to ensure maintainability, scalability, and separation of concerns.

---

# Technology Stack

| Component | Technology |
|------------|------------|
| Language | Java 25 |
| Framework | Spring Boot 4.1.0 |
| Build Tool | Maven |
| Database | MySQL 8 |
| ORM | Spring Data JPA (Hibernate) |
| API Type | REST API |
| Dependency Management | Maven |
| Testing | Spring Boot Test |

---

# Maven Dependencies

The project currently includes the following dependencies:

- Spring Boot Starter Web MVC
- Spring Boot Starter Data JPA
- MySQL Connector/J
- Spring Boot DevTools
- Spring Boot Testing

These dependencies provide:

- REST API development
- Database access using Hibernate/JPA
- MySQL connectivity
- Hot reload during development
- Unit and integration testing support

---

# Backend Architecture

The application follows the standard Spring Boot layered architecture.

```
                Client

                  │

            REST Controller

                  │

              Service Layer

                  │

          Repository Layer

                  │

          MySQL Database
```

Each layer has a specific responsibility.

### Controller

- Receives HTTP Requests
- Validates requests
- Returns API responses

### Service

- Business Logic
- Portfolio calculations
- Asset allocation
- Investment suggestions

### Repository

- Database operations
- CRUD functionality
- Query execution

### Database

Stores application data.

---

# Recommended Project Structure

```
src
└── main
    ├── java
    │
    └── com.backend
        │
        ├── controller
        │      CustomerController.java
        │      InvestmentController.java
        │      DashboardController.java
        │
        ├── service
        │      CustomerService.java
        │      InvestmentService.java
        │      PortfolioService.java
        │
        ├── repository
        │      CustomerRepository.java
        │      PortfolioRepository.java
        │      InvestmentRepository.java
        │
        ├── entity
        │      Customer.java
        │      Portfolio.java
        │      Investment.java
        │
        ├── dto
        │
        ├── exception
        │
        ├── config
        │
        └── BackendApplication.java

resources

application.properties

schema.sql

data.sql
```

---

# Database

Database Name

```
portfolio_db
```

---

# Database Tables

## Customer

Stores customer information.

| Column | Type |
|----------|---------|
| id | BIGINT |
| name | VARCHAR(100) |
| email | VARCHAR(100) |
| phone | VARCHAR(20) |
| risk_profile | VARCHAR(30) |
| investment_goal | VARCHAR(255) |
| created_date | TIMESTAMP |

---

## Portfolio

Each customer owns one portfolio.

| Column | Type |
|----------|---------|
| id | BIGINT |
| customer_id | BIGINT |
| created_date | TIMESTAMP |

---

## Investment

Stores all customer investments.

| Column | Type |
|----------|---------|
| id | BIGINT |
| portfolio_id | BIGINT |
| asset_name | VARCHAR(100) |
| asset_type | VARCHAR(50) |
| ticker | VARCHAR(20) |
| quantity | DECIMAL(15,2) |
| purchase_price | DECIMAL(15,2) |
| current_price | DECIMAL(15,2) |
| purchase_date | DATE |

---

# Entity Relationships

```
Customer

1

│

│

1

Portfolio

1

│

│

N

Investment
```

Meaning

- One Customer owns one Portfolio.
- One Portfolio contains multiple Investments.

---

# SQL Schema

## Customer

```sql
CREATE TABLE customer (

id BIGINT PRIMARY KEY AUTO_INCREMENT,

name VARCHAR(100) NOT NULL,

email VARCHAR(100) UNIQUE NOT NULL,

phone VARCHAR(20),

risk_profile VARCHAR(30),

investment_goal VARCHAR(255),

created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);
```

---

## Portfolio

```sql
CREATE TABLE portfolio (

id BIGINT PRIMARY KEY AUTO_INCREMENT,

customer_id BIGINT UNIQUE NOT NULL,

created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

FOREIGN KEY(customer_id)
REFERENCES customer(id)
ON DELETE CASCADE

);
```

---

## Investment

```sql
CREATE TABLE investment (

id BIGINT PRIMARY KEY AUTO_INCREMENT,

portfolio_id BIGINT NOT NULL,

asset_name VARCHAR(100),

asset_type VARCHAR(50),

ticker VARCHAR(20),

quantity DECIMAL(15,2),

purchase_price DECIMAL(15,2),

current_price DECIMAL(15,2),

purchase_date DATE,

FOREIGN KEY(portfolio_id)
REFERENCES portfolio(id)
ON DELETE CASCADE

);
```

---

# JPA Entities

## Customer

```java
@Entity
public class Customer {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String name;

private String email;

private String phone;

private String riskProfile;

private String investmentGoal;

private LocalDateTime createdDate;

@OneToOne(mappedBy = "customer")
private Portfolio portfolio;

}
```

---

## Portfolio

```java
@Entity
public class Portfolio {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private LocalDateTime createdDate;

@OneToOne
@JoinColumn(name="customer_id")
private Customer customer;

@OneToMany(mappedBy="portfolio")
private List<Investment> investments;

}
```

---

## Investment

```java
@Entity
public class Investment {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String assetName;

private String assetType;

private String ticker;

private BigDecimal quantity;

private BigDecimal purchasePrice;

private BigDecimal currentPrice;

private LocalDate purchaseDate;

@ManyToOne
@JoinColumn(name="portfolio_id")
private Portfolio portfolio;

}
```

---

# Repository Layer

```
CustomerRepository

PortfolioRepository

InvestmentRepository
```

All repositories extend

```
JpaRepository<Entity, Long>
```

---

# Service Layer

## CustomerService

Responsibilities

- Create customer
- Update customer
- Delete customer
- Get customer
- Get all customers

---

## InvestmentService

Responsibilities

- Add investment
- Update investment
- Delete investment
- Retrieve investments

---

## PortfolioService

Responsibilities

- Calculate total investment
- Calculate portfolio value
- Calculate return percentage
- Calculate profit/loss
- Generate allocation summary
- Generate investment suggestions

---

# REST APIs

## Customer

```
POST /customers

GET /customers

GET /customers/{id}

PUT /customers/{id}

DELETE /customers/{id}
```

---

## Investment

```
POST /customers/{id}/investments

GET /customers/{id}/investments

PUT /investments/{id}

DELETE /investments/{id}
```

---

## Dashboard

```
GET /dashboard/summary

GET /dashboard/allocation

GET /customers/{id}/performance
```

---

# Portfolio Calculations

## Total Investment

```
Σ (Quantity × Purchase Price)
```

---

## Current Portfolio Value

```
Σ (Quantity × Current Price)
```

---

## Profit / Loss

```
Current Value − Total Investment
```

---

## Return %

```
(Current Value − Total Investment)

---------------------------------- × 100

Total Investment
```

---

# Validation Rules

Customer

- Name cannot be empty
- Email must be unique
- Email must be valid

Investment

- Quantity > 0
- Purchase Price > 0
- Current Price > 0

---

# Exception Handling

The application should include a global exception handler.

Custom exceptions

```
CustomerNotFoundException

PortfolioNotFoundException

InvestmentNotFoundException
```

---

# application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/portfolio_db

spring.datasource.username=root

spring.datasource.password=password

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true

spring.jpa.properties.hibernate.format_sql=true
```

---

# Future Enhancements

- JWT Authentication
- User Login
- Role-Based Access
- Live Stock API Integration
- AI-based Investment Suggestions
- Email Notifications
- Transaction History
- Portfolio Reports
- Docker Deployment
- Flyway Database Migration

---

# Development Guidelines

- Follow REST API conventions.
- Keep business logic inside the Service layer.
- Use JPA repositories for database operations.
- Validate all incoming requests.
- Return appropriate HTTP status codes.
- Maintain clean package separation.
- Use meaningful entity relationships.
- Document APIs for frontend integration.
