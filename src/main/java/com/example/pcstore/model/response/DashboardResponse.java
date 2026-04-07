package com.example.pcstore.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dữ liệu tổng hợp trang chủ admin")
public class DashboardResponse {

    // ===== THỐNG KÊ TỔNG QUAN =====

    @Schema(description = "Tổng số người dùng đã đăng ký", example = "150")
    private long totalUsers;

    @Schema(description = "Tổng số sản phẩm trong hệ thống", example = "45")
    private long totalProducts;

    @Schema(description = "Tổng số danh mục", example = "4")
    private long totalCategories;

    @Schema(description = "Tổng số đơn hàng (mọi trạng thái)", example = "80")
    private long totalOrders;

    @Schema(description = "Tổng doanh thu từ các đơn COMPLETED (VNĐ)", example = "125000000")
    private BigDecimal totalRevenue;

    @Schema(description = "Số đơn hàng đang chờ xác nhận (PENDING)", example = "5")
    private long pendingOrders;

    // ===== DOANH THU THEO THÁNG =====

    @Schema(description = "Doanh thu 12 tháng gần nhất, sắp xếp từ cũ đến mới")
    private List<MonthlyRevenueItem> monthlyRevenue;

    // ===== TOP SẢN PHẨM BÁN CHẠY =====

    @Schema(description = "Top 5 sản phẩm bán chạy nhất (từ đơn COMPLETED)")
    private List<TopSellingProductItem> topSellingProducts;

    // ===== ĐƠN HÀNG GẦN NHẤT =====

    @Schema(description = "5 đơn hàng được tạo gần nhất")
    private List<RecentOrderItem> recentOrders;

    // ===== USER MỚI ĐĂNG KÝ =====

    @Schema(description = "5 người dùng đăng ký gần nhất")
    private List<RecentUserItem> recentUsers;

    // ===== SẢN PHẨM SẮP HẾT HÀNG =====

    @Schema(description = "Sản phẩm có số lượng tồn kho <= 5, sắp xếp theo quantity tăng dần")
    private List<LowStockProductItem> lowStockProducts;
}
