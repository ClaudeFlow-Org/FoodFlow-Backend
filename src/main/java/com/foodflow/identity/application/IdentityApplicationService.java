package com.foodflow.identity.application;

import com.foodflow.common.domain.DuplicateResourceException;
import com.foodflow.common.domain.NotFoundException;
import com.foodflow.common.domain.UnauthorizedException;
import com.foodflow.common.domain.ValidationException;
import com.foodflow.identity.domain.Email;
import com.foodflow.identity.domain.Password;
import com.foodflow.identity.domain.User;
import com.foodflow.identity.domain.UserRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class IdentityApplicationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email " + request.getEmail());
        }

        Email email = Email.of(request.getEmail());
        Password password = Password.of(request.getPassword());

        String encodedPassword = passwordEncoder.encode(password.value());

        User user = User.builder()
                .id(User.UserId.empty())
                .name(request.getName())
                .email(email.value())
                .password(encodedPassword)
                .profileImageUrl(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getId().value());

        return LoginResponse.builder()
                .token(token)
                .user(toResponse(user))
                .build();
    }

    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(User.UserId.of(userId))
                .orElseThrow(() -> new NotFoundException("User", "id " + userId));

        return toResponse(user);
    }

    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(User.UserId.of(userId))
                .orElseThrow(() -> new NotFoundException("User", "id " + userId));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("User", "email " + request.getEmail());
            }
        }

        user.updateProfile(request.getName(), request.getEmail());
        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(User.UserId.of(userId))
                .orElseThrow(() -> new NotFoundException("User", "id " + userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ValidationException("currentPassword", "Current password is incorrect");
        }

        Password newPassword = Password.of(request.getNewPassword());
        user.updatePassword(passwordEncoder.encode(newPassword.value()));

        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId().value())
                .name(user.getName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
