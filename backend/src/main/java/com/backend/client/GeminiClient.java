package com.backend.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Thin client around the Google Gemini "generateContent" REST API.
 *
 * Requires {@code gemini.api.key} to be configured; when it is blank/missing,
 * {@link #isConfigured()} returns false and callers should use a rule-based
 * fallback instead (per MARKET_DATA_INTEGRATION_GEMINI.md section 11).
 */
@Component
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiUrl;
    private final String apiKey;
    private final int timeoutSeconds;

    public GeminiClient(ObjectMapper objectMapper,
            @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent}") String apiUrl,
            @Value("${gemini.api.key:}") String apiKey,
            @Value("${gemini.request.timeout-seconds:5}") int timeoutSeconds) {
        this.objectMapper = objectMapper;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.timeoutSeconds = timeoutSeconds;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }

    /** True when an API key has been configured (via GEMINI_API_KEY / gemini.api.key). */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    /**
     * Sends {@code prompt} to Gemini and returns the raw text of the first candidate.
     * Returns {@link Optional#empty()} if not configured, on timeout, or any error -
     * callers are expected to fall back to rule-based suggestions in that case.
     */
    public Optional<String> generateContent(String prompt) {
        if (!isConfigured()) {
            return Optional.empty();
        }

        try {
            Map<String, Object> body = Map.of(
                    "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
            );
            String json = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl + "?key=" + apiKey))
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.warn("Gemini API returned status {}: {}", response.statusCode(), response.body());
                return Optional.empty();
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode textNode = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");
            if (textNode.isMissingNode() || textNode.isNull()) {
                return Optional.empty();
            }
            return Optional.of(textNode.asString());
        } catch (IOException ex) {
            log.warn("Gemini API request failed: {}", ex.getMessage());
            return Optional.empty();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (Exception ex) {
            log.warn("Failed to parse Gemini API response: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
