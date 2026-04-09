package com.example.pcstore.service.impl;

import com.example.pcstore.constant.Constant;
import com.example.pcstore.entity.Category;
import com.example.pcstore.exception.BusinessException;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.mapper.CategoriesMapper;
import com.example.pcstore.model.request.CreateCategoryRequest;
import com.example.pcstore.model.request.UpdateCategoryRequest;
import com.example.pcstore.model.response.CategoriesResponse;
import com.example.pcstore.model.response.CategoryResponse;
import com.example.pcstore.repositories.CategoryRepository;
import com.example.pcstore.repositories.ProductRepository;
import com.example.pcstore.service.CategoryService;
import com.example.pcstore.utils.JWTUtils;
import com.example.pcstore.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final Logger LOGGER = LoggingFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final CategoriesMapper categoriesMapper;
    private final ProductRepository productRepository;

    @Override
    public CategoriesResponse findAll(boolean productCount) {
        List<Category> categories = categoryRepository.getAllCategoryActive();
        List<CategoryResponse> categoriesResponses = categories.stream().map(categoriesMapper::mapToCategoryResponse).toList();
        if (productCount) {
            for (CategoryResponse categoryResponse : categoriesResponses) {
                categoryResponse.setProductCount(productRepository.getCountByCategoryId(categoryResponse.getId()));
            }
        }
        return CategoriesResponse.builder().categories(categoriesResponses).build();
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void create(CreateCategoryRequest request) {
        String username = JWTUtils.getUsername();
        if (StringUtils.isNullOrEmpty(request.getName())) {
            throw new BusinessException("Tên loại sản phẩm không được để trống!");
        }
        Boolean isExist = categoryRepository.existsByName(request.getName());
        if (Boolean.TRUE.equals(isExist)) {
            throw new BusinessException("Loại sản phẩm này đã tồn tại trong hệ thống!");
        }
        Category category = new Category(request.getName(), true);
        categoryRepository.save(category);
        LOGGER.info("[CATEGORY][{}][CREATE] Đã tạo mới loại sản phẩm: {}", username, request.getName());
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse update(String id, UpdateCategoryRequest request) {
        String username = JWTUtils.getUsername();
        LOGGER.info("[CATEGORY][UPDATE][{}] Starting... Request: {}", username, request);

        validateRequest(id, request.getName());

        Category category = categoryRepository.findById(id).orElse(null);
        LOGGER.info("[CATEGORY][UPDATE][{}] Category Old: {}", username, category);

        if (category == null) {
            throw new BusinessException("Không tìm thấy thông tin loại sản phẩm");
        }

        updateCategory(request, category);
        LOGGER.info("[CATEGORY][UPDATE][{}] Category New: {}", username, category);

        return categoriesMapper.mapToCategoryResponse(category);
    }

    @Override
    public CategoryResponse detail(String id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            throw new BusinessException("Không tìm thấy thông tin loại sản phẩm");
        }
        return categoriesMapper.mapToCategoryResponse(category);
    }

    private void updateCategory(UpdateCategoryRequest request, Category category) {
        category.setActive(request.isActive());
        category.setName(request.getName());
        categoryRepository.save(category);
    }

    private static void validateRequest(String id, String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            throw new BusinessException("Tên loại sản phẩm không được để trống.");
        }
        if (StringUtils.isNullOrEmpty(id)) {
            throw new BusinessException("ID loại sản phẩm không được để trống.");
        }
    }

}
