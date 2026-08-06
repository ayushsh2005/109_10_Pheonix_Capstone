package com.backend.service;

import com.backend.client.GeminiClient;
import com.backend.dto.AiSuggestionResponseDTO;
import com.backend.dto.SuggestionDTO;
import com.backend.entity.Customer;
import com.backend.entity.Investment;
import com.backend.exception.CustomerNotFoundException;
import com.backend.repository.CustomerRepository;
import com.backend.repository.InvestmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Produces AI-powered portfolio suggestions using the Gemini API, as described
 * in MARKET_DATA_INTEGRATION_GEMINI.md sections 6-9.
 *
 * Falls back to the existing rule-based {@link SuggestionService} whenever
 * Gemini is not configured, times out, or returns an unparseable response.
 */
@Service
public class AiSuggestionService {

    private static final Logger log = LoggerFactory.getLogger(AiSuggestionService.class);

    private final CustomerRepository customerRepository;
    private final InvestmentRepository investmentRepository;
    private final GeminiClient geminiClient;
    private final SuggestionService suggestionService;
    private final ObjectMapper objectMapper;

    public AiSuggestionService(CustomerRepository customerRepository, InvestmentRepository investmentRepository,
                                GeminiClient geminiClient, SuggestionService suggestionService,
                                ObjectMapper objectMapper) {
        this.customerRepository = customerRepository;
        this.investmentRepository = investmentRepository;
        this.geminiClient = geminiClient;
        this.suggestionService = suggestionService;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public AiSuggestionResponseDTO getAiSuggestions(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        List<Investment> investments = investmentRepository.findByPortfolioCustomerId(customerId);

        if (geminiClient.isConfigured() && !investments.isEmpty()) {
            Optional<AiSuggestionResponseDTO> aiResponse = tryGemini(customer, investments);
            if (aiResponse.isPresent()) {
                return aiResponse.get();
            }
        }
        return fallback(customerId, customer);
    }

    private Optional<AiSuggestionResponseDTO> tryGemini(Customer customer, List<Investment> investments) {
        String prompt = buildPrompt(customer, investments);
        Optional<String> raw = geminiClient.generateContent(prompt);
        if (raw.isEmpty()) {
            return Optional.empty();
        }
        return parseResponse(customer.getId(), raw.get());
    }

    private String buildPrompt(Customer customer, List<Investment> investments) {
        String holdings = investments.stream()
                .map(i -> "- " + i.getAssetName() + " (" + i.getAssetType() + "), qty=" + i.getQuantity()
                        + ", currentValue=" + i.getQuantity().multiply(i.getCurrentPrice() != null
                                ? i.getCurrentPrice() : BigDecimal.ZERO))
                .collect(Collectors.joining("\n"));

        return """
                You are a portfolio analysis assistant for an investment manager. Analyse the following \
                client portfolio and respond with ONLY a single JSON object (no markdown fences, no extra text) \
                matching exactly this shape:
                {"summary": "<one paragraph summary>", "suggestions": ["<suggestion 1>", "<suggestion 2>"], "riskLevel": "LOW|MEDIUM|HIGH"}

                Client risk profile: %s
                Investment goal: %s
                Portfolio holdings:
                %s
                """.formatted(customer.getRiskProfile(), customer.getInvestmentGoal(), holdings);
    }

    private Optional<AiSuggestionResponseDTO> parseResponse(Long customerId, String raw) {
        try {
            String cleaned = raw.trim();
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.replaceAll("^```[a-zA-Z]*\\n?", "").replaceAll("```$", "").trim();
            }
            JsonNode root = objectMapper.readTree(cleaned);

            String summary = root.path("summary").asString("");
            String riskLevel = root.path("riskLevel").asString("MEDIUM");

            List<String> suggestions = new ArrayList<>();
            JsonNode suggestionsNode = root.path("suggestions");
            if (suggestionsNode.isArray()) {
                suggestionsNode.forEach(node -> suggestions.add(node.asString("")));
            }

            if (summary.isBlank() && suggestions.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(new AiSuggestionResponseDTO(customerId, summary, suggestions, riskLevel, "AI"));
        } catch (Exception ex) {
            log.warn("Could not parse Gemini response for customer {}: {}", customerId, ex.getMessage());
            return Optional.empty();
        }
    }

    private AiSuggestionResponseDTO fallback(Long customerId, Customer customer) {
        List<SuggestionDTO> ruleBased = suggestionService.getSuggestionsByCustomer(customerId);
        List<String> messages = ruleBased.stream().map(SuggestionDTO::getMessage).collect(Collectors.toList());

        String summary = messages.isEmpty()
                ? "Portfolio looks balanced for a " + customer.getRiskProfile() + " risk profile; no immediate concerns detected."
                : "AI suggestions are unavailable right now, showing rule-based analysis instead.";

        String riskLevel = ruleBased.stream().anyMatch(s -> "High".equalsIgnoreCase(s.getSeverity())) ? "HIGH"
                : ruleBased.stream().anyMatch(s -> "Medium".equalsIgnoreCase(s.getSeverity())) ? "MEDIUM" : "LOW";

        return new AiSuggestionResponseDTO(customerId, summary, messages, riskLevel, "RULE_BASED");
    }
}
