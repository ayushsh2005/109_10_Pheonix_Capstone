package com.backend.controller;

import com.backend.dto.SuggestionDTO;
import com.backend.service.SuggestionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping("/suggestions")
    public List<SuggestionDTO> getAllSuggestions() {
        return suggestionService.getAllSuggestions();
    }

    @GetMapping("/customers/{id}/suggestions")
    public List<SuggestionDTO> getSuggestionsByCustomer(@PathVariable Long id) {
        return suggestionService.getSuggestionsByCustomer(id);
    }
}
