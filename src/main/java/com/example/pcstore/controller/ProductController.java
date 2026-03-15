package com.example.pcstore.controller;

import com.example.pcstore.aop.Secured;
import com.example.pcstore.enums.RoleEnum;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.request.CreateProductRequest;
import com.example.pcstore.model.response.ProductResponse;
import com.example.pcstore.model.response.ProductsResponse;
import com.example.pcstore.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Product Controller", description = "Danh sách API phục vụ sản phẩm của người dùng")
public class ProductController {

    private static final Logger LOGGER = LoggingFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<ProductsResponse> getProducts(@RequestParam("page") int page,
                                                        @RequestParam("size") int size,
                                                        @RequestParam(value = "categoryId", required = false) String categoryId,
                                                        @RequestParam(value = "name", required = false) String name) {
        return ResponseEntity.ok(productService.getProducts(page, size, categoryId, name));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") String id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @Secured(role = RoleEnum.ADMIN)
    @PostMapping(value = "/product")
    public ResponseEntity<ProductResponse> createProduct(@Valid @ModelAttribute CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @Secured(role = RoleEnum.ADMIN)
    @PutMapping(value = "/product/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String id,
                                                         @Valid @ModelAttribute CreateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @Secured(role = RoleEnum.ADMIN)
    @PutMapping("/product/quantity")
    public ResponseEntity<ProductResponse> updateQuantityProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

}

