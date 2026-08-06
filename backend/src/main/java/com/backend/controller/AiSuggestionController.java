package com.backend.controller;

import com.backend.dto.AiSuggestionResponseDTO;
import com.backend.service.AiSuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "AI Suggestions", description = "Gemini-powered portfolio suggestion APIs")
public class AiSuggestionController {

    private final AiSuggestionService aiSuggestionService;

    public AiSuggestionController(AiSuggestionService aiSuggestionService) {
        this.aiSuggestionService = aiSuggestionService;
    }

    @GetMapping("/customers/{customerId}/ai-suggestions")
    @Operation(summary = "Get AI portfolio suggestions",
            description = "Returns Gemini-generated portfolio insights for a customer, falling back to rule-based suggestions if Gemini is unavailable")
    @Parameter(name = "customerId", description = "Customer ID", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suggestions fetched",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AiSuggestionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    public AiSuggestionResponseDTO getAiSuggestions(@PathVariable Long customerId) {
        return aiSuggestionService.getAiSuggestions(customerId);
    }
}
