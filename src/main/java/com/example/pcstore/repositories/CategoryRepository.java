package com.example.pcstore.repositories;

import com.example.pcstore.constant.Constant;
import com.example.pcstore.entity.Category;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String>, JpaSpecificationExecutor<Category> {
    boolean existsByName(String name);

    @Cacheable(value = "categories", keyGenerator = Constant.KEY_GENERATOR)
    @Query(value = "select * from category where is_active = true order by name", nativeQuery = true)
    List<Category> getAllCategoryActive();

    @Query(value = "select * from category where id = :id", nativeQuery = true)
    Category findCategoryBy(String id);
}
