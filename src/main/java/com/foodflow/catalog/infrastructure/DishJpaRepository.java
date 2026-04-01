package com.foodflow.catalog.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishJpaRepository extends JpaRepository<DishJpaEntity, Long> {

    List<DishJpaEntity> findByUserId(Long userId);

    @Query("SELECT d FROM DishJpaEntity d WHERE d.userId = :userId AND LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<DishJpaEntity> findByUserIdAndNameContaining(@Param("userId") Long userId, @Param("name") String name);

    boolean existsByUserIdAndName(Long userId, String name);
}
