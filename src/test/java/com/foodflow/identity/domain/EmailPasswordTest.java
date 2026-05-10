package com.foodflow.identity.domain;

import com.foodflow.common.domain.ValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailPasswordTest {

    // Prueba unitaria: acepta correo valido de propietario (BE-UT-001)
    @Test
    void acceptsValidOwnerEmail() {
        Email email = Email.of("owner@foodflow.test");

        assertThat(email.value()).isEqualTo("owner@foodflow.test");
    }
    // fin prueba

    // Prueba unitaria: rechaza correo invalido de login (BE-UT-002)
    @Test
    void rejectsInvalidEmailFormat() {
        assertThatThrownBy(() -> Email.of("owner-foodflow.test"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("email: Invalid email format");
    }
    // fin prueba

    // Prueba unitaria: acepta password con largo minimo (BE-UT-003)
    @Test
    void acceptsPasswordWithMinimumLength() {
        Password password = Password.of("secret");

        assertThat(password.value()).isEqualTo("secret");
    }
    // fin prueba

    // Prueba unitaria: rechaza password demasiado corto (BE-UT-004)
    @Test
    void rejectsShortPassword() {
        assertThatThrownBy(() -> Password.of("12345"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("password: Password must be at least 6 characters");
    }
    // fin prueba
}
