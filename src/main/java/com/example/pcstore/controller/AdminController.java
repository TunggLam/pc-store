package com.example.pcstore.controller;

import com.example.pcstore.aop.Secured;
import com.example.pcstore.enums.RoleEnum;
import com.example.pcstore.model.request.UpdateOrderStatusRequest;
import com.example.pcstore.model.response.AdminOrderResponse;
import com.example.pcstore.model.response.AdminOrdersResponse;
import com.example.pcstore.model.response.CategoriesResponse;
import com.example.pcstore.model.response.DashboardResponse;
import com.example.pcstore.model.response.OrderStatisticsResponse;
import com.example.pcstore.model.response.UserProfileResponse;
import com.example.pcstore.model.response.UserProfilesResponse;
import com.example.pcstore.service.AdminOrderService;
import com.example.pcstore.service.AdminService;
import com.example.pcstore.service.DashboardService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Quản trị hệ thống — người dùng, danh mục và đơn hàng (yêu cầu quyền ADMIN)")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final AdminService adminService;
    private final AdminOrderService adminOrderService;
    private final DashboardService dashboardService;

    // ==================== DASHBOARD ====================

    @Operation(
            summary = "Dữ liệu tổng hợp trang chủ admin",
            description = """
                    Trả về tất cả dữ liệu cần thiết cho dashboard admin trong một lần gọi:
                    - Thống kê tổng quan (users, products, categories, orders, revenue, pending)
                    - Doanh thu 12 tháng gần nhất (biểu đồ)
                    - Top 5 sản phẩm bán chạy nhất
                    - 5 đơn hàng được tạo gần nhất
                    - 5 user đăng ký gần nhất
                    - Sản phẩm sắp hết hàng (quantity ≤ 5)
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = DashboardResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền ADMIN", content = @Content)
    })
    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    // ==================== USER MANAGEMENT ====================

    @Operation(
            summary = "Lấy danh sách người dùng",
            description = "Hỗ trợ tìm kiếm theo tên (`name`), tên đăng nhập (`username`), email. Có phân trang."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = UserProfilesResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền ADMIN", content = @Content)
    })
    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/users")
    public ResponseEntity<UserProfilesResponse> getUsers(
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0") @RequestParam(required = false) int page,
            @Parameter(description = "Số bản ghi mỗi trang", example = "10") @RequestParam(required = false) int size,
            @Parameter(description = "Tìm kiếm theo họ tên") @RequestParam(required = false) String name,
            @Parameter(description = "Tìm kiếm theo tên đăng nhập") @RequestParam(required = false) String username,
            @Parameter(description = "Tìm kiếm theo email") @RequestParam(required = false) String email) {
        return ResponseEntity.ok(adminService.getUserProfiles(page, size, name, username, email));
    }

    @Operation(
            summary = "Lấy chi tiết người dùng theo Keycloak ID",
            description = "Trả về hồ sơ người dùng tương ứng với Keycloak ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Không tìm thấy người dùng", content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền ADMIN", content = @Content)
    })
    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/user/{keycloakId}")
    public ResponseEntity<UserProfileResponse> getUserByKeycloakId(
            @Parameter(description = "Keycloak User ID", required = true) @PathVariable(name = "keycloakId") String keycloakId) {
        return ResponseEntity.ok(adminService.getUserByKeycloakId(keycloakId));
    }

    // ==================== CATEGORY MANAGEMENT ====================

    @Operation(
            summary = "Lấy danh sách danh mục (Admin)",
            description = "Lấy danh mục có hỗ trợ filter theo tên, trạng thái và sắp xếp. Ví dụ `sort=name:asc` hoặc `sort=active:desc`."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = CategoriesResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền ADMIN", content = @Content)
    })
    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/categories")
    public ResponseEntity<CategoriesResponse> getCategories(
            @Parameter(description = "Số trang", example = "0") @RequestParam int page,
            @Parameter(description = "Số bản ghi mỗi trang", example = "10") @RequestParam int size,
            @Parameter(description = "Tìm kiếm theo tên danh mục") @RequestParam(required = false) String name,
            @Parameter(description = "Lọc theo trạng thái: true = đang hoạt động, false = đã ẩn") @RequestParam(required = false) Boolean status,
            @Parameter(description = "Sắp xếp, ví dụ: `name:asc`, `active:desc`") @RequestParam(name = "sort", required = false) List<String> orderBy) {
        return ResponseEntity.ok(adminService.getCategories(page, size, name, status, orderBy));
    }

    // ==================== ORDER MANAGEMENT ====================

    @Operation(
            summary = "Lấy danh sách đơn hàng",
            description = """
                    Lấy tất cả đơn hàng trong hệ thống với phân trang và filter.

                    **Các trạng thái đơn hàng:**
                    - `PENDING` — Chờ xác nhận
                    - `PROCESSING` — Đang xử lý
                    - `SHIPPED` — Đang giao hàng
                    - `COMPLETED` — Hoàn thành
                    - `CANCELLED` — Đã hủy
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = AdminOrdersResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền ADMIN", content = @Content)
    })
    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/orders")
    public ResponseEntity<AdminOrdersResponse> getOrders(
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số bản ghi mỗi trang", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Lọc theo username khách hàng") @RequestParam(required = false) String username,
            @Parameter(description = "Lọc theo trạng thái: PENDING | PROCESSING | SHIPPED | COMPLETED | CANCELLED") @RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminOrderService.getOrders(page, size, username, status));
    }

    @Operation(
            summary = "Lấy chi tiết đơn hàng",
            description = "Trả về đầy đủ thông tin đơn hàng gồm: danh sách sản phẩm, thông tin khách hàng và lịch sử thanh toán VNPay."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = AdminOrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Không tìm thấy đơn hàng", content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền ADMIN", content = @Content)
    })
    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<AdminOrderResponse> getOrderDetail(
            @Parameter(description = "ID đơn hàng (cart ID)", required = true) @PathVariable String orderId) {
        return ResponseEntity.ok(adminOrderService.getOrderDetail(orderId));
    }

    @Operation(
            summary = "Cập nhật trạng thái đơn hàng",
            description = """
                    Cập nhật trạng thái xử lý đơn hàng. Luồng thông thường:
                    `PENDING` → `PROCESSING` → `SHIPPED` → `COMPLETED`

                    Lưu ý: Không thể cập nhật đơn hàng đã ở trạng thái `COMPLETED` hoặc `CANCELLED`.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "400", description = "Không tìm thấy đơn hàng, trạng thái không hợp lệ, hoặc đơn đã kết thúc", content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền ADMIN", content = @Content)
    })
    @Secured(role = RoleEnum.ADMIN)
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @Parameter(description = "ID đơn hàng", required = true) @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        adminOrderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Thống kê đơn hàng",
            description = "Trả về tổng số đơn hàng phân theo từng trạng thái và tổng doanh thu từ các đơn đã hoàn thành (COMPLETED)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = OrderStatisticsResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content),
            @ApiResponse(responseCode = "403", description = "Không có quyền ADMIN", content = @Content)
    })
    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/orders/statistics")
    public ResponseEntity<OrderStatisticsResponse> getOrderStatistics() {
        return ResponseEntity.ok(adminOrderService.getOrderStatistics());
    }

}
