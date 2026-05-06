package com.foodflow.inventory.infrastructure;

import com.foodflow.inventory.domain.InventoryPurchase;
import com.foodflow.inventory.domain.InventoryPurchaseRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    public Set<Long> findProductIdsWithPurchases(Long userId) {
        return jpaRepository.findProductIdsWithPurchases(userId);
    }
}
