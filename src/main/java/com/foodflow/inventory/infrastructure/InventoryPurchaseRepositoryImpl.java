package com.foodflow.inventory.infrastructure;

import com.foodflow.inventory.domain.InventoryPurchase;
import com.foodflow.inventory.domain.InventoryPurchaseRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Repository
@RequiredArgsConstructor
public class InventoryPurchaseRepositoryImpl implements InventoryPurchaseRepository {

    private final InventoryPurchaseJpaRepository jpaRepository;
    private final InventoryPurchaseMapper mapper;

    @Override
    public InventoryPurchase save(InventoryPurchase purchase) {
        InventoryPurchaseJpaEntity entity = mapper.toEntity(purchase);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<InventoryPurchase> findByUserIdAndPurchasedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.findByUserIdAndPurchasedAtBetween(userId, startDate, endDate).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUserIdAndProductId(Long userId, Long productId) {
        return jpaRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public void updateProductCategory(Long userId, Long productId, String categoryName) {
        jpaRepository.updateProductCategory(userId, productId, categoryName);
    }

    @Override
    public void renameCategory(Long userId, String previousName, String newName) {
        jpaRepository.renameCategory(userId, previousName, newName);
    }

    @Override
    public void clearCategory(Long userId, String categoryName) {
        jpaRepository.clearCategory(userId, categoryName);
    }
}
