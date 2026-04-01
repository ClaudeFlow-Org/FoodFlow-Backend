package com.foodflow.inventory.infrastructure;

import com.foodflow.inventory.domain.Product;
import com.foodflow.inventory.domain.ProductRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductMapper productMapper;

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = productMapper.toEntity(product);
        ProductJpaEntity savedEntity = jpaRepository.save(entity);
        return productMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(Product.ProductId id) {
        return jpaRepository.findById(id.value())
                .map(productMapper::toDomain);
    }

    @Override
    public List<Product> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(productMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(Product.ProductId id) {
        jpaRepository.deleteById(id.value());
    }

    @Override
    public boolean existsByUserIdAndName(Long userId, String name) {
        return jpaRepository.existsByUserIdAndName(userId, name);
    }
}
