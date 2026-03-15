package com.example.pcstore.controller;

import com.example.pcstore.aop.Secured;
import com.example.pcstore.enums.RoleEnum;
import com.example.pcstore.model.request.AddCartRequest;
import com.example.pcstore.model.request.UpdateCartRequest;
import com.example.pcstore.model.response.CartResponse;
import com.example.pcstore.service.CartService;
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
@Tag(name = "Cart Controller", description = "Danh sách API phục vụ giỏ hàng của người dùng")
public class CartController {

    private final CartService cartService;

    @Secured(role = RoleEnum.USER)
    @GetMapping("/cart")
    public ResponseEntity<CartResponse> cart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @Secured(role = RoleEnum.USER)
    @GetMapping("/cart/history")
    public ResponseEntity<CartResponse> cartHistory(@RequestParam("cartId") String cartId) {
        return ResponseEntity.ok(cartService.getCartHistory(cartId));
    }

    @Secured(role = RoleEnum.USER)
    @PostMapping("/cart")
    public ResponseEntity<Void> addCart(@RequestBody AddCartRequest request) {
        cartService.addCart(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Secured(role = RoleEnum.USER)
    @PutMapping("/cart")
    public ResponseEntity<Void> updateCart(@Valid @RequestBody UpdateCartRequest request) {
        cartService.updateCart(request);
        return ResponseEntity.ok().build();
    }

    @Secured(role = RoleEnum.USER)
    @DeleteMapping("/cart/{cartId}/{productId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("productId") String productId,
                                           @PathVariable("cartId") String cartId) {
        cartService.removeCart(cartId, productId);
        return ResponseEntity.ok().build();
    }

}

