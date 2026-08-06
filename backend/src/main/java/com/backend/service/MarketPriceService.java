package com.backend.service;

import com.backend.client.YahooFinanceClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Fetches current market prices for investment tickers, converting the
 * database ticker format (e.g. "RELIANCE") to Yahoo Finance's NSE format
 * (e.g. "RELIANCE.NS") before delegating to {@link YahooFinanceClient}.
 */
@Service
public class MarketPriceService {

    private final YahooFinanceClient yahooFinanceClient;

    public MarketPriceService(YahooFinanceClient yahooFinanceClient) {
        this.yahooFinanceClient = yahooFinanceClient;
    }

    /**
     * Returns the latest market price for {@code ticker}, or empty if it could
     * not be fetched (blank ticker, upstream failure, etc.). Callers should
     * keep using the previously known price when this returns empty.
     */
    public Optional<BigDecimal> getLatestPrice(String ticker) {
        if (ticker == null || ticker.isBlank()) {
            return Optional.empty();
        }
        return yahooFinanceClient.fetchLatestPrice(toYahooTicker(ticker));
    }

    /** Converts a database ticker to the Yahoo Finance NSE format, e.g. RELIANCE -> RELIANCE.NS. */
    public String toYahooTicker(String ticker) {
        String normalized = ticker.trim().toUpperCase();
        return normalized.contains(".") ? normalized : normalized + ".NS";
    }
}
