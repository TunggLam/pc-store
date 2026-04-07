package com.example.pcstore.controller;

import com.example.pcstore.aop.Secured;
import com.example.pcstore.enums.RoleEnum;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.request.CreateCategoryRequest;
import com.example.pcstore.model.request.UpdateCategoryRequest;
import com.example.pcstore.model.response.CategoriesResponse;
import com.example.pcstore.model.response.CategoryResponse;
import com.example.pcstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Category", description = "Quản lý danh mục sản phẩm")
public class CategoryController {

    private static final Logger LOGGER = LoggingFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @Operation(
            summary = "Lấy danh sách danh mục",
            description = "Trả về tất cả danh mục đang hoạt động. Truyền `productCount=true` để kèm số lượng sản phẩm trong mỗi danh mục."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = CategoriesResponse.class)))
    })
    @GetMapping("/categories")
    public ResponseEntity<CategoriesResponse> findAll(
            @Parameter(description = "Truyền true để kèm số lượng sản phẩm trong mỗi danh mục", example = "false")
            @RequestParam(value = "productCount", required = false) boolean productCount) {
        return ResponseEntity.ok(categoryService.findAll(productCount));
    }

    @Operation(
            summary = "Lấy chi tiết danh mục (Admin)",
            description = "Trả về thông tin chi tiết của một danh mục. Yêu cầu quyền ADMIN.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Không tìm thấy danh mục", content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền ADMIN", content = @Content)
    })
    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/category/{id}")
    public ResponseEntity<CategoryResponse> categoryDetail(
            @Parameter(description = "ID danh mục", required = true) @PathVariable("id") String id) {
        return ResponseEntity.ok(categoryService.detail(id));
    }

    @Operation(
            summary = "Tạo danh mục mới (Admin)",
            description = "Tạo một danh mục sản phẩm mới. Tên danh mục phải duy nhất. Yêu cầu quyền ADMIN.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Tên danh mục đã tồn tại hoặc không hợp lệ", content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền ADMIN", content = @Content)
    })
    @Secured(role = RoleEnum.ADMIN)
    @PostMapping("/category")
    public ResponseEntity<Void> create(@RequestBody CreateCategoryRequest request) {
        LOGGER.info("[CATEGORY][CREATE] Request:{}", request);
        categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Cập nhật danh mục",
            description = "Cập nhật tên hoặc trạng thái hoạt động của danh mục."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Không tìm thấy danh mục", content = @Content)
    })
    @PutMapping("/category/{id}")
    public ResponseEntity<CategoryResponse> update(
            @Parameter(description = "ID danh mục", required = true) @PathVariable("id") String id,
            @RequestBody UpdateCategoryRequest request) {
        LOGGER.info("[CATEGORY][UPDATE] Request:{}", request);
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @Operation(
            summary = "Xóa danh mục",
            description = "Xóa danh mục theo ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Xóa thành công"),
            @ApiResponse(responseCode = "400", description = "Không tìm thấy danh mục", content = @Content)
    })
    @DeleteMapping("/category/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID danh mục", required = true) @PathVariable(name = "id") String categoryId) {
        LOGGER.info("[CATEGORY][DELETE] ID:{}", categoryId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
