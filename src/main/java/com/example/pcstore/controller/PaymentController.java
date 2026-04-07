package com.example.pcstore.controller;

import com.example.pcstore.aop.Secured;
import com.example.pcstore.enums.RoleEnum;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.request.VNPayCallbackRequest;
import com.example.pcstore.model.response.CallbackResponse;
import com.example.pcstore.model.response.VNPayIPNResponse;
import com.example.pcstore.model.response.VNPayInitOrderResponse;
import com.example.pcstore.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Tag(name = "Payment", description = "Thanh toán qua cổng VNPay")
public class PaymentController {

    private static final Logger LOGGER = LoggingFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    @Operation(
            summary = "VNPay IPN — xác nhận thanh toán",
            description = """
                    Endpoint nhận thông báo thanh toán tức thời (Instant Payment Notification) từ VNPay server.
                    **Không gọi trực tiếp từ client.** VNPay sẽ tự động gọi endpoint này sau khi giao dịch hoàn tất.

                    Hệ thống sẽ xác minh chữ ký và cập nhật trạng thái đơn hàng.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xử lý IPN thành công",
                    content = @Content(schema = @Schema(implementation = VNPayIPNResponse.class)))
    })
    @GetMapping(value = "/ipn")
    public ResponseEntity<VNPayIPNResponse> ipn(
            @Parameter(description = "Các tham số VNPay gửi kèm (vnp_TxnRef, vnp_Amount, vnp_SecureHash...)")
            @RequestParam Map<String, String> parameters,
            HttpServletRequest request) {
        LOGGER.info("[PAYMENT][VNPAY][IPN] Parameters: {}", parameters);
        return ResponseEntity.ok(paymentService.processIPN(parameters, request));
    }

    @Operation(
            summary = "Khởi tạo đơn thanh toán VNPay",
            description = "Tạo đơn hàng và sinh URL thanh toán VNPay. Client redirect người dùng đến `target` URL trong response để thực hiện thanh toán.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Khởi tạo thành công, trả về URL thanh toán",
                    content = @Content(schema = @Schema(implementation = VNPayInitOrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Giỏ hàng trống hoặc không hợp lệ", content = @Content),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content)
    })
    @Secured(role = RoleEnum.USER)
    @PostMapping("/vnpay/init")
    public ResponseEntity<VNPayInitOrderResponse> initOrder() {
        return ResponseEntity.ok(paymentService.initOrder());
    }

    @Operation(
            summary = "Xử lý callback sau thanh toán VNPay",
            description = """
                    Nhận kết quả thanh toán từ frontend sau khi người dùng hoàn tất trên trang VNPay.
                    Truyền vào `invoiceNo` (mã đơn hàng `txnRef`) để hệ thống truy vấn và cập nhật trạng thái.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xử lý thành công",
                    content = @Content(schema = @Schema(implementation = CallbackResponse.class))),
            @ApiResponse(responseCode = "400", description = "Mã đơn hàng không tồn tại", content = @Content)
    })
    @PostMapping("/vnpay/callback")
    public ResponseEntity<CallbackResponse> callback(@RequestBody VNPayCallbackRequest request) {
        return ResponseEntity.ok(paymentService.callback(request));
    }

}
