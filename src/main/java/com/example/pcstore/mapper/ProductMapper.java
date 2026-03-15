package com.example.pcstore.mapper;

import com.example.pcstore.entity.Product;
import com.example.pcstore.model.response.ProductResponse;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse mapToProductResponse(Product product);
}
