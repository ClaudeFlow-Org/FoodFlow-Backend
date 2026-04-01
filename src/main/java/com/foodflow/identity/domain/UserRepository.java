package com.foodflow.identity.domain;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(User.UserId id);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    void delete(User.UserId id);
}
