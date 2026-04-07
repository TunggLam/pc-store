package com.example.pcstore.service;

import com.example.pcstore.model.request.CreateProductRequest;
import com.example.pcstore.model.response.ProductResponse;
import com.example.pcstore.model.response.ProductsResponse;

public interface ProductService {

    ProductsResponse getProducts(int page, int size, String categoryId, String name);

    ProductsResponse searchProducts(int page, int size, String keyword, String categoryId);

    ProductResponse getProduct(String id);

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(String id, CreateProductRequest request);
}


