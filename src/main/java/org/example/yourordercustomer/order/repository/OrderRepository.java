package org.example.yourordercustomer.order.repository;

import org.example.yourordercustomer.order.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    Page<OrderEntity> findByUserId(UUID userId, Pageable pageable);

    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.userId = :userId")
    List<OrderEntity> findByUserIdWithItems(@Param("userId") UUID userId);
    Optional<OrderEntity> findByIdAndUserId(UUID id, UUID userId);

    @Query("""
            SELECT DISTINCT o FROM OrderEntity o
            JOIN o.items i
            JOIN i.product p
            WHERE p.companyId = :companyId
            ORDER BY o.createdAt DESC
            """)
    Page<OrderEntity> findSalesByCompanyId(@Param("companyId") UUID companyId, Pageable pageable);
}
