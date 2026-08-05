package com.backend;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

class BackendApplicationMainTest {

    @Test
    void main_invokesSpringApplicationRun() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            String[] args = new String[]{"--server.port=0"};

            BackendApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(eq(BackendApplication.class), eq(args)));
        }
    }
}

