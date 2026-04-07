package com.example.pcstore.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin danh mục sản phẩm")
public class CategoryResponse {

    @Schema(description = "ID danh mục (UUID)")
    private String id;

    @Schema(description = "Tên danh mục", example = "CPU / Bộ xử lý")
    private String name;

    @Schema(description = "Danh mục đang hoạt động hay đã ẩn", example = "true")
    private boolean isActive;

    @Schema(description = "Thời điểm tạo danh mục")
    private String createdAt;

    @Schema(description = "Số lượng sản phẩm trong danh mục (chỉ có khi gọi với productCount=true)", example = "25")
    private int productCount;

}


