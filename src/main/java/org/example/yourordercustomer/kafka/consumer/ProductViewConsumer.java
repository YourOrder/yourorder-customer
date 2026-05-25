package org.example.yourordercustomer.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.yourordercustomer.kafka.event.ProductEvent;
import org.example.yourordercustomer.productview.entity.ProductViewEntity;
import org.example.yourordercustomer.productview.repository.ProductViewRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductViewConsumer {

    private final ProductViewRepository productViewRepository;

    @KafkaListener(topics = "${kafka.topics.product-created}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void onProductCreated(ProductEvent event) {
        log.info("product.created: id={}", event.id());
        productViewRepository.findById(event.id()).ifPresentOrElse(
                existing -> log.warn("ProductView already exists: {}", event.id()),
                () -> productViewRepository.save(toEntity(event))
        );
    }

    @KafkaListener(topics = "${kafka.topics.product-updated}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void onProductUpdated(ProductEvent event) {
        log.info("product.updated: id={}", event.id());
        productViewRepository.findById(event.id()).ifPresentOrElse(
                existing -> {
                    existing.setName(event.name());
                    existing.setPrice(event.price());
                    existing.setCompanyId(event.companyId());
                    existing.setUpdatedAt(LocalDateTime.now());
                    productViewRepository.save(existing);
                },
                () -> {
                    log.warn("ProductView not found on update, inserting: {}", event.id());
                    productViewRepository.save(toEntity(event));
                }
        );
    }

    @KafkaListener(topics = "${kafka.topics.product-deleted}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void onProductDeleted(ProductEvent event) {
        log.info("product.deleted: id={}", event.id());
        productViewRepository.deleteById(event.id());
    }

    private ProductViewEntity toEntity(ProductEvent event) {
        return ProductViewEntity.builder()
                .id(event.id())
                .name(event.name())
                .price(event.price())
                .companyId(event.companyId())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

