package com.example.pcstore.controller;

import com.example.pcstore.aop.Secured;
import com.example.pcstore.enums.RoleEnum;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.request.UpdateProfileRequest;
import com.example.pcstore.model.response.OrderHistoryResponse;
import com.example.pcstore.model.response.UserProfileResponse;
import com.example.pcstore.service.OrderHistoryService;
import com.example.pcstore.service.UserProfileService;
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
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User", description = "Thông tin tài khoản người dùng đang đăng nhập")
public class UserController {

    private static final Logger LOGGER = LoggingFactory.getLogger(UserController.class);

    private final UserProfileService userProfileService;
    private final OrderHistoryService orderHistoryService;

    @Operation(
            summary = "Lấy thông tin cá nhân",
            description = "Trả về thông tin hồ sơ của người dùng đang đăng nhập dựa trên JWT token.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực hoặc token hết hạn", content = @Content),
            @ApiResponse(responseCode = "400", description = "Không tìm thấy thông tin người dùng", content = @Content)
    })
    @Secured(role = RoleEnum.USER)
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        return ResponseEntity.ok(userProfileService.myProfile());
    }

    @Operation(
            summary = "Cập nhật hồ sơ cá nhân",
            description = "Cập nhật thông tin hồ sơ của người dùng đang đăng nhập. Trường nào null sẽ giữ nguyên giá trị cũ. Không cho phép thay đổi username.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc email/số điện thoại đã tồn tại", content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực hoặc token hết hạn", content = @Content)
    })
    @Secured(role = RoleEnum.USER)
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateProfile(request));
    }

    @Operation(
            summary = "Lấy lịch sử đơn hàng",
            description = "Trả về danh sách đơn hàng của người dùng đang đăng nhập, sắp xếp theo thời gian mới nhất trước. Hỗ trợ phân trang và lọc theo trạng thái.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = OrderHistoryResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực hoặc token hết hạn", content = @Content)
    })
    @Secured(role = RoleEnum.USER)
    @GetMapping("/orders")
    public ResponseEntity<OrderHistoryResponse> getMyOrders(
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số đơn hàng mỗi trang", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Lọc theo trạng thái: PENDING | PROCESSING | SHIPPED | COMPLETED | CANCELLED") @RequestParam(required = false) String status) {
        return ResponseEntity.ok(orderHistoryService.getMyOrders(page, size, status));
    }
}
