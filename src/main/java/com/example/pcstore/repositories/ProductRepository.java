package com.example.pcstore.repositories;

import com.example.pcstore.constant.Constant;
import com.example.pcstore.entity.Product;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
    Optional<Product> findProductById(String id);

    @Query(value = "select count(*) from product where category_id = :categoryId", nativeQuery = true)
    int getCountByCategoryId(String categoryId);

    @NonNull
    @Override
    @Cacheable(value = "products", keyGenerator = Constant.KEY_GENERATOR)
    List<Product> findAll(@NonNull Specification<Product> specification);

    @NonNull
    @Override
    Product save(@NonNull Product product);

    @Query(value = "SELECT * FROM product WHERE quantity <= 5 AND quantity > 0 ORDER BY quantity ASC", nativeQuery = true)
    List<Product> findLowStockProducts();
}
