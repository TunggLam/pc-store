package com.example.pcstore.controller;

import com.example.pcstore.aop.Secured;
import com.example.pcstore.enums.RoleEnum;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.request.CreateProductRequest;
import com.example.pcstore.model.response.ProductResponse;
import com.example.pcstore.model.response.ProductsResponse;
import com.example.pcstore.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Product", description = "Quản lý sản phẩm — xem danh sách, chi tiết và tạo mới (Admin)")
public class ProductController {

    private static final Logger LOGGER = LoggingFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @Operation(
            summary = "Lấy danh sách sản phẩm",
            description = "Hỗ trợ phân trang, lọc theo danh mục (`categoryId`) và tìm kiếm theo tên (`name`)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = ProductsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Tham số không hợp lệ", content = @Content)
    })
    @GetMapping("/products")
    public ResponseEntity<ProductsResponse> getProducts(
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0") @RequestParam("page") int page,
            @Parameter(description = "Số sản phẩm mỗi trang", example = "10") @RequestParam("size") int size,
            @Parameter(description = "ID danh mục để lọc") @RequestParam(value = "categoryId", required = false) String categoryId,
            @Parameter(description = "Từ khóa tìm kiếm theo tên sản phẩm") @RequestParam(value = "name", required = false) String name) {
        return ResponseEntity.ok(productService.getProducts(page, size, categoryId, name));
    }

    @Operation(
            summary = "Tìm kiếm sản phẩm",
            description = "Tìm kiếm gần đúng, không phân biệt hoa thường theo tên và mô tả sản phẩm. Hỗ trợ lọc theo danh mục và phân trang. Trả về danh sách rỗng nếu keyword trống."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = ProductsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Tham số không hợp lệ", content = @Content)
    })
    @GetMapping("/products/search")
    public ResponseEntity<ProductsResponse> searchProducts(
            @Parameter(description = "Từ khóa tìm kiếm (bắt buộc)", required = true) @RequestParam("keyword") String keyword,
            @Parameter(description = "ID danh mục để lọc") @RequestParam(value = "categoryId", required = false) String categoryId,
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Số sản phẩm mỗi trang", example = "10") @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.searchProducts(page, size, keyword, categoryId));
    }

    @Operation(
            summary = "Lấy chi tiết sản phẩm",
            description = "Trả về đầy đủ thông tin sản phẩm theo ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Không tìm thấy sản phẩm", content = @Content)
    })
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(description = "ID sản phẩm", required = true) @PathVariable("id") String id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @Operation(
            summary = "Tạo sản phẩm mới (Admin)",
            description = "Upload ảnh sản phẩm lên MinIO và lưu thông tin sản phẩm. Yêu cầu quyền ADMIN.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo thành công",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ", content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền ADMIN", content = @Content)
    })
    @Secured(role = RoleEnum.ADMIN)
    @PostMapping(value = "/product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(@Valid @ModelAttribute CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @Operation(
            summary = "Cập nhật số lượng sản phẩm (Admin)",
            description = "Cập nhật số lượng tồn kho của sản phẩm. Yêu cầu quyền ADMIN.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Không tìm thấy sản phẩm", content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền ADMIN", content = @Content)
    })
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
