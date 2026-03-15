package com.example.pcstore.controller;

import com.example.pcstore.aop.Secured;
import com.example.pcstore.enums.RoleEnum;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.request.VNPayCallbackRequest;
import com.example.pcstore.model.response.CallbackResponse;
import com.example.pcstore.model.response.VNPayIPNResponse;
import com.example.pcstore.model.response.VNPayInitOrderResponse;
import com.example.pcstore.service.PaymentService;
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
@Tag(name = "Payment Controller", description = "Danh sách API phục vụ thanh toán của người dùng")
public class PaymentController {

    private static final Logger LOGGER = LoggingFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    @GetMapping(value = "/ipn")
    public ResponseEntity<VNPayIPNResponse> ipn(@RequestParam Map<String, String> parameters, HttpServletRequest request) {
        LOGGER.info("[PAYMENT][VNPAY][IPN] Parameters: {}", parameters);
        return ResponseEntity.ok(paymentService.processIPN(parameters, request));
    }

    @Secured(role = RoleEnum.USER)
    @PostMapping("/vnpay/init")
    public ResponseEntity<VNPayInitOrderResponse> initOrder() {
        return ResponseEntity.ok(paymentService.initOrder());
    }

    //    @Secured(role = RoleEnum.USER)
    @PostMapping("/vnpay/callback")
    public ResponseEntity<CallbackResponse> callback(@RequestBody VNPayCallbackRequest request) {
        return ResponseEntity.ok(paymentService.callback(request));
    }

}

