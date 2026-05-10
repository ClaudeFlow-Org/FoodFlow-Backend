package com.foodflow.identity.presentation;

import com.foodflow.common.presentation.GlobalExceptionHandler;
import com.foodflow.identity.application.IdentityApplicationService;
import com.foodflow.identity.application.LoginResponse;
import com.foodflow.identity.application.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IdentityApplicationService identityApplicationService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(identityApplicationService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // Prueba integral: registra usuario desde contrato HTTP (BE-INT-007)
    @Test
    void registersUser() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(15L)
                .name("Owner FoodFlow")
                .email("owner@foodflow.test")
                .subscriptionType("FREE")
                .createdAt(LocalDateTime.of(2026, 5, 9, 9, 0))
                .build();
        when(identityApplicationService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Owner FoodFlow",
                                  "email": "owner@foodflow.test",
                                  "password": "secret1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.email").value("owner@foodflow.test"))
                .andExpect(jsonPath("$.data.subscriptionType").value("FREE"));
    }
    // fin prueba

    // Prueba integral: inicia sesion y devuelve token (BE-INT-008)
    @Test
    void logsInUser() throws Exception {
        UserResponse user = UserResponse.builder()
                .id(15L)
                .name("Owner FoodFlow")
                .email("owner@foodflow.test")
                .subscriptionType("STANDARD")
                .build();
        when(identityApplicationService.login(any())).thenReturn(LoginResponse.builder()
                .token("jwt-token")
                .user(user)
                .build());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "owner@foodflow.test",
                                  "password": "secret1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.user.subscriptionType").value("STANDARD"));

        verify(identityApplicationService).login(any());
    }
    // fin prueba

    // Prueba integral: rechaza registro con datos invalidos (BE-INT-009)
    @Test
    void rejectsInvalidRegistrationPayload() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "email": "owner-foodflow.test",
                                  "password": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }
    // fin prueba
}
