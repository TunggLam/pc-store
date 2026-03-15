package com.example.pcstore.service;

import com.example.pcstore.model.request.CreateCategoryRequest;
import com.example.pcstore.model.request.UpdateCategoryRequest;
import com.example.pcstore.model.response.CategoriesResponse;
import com.example.pcstore.model.response.CategoryResponse;

public interface CategoryService {
    CategoriesResponse findAll(boolean productCount);

    void create(CreateCategoryRequest request);

    CategoryResponse update(String id, UpdateCategoryRequest request);

    CategoryResponse detail(String id);
}
