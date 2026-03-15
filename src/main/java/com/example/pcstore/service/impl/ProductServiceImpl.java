package com.example.pcstore.service.impl;

import com.example.pcstore.constant.Constant;
import com.example.pcstore.entity.Category;
import com.example.pcstore.entity.ImgurUpload;
import com.example.pcstore.entity.Product;
import com.example.pcstore.exception.BusinessException;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.mapper.CategoriesMapper;
import com.example.pcstore.mapper.ProductMapper;
import com.example.pcstore.model.request.CreateProductRequest;
import com.example.pcstore.model.response.CategoryResponse;
import com.example.pcstore.model.response.ProductResponse;
import com.example.pcstore.model.response.ProductsResponse;
import com.example.pcstore.repositories.CategoryRepository;
import com.example.pcstore.repositories.ImgurUploadRepository;
import com.example.pcstore.repositories.ProductRepository;
import com.example.pcstore.repositories.spectification.ProductSpecification;
import com.example.pcstore.service.ProductService;
import com.example.pcstore.utils.JWTUtils;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggingFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ImgurUploadRepository imgurUploadRepository;
    private final CategoryRepository categoryRepository;
    private final ProductSpecification productSpecification;
    private final ProductMapper productMapper;
    private final CategoriesMapper categoriesMapper;
    private final MinioClient minioClient;

    @Value("${minio.url}")
    private String minioUrl;

    @Override
    public ProductsResponse getProducts(int page, int size, String categoryId, String name) {
        List<Product> products = productSpecification.findAllByCategoryIdAndName(categoryId, name);
        List<ProductResponse> productResponses = new ArrayList<>();
        for (Product product : products.stream().skip((long) page * size).limit(size).toList()) {
            ProductResponse productResponse = productMapper.mapToProductResponse(product);
            Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
            CategoryResponse categoryResponse = categoriesMapper.mapToCategoryResponse(category);
            productResponse.setCategory(categoryResponse);
            productResponses.add(productResponse);
        }
        return ProductsResponse.builder().totalElements(products.size()).products(productResponses).build();
    }

    @Override
    public ProductResponse getProduct(String id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new BusinessException("Không tìm thấy sản phẩm");
        }
        Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
        if (category == null) {
            throw new BusinessException("Không tìm thấy loại sản phẩm");
        }
        ProductResponse productResponse = productMapper.mapToProductResponse(product);
        CategoryResponse categoryResponse = categoriesMapper.mapToCategoryResponse(category);
        productResponse.setCategory(categoryResponse);
        return productResponse;
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        String username = JWTUtils.getUsername();

        /* Lấy thông tin category xem có tồn tại không */
        boolean isCategoryExist = categoryRepository.existsById(request.getCategoryId());
        LOGGER.info("[PRODUCT][CREATE PRODUCT][{}] Kiểm tra tồn tại loại sản phẩm: {}", username, isCategoryExist);

        if (!isCategoryExist) {
            throw new BusinessException("Không tìm thấy thông tin loại sản phẩm");
        }

        /* Uplaod ảnh lên Minio */
        String imageUrl = uploadImgToMinio(request);

        ImgurUpload imgurUpload = new ImgurUpload();
        imgurUpload.setStatus("UPLOADED");
        imgurUpload.setSize(request.getImage().getSize());
        imgurUpload.setImgurUrl(imageUrl);
        imgurUploadRepository.save(imgurUpload);

        Product product = saveProduct(request, imageUrl);

        return mapToProductResponse(product);
    }

    private String uploadImgToMinio(CreateProductRequest request) {
        try (InputStream inputStream = request.getImage().getInputStream()) {
            String objectName = String.format("%s/%s.png", request.getCategoryId(), request.getName());
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("product")
                            .object(objectName)
                            .stream(inputStream, request.getImage().getSize(), -1)
                            .contentType(request.getImage().getContentType())
                            .build()
            );

           return String.format("%s/product/%s", minioUrl, objectName);
        } catch (Exception e) {
            LOGGER.error("[MINIO] Upload avatar thất bại: {}", e.getMessage());
            throw new BusinessException("Upload ảnh thất bại");
        }
    }

    @Override
    public ProductResponse updateProduct(String id, CreateProductRequest request) {
        String username = JWTUtils.getUsername();

        Product product = getProductById(id);
        LOGGER.info("[PRODUCT][UPDATE PRODUCT][{}] Thông tin sản phẩm: {}", username, product);

        uploadImageIfFileExist(request, username, product);
        saveUpdateProduct(request, product);
        return productMapper.mapToProductResponse(product);
    }

    private Product getProductById(String id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new BusinessException(Constant.PRODUCT_NOT_FOUND);
        }
        return product;
    }

    private void uploadImageIfFileExist(CreateProductRequest request, String username, Product product) {
        if (!request.getImage().isEmpty()) {
            /* Uplaod ảnh lên Minio */
            String imageUrl = uploadImgToMinio(request);

            ImgurUpload imgurUpload = new ImgurUpload();
            imgurUpload.setStatus("UPLOADED");
            imgurUpload.setSize(request.getImage().getSize());
            imgurUpload.setImgurUrl(imageUrl);
            imgurUploadRepository.save(imgurUpload);

            product.setImageUrl(imageUrl);
        }
    }

    private void saveUpdateProduct(CreateProductRequest request, Product product) {
        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setDescription(request.getDescription());
        productRepository.save(product);
    }

    private static ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setId(product.getId());
        response.setImageUrl(product.getImageUrl());
        return response;
    }

    private Product saveProduct(CreateProductRequest request, String imageUrl) {
        Product product = new Product();
        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setImageUrl(imageUrl);
        product.setQuantity(request.getQuantity());
        product.setDescription(request.getDescription());
        productRepository.save(product);

        return product;
    }

}

