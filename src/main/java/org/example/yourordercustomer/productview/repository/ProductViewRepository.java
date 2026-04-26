package org.example.yourordercustomer.productview.repository;

import org.example.yourordercustomer.productview.entity.ProductViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductViewRepository extends JpaRepository<ProductViewEntity, UUID> {
}