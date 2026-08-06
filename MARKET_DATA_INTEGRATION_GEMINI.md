# Market Data Integration Design

## Portfolio Manager - Asset Current Market Value and AI Suggestion Integration

## 1. Overview

The Portfolio Manager will integrate two external services:

1.  Yahoo Finance API for current asset market prices.
2.  Gemini API for AI-based portfolio suggestions.

The goal is to provide live portfolio valuation and intelligent
investment insights.

------------------------------------------------------------------------

# 2. Market Data Integration

Architecture:

    Frontend
        |
    Spring Boot Backend
        |
    MarketPriceService
        |
    Yahoo Finance API
        |
    Investment.currentPrice
        |
    Portfolio Calculation

------------------------------------------------------------------------

# 3. Yahoo Finance API

## Selected Option

Yahoo Finance is selected because:

-   Free
-   No API key required
-   Supports NSE stocks
-   Simple REST integration

Ticker conversion:

Database:

    RELIANCE
    TCS
    INFY

Yahoo format:

    RELIANCE.NS
    TCS.NS
    INFY.NS

Example:

    GET
    https://query1.finance.yahoo.com/v8/finance/chart/RELIANCE.NS

------------------------------------------------------------------------

# 4. Market Backend Components

Create:

    market
     |
     |-- MarketController.java
     |-- MarketPriceService.java
     |-- YahooFinanceClient.java
     |-- MarketPriceDTO.java

Endpoint:

    GET /api/market/{ticker}

Example:

    GET /api/market/RELIANCE

------------------------------------------------------------------------

# 5. Price Refresh Scheduler

Add:

    MarketPriceScheduler

Runs:

    Every 5 minutes

Flow:

    Fetch investments
            |
    Read ticker
            |
    Call Yahoo Finance
            |
    Update currentPrice
            |
    Save database

------------------------------------------------------------------------

# 6. Gemini API Integration

## Purpose

Gemini API will provide AI-powered portfolio suggestions.

Use cases:

-   Portfolio analysis
-   Diversification suggestions
-   Risk analysis
-   Investment insights
-   Explain portfolio performance

------------------------------------------------------------------------

# 7. Gemini Architecture

    User Portfolio Data
            |
            |
    Spring Boot Backend
            |
            |
    Portfolio Analysis Service
            |
            |
    Gemini API
            |
            |
    AI Recommendation Response
            |
            |
    Frontend Dashboard

------------------------------------------------------------------------

# 8. Gemini Request Flow

Backend sends:

``` json
{
  "portfolio": [
    {
      "asset": "RELIANCE",
      "quantity": 10,
      "currentValue": 31054
    }
  ],
  "riskProfile": "MODERATE",
  "investmentGoal": "LONG_TERM_GROWTH"
}
```

Gemini returns:

``` json
{
  "summary": "Portfolio is moderately diversified",
  "suggestions": [
    "Increase sector diversification",
    "Review concentration risk"
  ],
  "riskLevel": "MEDIUM"
}
```

------------------------------------------------------------------------

# 9. Gemini Backend Components

Create:

    ai
     |
     |-- GeminiController.java
     |-- GeminiService.java
     |-- GeminiClient.java
     |-- SuggestionDTO.java

API:

    GET /api/portfolio/{customerId}/suggestions

Response:

``` json
{
 "customerId":1,
 "suggestions":[
   "Increase diversification",
   "Review technology allocation"
 ]
}
```

------------------------------------------------------------------------

# 10. Database Enhancement

Optional table:

    portfolio_suggestion
    --------------------
    id
    customer_id
    suggestion_text
    created_date

Purpose:

-   Store AI recommendations
-   Maintain suggestion history
-   Track previous analysis

------------------------------------------------------------------------

# 11. Error Handling

Market API failure:

    Use previous currentPrice

Gemini API failure:

    Return rule-based suggestions

Timeout:

    5 second timeout
    2 retries

------------------------------------------------------------------------

# 12. Future Improvements

Market:

-   Redis cache
-   WebSocket updates
-   Broker APIs

AI:

-   News sentiment analysis
-   Risk scoring
-   Financial assistant
-   Personalized recommendations

------------------------------------------------------------------------

# 13. Implementation Plan

## Phase 1

-   Yahoo Finance integration
-   Market price endpoint
-   Portfolio valuation update

## Phase 2

-   Gemini API integration
-   Suggestion endpoint
-   Dashboard AI section

## Phase 3

-   Caching
-   Real-time updates
-   Advanced AI analytics

------------------------------------------------------------------------

# Final Architecture

    React Dashboard

            |

    Spring Boot Backend

            |
     -------------------------
     |                       |
    Market Service       AI Service
     |                       |
    Yahoo Finance        Gemini API
     |                       |
    Investment DB      Portfolio Insights
