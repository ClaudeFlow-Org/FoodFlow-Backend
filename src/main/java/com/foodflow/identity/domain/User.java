package com.foodflow.identity.domain;

import com.foodflow.common.domain.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UserId id;
    private String name;
    private String email;
    private String password;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateProfile(String name, String email) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (email != null && !email.isBlank()) {
            this.email = email;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new ValidationException("Password cannot be empty");
        }
        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfileImageUrl(String imageUrl) {
        this.profileImageUrl = imageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public record UserId(Long value) {
        public static UserId of(Long value) {
            return new UserId(value);
        }

        public static UserId empty() {
            return new UserId(null);
        }
    }
}
