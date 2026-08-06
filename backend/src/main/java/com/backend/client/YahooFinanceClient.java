package com.backend.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Thin client around the (unofficial, key-free) Yahoo Finance "chart" endpoint.
 * Used to fetch the latest market price for a given ticker, e.g. RELIANCE.NS.
 *
 * On any failure (timeout, non-200, malformed body) this returns
 * {@link Optional#empty()} so callers can fall back to the previously known
 * price, per the error-handling strategy in MARKET_DATA_INTEGRATION_GEMINI.md.
 */
@Component
public class YahooFinanceClient {

    private static final Logger log = LoggerFactory.getLogger(YahooFinanceClient.class);

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String baseUrl;
    private final int timeoutSeconds;
    private final int retries;

    public YahooFinanceClient(ObjectMapper objectMapper,
            @Value("${market.yahoo.base-url:https://query1.finance.yahoo.com/v8/finance/chart}") String baseUrl,
            @Value("${market.request.timeout-seconds:5}") int timeoutSeconds,
            @Value("${market.request.retries:2}") int retries) {
        this.objectMapper = objectMapper;
        this.baseUrl = baseUrl;
        this.timeoutSeconds = timeoutSeconds;
        this.retries = retries;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }

    /**
     * Fetches the latest regular market price for the given Yahoo-formatted ticker
     * (e.g. "RELIANCE.NS"). Retries up to {@code market.request.retries} times on failure.
     */
    public Optional<BigDecimal> fetchLatestPrice(String yahooTicker) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + yahooTicker))
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .header("User-Agent", "Mozilla/5.0 (compatible; PortfolioManager/1.0)")
                .header("Accept", "application/json")
                .GET()
                .build();

        for (int attempt = 0; attempt <= retries; attempt++) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    Optional<BigDecimal> price = parsePrice(response.body());
                    if (price.isPresent()) {
                        return price;
                    }
                    log.warn("Yahoo Finance response for {} did not contain a usable price", yahooTicker);
                    return Optional.empty();
                }
                log.warn("Yahoo Finance returned status {} for ticker {} (attempt {}/{})",
                        response.statusCode(), yahooTicker, attempt + 1, retries + 1);
            } catch (IOException ex) {
                log.warn("Yahoo Finance request failed for ticker {} (attempt {}/{}): {}",
                        yahooTicker, attempt + 1, retries + 1, ex.getMessage());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private Optional<BigDecimal> parsePrice(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode results = root.path("chart").path("result");
            if (!results.isArray() || results.isEmpty()) {
                return Optional.empty();
            }
            JsonNode meta = results.get(0).path("meta");
            JsonNode priceNode = meta.path("regularMarketPrice");
            if (priceNode.isMissingNode() || priceNode.isNull()) {
                return Optional.empty();
            }
            return Optional.of(BigDecimal.valueOf(priceNode.asDouble()));
        } catch (Exception ex) {
            log.warn("Failed to parse Yahoo Finance response: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
