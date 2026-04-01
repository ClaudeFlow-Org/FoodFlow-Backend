package com.foodflow.identity.infrastructure;

import com.foodflow.identity.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserJpaEntity entity) {
        return User.builder()
                .id(User.UserId.of(entity.getId()))
                .name(entity.getName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .profileImageUrl(entity.getProfileImageUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserJpaEntity toEntity(User domain) {
        UserJpaEntityBuilder builder = UserJpaEntity.builder()
                .name(domain.getName())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .profileImageUrl(domain.getProfileImageUrl())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt());

        if (domain.getId().value() != null) {
            builder.id(domain.getId().value());
        }

        return builder.build();
    }
}
