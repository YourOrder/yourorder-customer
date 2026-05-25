package org.example.yourordercustomer.productview.controller;

import lombok.RequiredArgsConstructor;
import org.example.yourordercustomer.exception.NotFoundException;
import org.example.yourordercustomer.productview.entity.ProductViewEntity;
import org.example.yourordercustomer.productview.repository.ProductViewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/customer/products")
@RequiredArgsConstructor
public class CustomerProductController {

    private final ProductViewRepository productViewRepository;

    @GetMapping
    public Page<ProductViewEntity> getProducts(
            @RequestParam(required = false) UUID companyId,
            Pageable pageable
    ) {
        if (companyId != null) {
            return productViewRepository.findByCompanyId(companyId, pageable);
        }
        return productViewRepository.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ProductViewEntity getProduct(@PathVariable UUID id) {
        return productViewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }
}

