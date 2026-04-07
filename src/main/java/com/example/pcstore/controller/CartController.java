package com.example.pcstore.controller;

import com.example.pcstore.aop.Secured;
import com.example.pcstore.enums.RoleEnum;
import com.example.pcstore.model.request.AddCartRequest;
import com.example.pcstore.model.request.UpdateCartRequest;
import com.example.pcstore.model.response.CartResponse;
import com.example.pcstore.service.CartService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Cart", description = "Quản lý giỏ hàng — thêm, sửa, xóa sản phẩm (yêu cầu đăng nhập)")
public class CartController {

    private final CartService cartService;

    @Operation(
            summary = "Lấy giỏ hàng hiện tại",
            description = "Trả về giỏ hàng đang ở trạng thái PENDING của người dùng đang đăng nhập, kèm danh sách sản phẩm và tổng tiền.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content)
    })
    @Secured(role = RoleEnum.USER)
    @GetMapping("/cart")
    public ResponseEntity<CartResponse> cart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @Operation(
            summary = "Lấy lịch sử đơn hàng",
            description = "Trả về thông tin đơn hàng theo cartId (dùng cho xem lại đơn hàng cũ).",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content)
    })
    @Secured(role = RoleEnum.USER)
    @GetMapping("/cart/history")
    public ResponseEntity<CartResponse> cartHistory(
            @Parameter(description = "ID của đơn hàng cần xem", required = true) @RequestParam("cartId") String cartId) {
        return ResponseEntity.ok(cartService.getCartHistory(cartId));
    }

    @Operation(
            summary = "Thêm sản phẩm vào giỏ hàng",
            description = "Thêm sản phẩm vào giỏ hàng PENDING. Nếu sản phẩm đã có trong giỏ thì cộng thêm số lượng.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thêm vào giỏ thành công"),
            @ApiResponse(responseCode = "400", description = "Không tìm thấy sản phẩm", content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content)
    })
    @Secured(role = RoleEnum.USER)
    @PostMapping("/cart")
    public ResponseEntity<Void> addCart(@RequestBody AddCartRequest request) {
        cartService.addCart(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(
            summary = "Cập nhật số lượng sản phẩm trong giỏ",
            description = "Tăng hoặc giảm số lượng sản phẩm trong giỏ hàng. Truyền `type = ADD` để tăng, `type = DECREASE` để giảm. Nếu số lượng về 0, sản phẩm bị xóa khỏi giỏ.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "400", description = "Không tìm thấy sản phẩm trong giỏ hoặc type không hợp lệ", content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content)
    })
    @Secured(role = RoleEnum.USER)
    @PutMapping("/cart")
    public ResponseEntity<Void> updateCart(@Valid @RequestBody UpdateCartRequest request) {
        cartService.updateCart(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Xóa sản phẩm khỏi giỏ hàng",
            description = "Xóa hoàn toàn một sản phẩm ra khỏi giỏ hàng.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content)
    })
    @Secured(role = RoleEnum.USER)
    @DeleteMapping("/cart/{cartId}/{productId}")
    public ResponseEntity<Void> deleteCart(
            @Parameter(description = "ID sản phẩm cần xóa", required = true) @PathVariable("productId") String productId,
            @Parameter(description = "ID giỏ hàng", required = true) @PathVariable("cartId") String cartId) {
        cartService.removeCart(cartId, productId);
        return ResponseEntity.ok().build();
    }

}
