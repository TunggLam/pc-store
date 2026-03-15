package com.example.pcstore.controller;

import com.example.pcstore.aop.Secured;
import com.example.pcstore.enums.RoleEnum;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.request.CreateCategoryRequest;
import com.example.pcstore.model.request.UpdateCategoryRequest;
import com.example.pcstore.model.response.CategoriesResponse;
import com.example.pcstore.model.response.CategoryResponse;
import com.example.pcstore.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Category Controller", description = "Danh sách API phục vụ loại sản phẩm")
public class CategoryController {

    private static final Logger LOGGER = LoggingFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<CategoriesResponse> findAll(@RequestParam(value = "productCount", required = false) boolean productCount) {
        return ResponseEntity.ok(categoryService.findAll(productCount));
    }

    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/category/{id}")
    public ResponseEntity<CategoryResponse> categoryDetail(@PathVariable("id") String id) {
        return ResponseEntity.ok(categoryService.detail(id));
    }


    @Secured(role = RoleEnum.ADMIN)
    @PostMapping("/category")
    public ResponseEntity<Void> create(@RequestBody CreateCategoryRequest request) {
        LOGGER.info("[CATEGORY][CREATE] Request:{}", request);
        categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/category/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable("id") String id,
                                                   @RequestBody UpdateCategoryRequest request) {
        LOGGER.info("[CATEGORY][UPDATE] Request:{}", request);
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") String categoryId) {
        LOGGER.info("[CATEGORY][DELETE] ID:{}", categoryId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

