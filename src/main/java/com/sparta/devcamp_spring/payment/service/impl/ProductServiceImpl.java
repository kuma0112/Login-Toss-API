package com.sparta.devcamp_spring.payment.service.impl;

import com.sparta.devcamp_spring.payment.entity.Product;
import com.sparta.devcamp_spring.payment.repository.ProductRepository;
import com.sparta.devcamp_spring.payment.service.ProductService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Product getProductById(Long productId) throws Exception {
        return productRepository.findById(productId).orElseThrow(Exception::new);
    }

    @Override
    public List<Product> getProductsByIds(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }

    @Override
    public void decreaseStockQuantity(Map<Product, Integer> productQuantityMap) throws Exception {
        List<Product> result = productQuantityMap.entrySet().stream().map(entry -> {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            try {
                product.decreaseStock(quantity);
                return product;
            } catch (Exception e) {
                throw new RuntimeException("주문을 위해 상품 재고를 업데이트 하는데 실패했습니다 : " + product.getName(), e);
            }
        }).collect(Collectors.toList());

        productRepository.saveAll(result);
    }

    @Override
    public void increaseStockQuantity(Map<Product, Integer> productQuantityMap) throws Exception {
        List<Product> result = productQuantityMap.entrySet().stream().map(entry -> {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            try {
                product.increaseStock(quantity);
                return product;
            } catch (Exception e) {
                throw new RuntimeException("주문을 취소로 인한 상품 재고를 업데이트 하는데 실패했습니다 : " + product.getName(), e);
            }
        }).collect(Collectors.toList());

        productRepository.saveAll(result);
    }

}
