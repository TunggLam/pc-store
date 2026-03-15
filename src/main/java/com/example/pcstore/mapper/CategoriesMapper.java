package com.example.pcstore.mapper;

import com.example.pcstore.entity.Category;
import com.example.pcstore.model.response.CategoryResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CategoriesMapper {
    CategoryResponse mapToCategoryResponse(Category category);
}
