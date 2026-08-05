package com.backend;

import com.backend.repository.CustomerRepository;
import com.backend.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CustomerControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void createCustomer_createsCustomerAndPortfolio() throws Exception {
        String payload = """
                {
                  "name": "Alice Walker",
                  "email": "alice.walker@example.com",
                  "phone": "1234567890",
                  "riskProfile": "Moderate",
                  "investmentGoal": "Retirement"
                }
                """;

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice Walker"))
                .andExpect(jsonPath("$.email").value("alice.walker@example.com"))
                .andExpect(jsonPath("$.status").value("Active"))
                .andExpect(jsonPath("$.portfolioValue").value(0));

        Long id = customerRepository.findByEmail("alice.walker@example.com").orElseThrow().getId();
        assertThat(customerRepository.findById(id)).isPresent();
        assertThat(portfolioRepository.findByCustomerId(id)).isPresent();
    }

    @Test
    void createCustomer_withDuplicateEmail_returnsBadRequest() throws Exception {
        String payload = """
                {
                  "name": "John Doe",
                  "email": "john.doe@example.com",
                  "phone": "1234567890",
                  "riskProfile": "Aggressive",
                  "investmentGoal": "Growth"
                }
                """;

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is already in use: john.doe@example.com"));
    }

    @Test
    void createCustomer_withValidationError_returnsFieldErrors() throws Exception {
        String invalidPayload = """
                {
                  "name": "",
                  "email": "not-an-email"
                }
                """;

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.name").value("Name is required"))
                .andExpect(jsonPath("$.fieldErrors.email").value("Email must be valid"));
    }

    @Test
    void getUnknownCustomer_returnsNotFound() throws Exception {
        mockMvc.perform(get("/customers/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found with id: 99999"));
    }

    @Test
    void deleteCustomer_removesCustomer() throws Exception {
        String payload = """
                {
                  "name": "Delete Me",
                  "email": "delete.me@example.com",
                  "phone": "0001112222",
                  "riskProfile": "Conservative",
                  "investmentGoal": "Safety"
                }
                """;

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        Long id = customerRepository.findByEmail("delete.me@example.com").orElseThrow().getId();

        mockMvc.perform(delete("/customers/{id}", id))
                .andExpect(status().isNoContent());

        assertThat(customerRepository.findById(id)).isEmpty();
    }

    @Test
    void getCustomers_andUpdateCustomer_coverJsonAndTextPlainPaths() throws Exception {
        String createPayload = """
                {
                  "name": "Mira Hall",
                  "email": "mira.hall@example.com",
                  "phone": "9991112222",
                  "riskProfile": "Moderate",
                  "investmentGoal": "Income"
                }
                """;

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isCreated());

        Long id = customerRepository.findByEmail("mira.hall@example.com").orElseThrow().getId();

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());

        String updateJson = """
                {
                  "name": "Mira Hall Updated",
                  "email": "mira.hall@example.com",
                  "phone": "9991112223",
                  "riskProfile": "Aggressive",
                  "investmentGoal": "Growth"
                }
                """;

        mockMvc.perform(put("/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mira Hall Updated"))
                .andExpect(jsonPath("$.riskProfile").value("Aggressive"));

        mockMvc.perform(get("/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mira Hall Updated"));

        String updateText = """
                {
                  "name": "Mira Hall Text",
                  "email": "mira.hall@example.com",
                  "phone": "9991112224",
                  "riskProfile": "Conservative",
                  "investmentGoal": "Preserve Capital"
                }
                """;

        mockMvc.perform(put("/customers/{id}", id)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(updateText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mira Hall Text"))
                .andExpect(jsonPath("$.riskProfile").value("Conservative"));
    }

    @Test
    void createCustomerFromText_andInvalidPayload_coverErrorHandlers() throws Exception {
        String textPayload = """
                {
                  "name": "Text Customer",
                  "email": "text.customer@example.com",
                  "phone": "1010101010",
                  "riskProfile": "Moderate",
                  "investmentGoal": "Retirement"
                }
                """;

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(textPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("text.customer@example.com"));

        Long id = customerRepository.findByEmail("text.customer@example.com").orElseThrow().getId();

        mockMvc.perform(put("/customers/{id}", id)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request body. Expected JSON payload."));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_XML)
                        .content("<customer></customer>"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.message").value("Unsupported Content-Type. Use application/json."));
    }
}

