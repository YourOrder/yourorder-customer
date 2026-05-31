package org.example.yourordercustomer.productview.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "product_view")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    @Builder.Default
    private Integer reservedQuantity = 0;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
