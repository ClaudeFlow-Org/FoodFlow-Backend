package com.foodflow.inventory.domain;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Product.ProductId id);

    List<Product> findByUserId(Long userId);

    void delete(Product.ProductId id);

    boolean existsByUserIdAndName(Long userId, String name);
}
