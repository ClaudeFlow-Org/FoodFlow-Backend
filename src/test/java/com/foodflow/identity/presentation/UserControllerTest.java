package com.foodflow.identity.presentation;

import com.foodflow.common.presentation.GlobalExceptionHandler;
import com.foodflow.identity.application.IdentityApplicationService;
import com.foodflow.identity.application.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static com.foodflow.ControllerTestSupport.AUTHENTICATED_USER_ID;
import static com.foodflow.ControllerTestSupport.authenticatedUserArgumentResolver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IdentityApplicationService identityApplicationService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserController(identityApplicationService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(authenticatedUserArgumentResolver())
                .build();
    }

    // Prueba integral: obtiene perfil del usuario autenticado (BE-INT-019)
    @Test
    void getsAuthenticatedUserProfile() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(AUTHENTICATED_USER_ID)
                .name("Owner FoodFlow")
                .email("owner@foodflow.test")
                .subscriptionType("FREE")
                .createdAt(LocalDateTime.of(2026, 5, 10, 9, 0))
                .build();
        when(identityApplicationService.getProfile(AUTHENTICATED_USER_ID)).thenReturn(response);

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(AUTHENTICATED_USER_ID))
                .andExpect(jsonPath("$.data.email").value("owner@foodflow.test"))
                .andExpect(jsonPath("$.data.subscriptionType").value("FREE"));

        verify(identityApplicationService).getProfile(AUTHENTICATED_USER_ID);
    }
    // fin prueba

    // Prueba integral: actualiza perfil con nombre y correo validos (BE-INT-020)
    @Test
    void updatesAuthenticatedUserProfile() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(AUTHENTICATED_USER_ID)
                .name("Owner Updated")
                .email("updated@foodflow.test")
                .subscriptionType("STANDARD")
                .build();
        when(identityApplicationService.updateProfile(eq(AUTHENTICATED_USER_ID), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Owner Updated",
                                  "email": "updated@foodflow.test"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Profile updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Owner Updated"))
                .andExpect(jsonPath("$.data.email").value("updated@foodflow.test"));

        ArgumentCaptor<com.foodflow.identity.application.UpdateProfileRequest> requestCaptor =
                ArgumentCaptor.forClass(com.foodflow.identity.application.UpdateProfileRequest.class);
        verify(identityApplicationService).updateProfile(eq(AUTHENTICATED_USER_ID), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getName()).isEqualTo("Owner Updated");
        assertThat(requestCaptor.getValue().getEmail()).isEqualTo("updated@foodflow.test");
    }
    // fin prueba

    // Prueba integral: cambia password del usuario autenticado (BE-INT-021)
    @Test
    void updatesAuthenticatedUserPassword() throws Exception {
        mockMvc.perform(put("/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "currentPassword": "secret1",
                                  "newPassword": "secret2"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password updated successfully"));

        ArgumentCaptor<com.foodflow.identity.application.UpdatePasswordRequest> requestCaptor =
                ArgumentCaptor.forClass(com.foodflow.identity.application.UpdatePasswordRequest.class);
        verify(identityApplicationService).updatePassword(eq(AUTHENTICATED_USER_ID), requestCaptor.capture());
        assertThat(requestCaptor.getValue().getCurrentPassword()).isEqualTo("secret1");
        assertThat(requestCaptor.getValue().getNewPassword()).isEqualTo("secret2");
    }
    // fin prueba

    // Prueba integral: rechaza perfil y password con datos invalidos (BE-INT-022)
    @Test
    void rejectsInvalidProfileAndPasswordPayloads() throws Exception {
        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "A",
                                  "email": "bad-email"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());

        mockMvc.perform(put("/api/users/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "currentPassword": "",
                                  "newPassword": "123"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }
    // fin prueba
}
