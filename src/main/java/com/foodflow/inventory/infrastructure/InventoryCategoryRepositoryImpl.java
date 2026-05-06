package com.foodflow.inventory.infrastructure;

import com.foodflow.inventory.domain.InventoryCategory;
import com.foodflow.inventory.domain.InventoryCategoryRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
public class InventoryCategoryRepositoryImpl implements InventoryCategoryRepository {

    private final InventoryCategoryJpaRepository jpaRepository;
    private final InventoryCategoryMapper mapper;

    @Override
    public InventoryCategory save(InventoryCategory category) {
        InventoryCategoryJpaEntity entity = mapper.toEntity(category);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<InventoryCategory> findById(InventoryCategory.InventoryCategoryId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<InventoryCategory> findByUserId(Long userId) {
        return jpaRepository.findByUserIdOrderByNameAsc(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<InventoryCategory> findByUserIdAndName(Long userId, String name) {
        return jpaRepository.findByUserIdAndNameIgnoreCase(userId, name).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndName(Long userId, String name) {
        return jpaRepository.existsByUserIdAndNameIgnoreCase(userId, name);
    }

    @Override
    public void delete(InventoryCategory.InventoryCategoryId id) {
        jpaRepository.deleteById(id.value());
    }
}
