package com.sparta.devcamp_spring.payment.service;



import com.sparta.devcamp_spring.payment.entity.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {
    /**
     * productId로 product 엔티티를 조회한다.
     * @param productId
     * @return
     */
    Product getProductById(Long productId) throws Exception;
    /**
     * productId로 products 엔티티를 조회한다.
     *
     * @param productIds product entity pk
     * @return products entity
     */
    List<Product> getProductsByIds(List<Long> productIds) throws Exception;

    /**
     * 재고 수량을 내린다.
     * @param productQuantityMap 상품 id와 재고정보
     * @throws Exception exception
     */
    void decreaseStockQuantity(Map<Product, Integer> productQuantityMap) throws Exception;

    /**
     * 재고 수량을 복구한다.
     * @param productQuantityMap 상품 id와 재고정보
     * @throws Exception exception
     */
    void increaseStockQuantity(Map<Product, Integer> productQuantityMap) throws Exception;
}
