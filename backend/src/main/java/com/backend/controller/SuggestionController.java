package com.backend.controller;

import com.backend.dto.SuggestionDTO;
import com.backend.service.SuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Suggestions", description = "Investment suggestion APIs")
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping("/suggestions")
    @Operation(summary = "List all suggestions", description = "Returns investment suggestions for all customers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suggestions fetched",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = SuggestionDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public List<SuggestionDTO> getAllSuggestions() {
        return suggestionService.getAllSuggestions();
    }

    @GetMapping("/customers/{id}/suggestions")
    @Operation(summary = "List customer suggestions", description = "Returns investment suggestions for a specific customer")
    @Parameter(name = "id", description = "Customer ID", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suggestions fetched",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = SuggestionDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    public List<SuggestionDTO> getSuggestionsByCustomer(@PathVariable Long id) {
        return suggestionService.getSuggestionsByCustomer(id);
    }
}
