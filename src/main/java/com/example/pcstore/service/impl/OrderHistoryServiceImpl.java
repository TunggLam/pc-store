package com.example.pcstore.service.impl;

import com.example.pcstore.entity.Cart;
import com.example.pcstore.entity.CartItem;
import com.example.pcstore.entity.Product;
import com.example.pcstore.enums.OrderStatus;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.response.OrderHistoryDetailResponse;
import com.example.pcstore.model.response.OrderHistoryItemResponse;
import com.example.pcstore.model.response.OrderHistoryResponse;
import com.example.pcstore.repositories.CartItemRepository;
import com.example.pcstore.repositories.CartRepository;
import com.example.pcstore.repositories.ProductRepository;
import com.example.pcstore.service.OrderHistoryService;
import com.example.pcstore.utils.JWTUtils;
import com.example.pcstore.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderHistoryServiceImpl implements OrderHistoryService {

    private static final Logger LOGGER = LoggingFactory.getLogger(OrderHistoryServiceImpl.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    public OrderHistoryResponse getMyOrders(int page, int size, String status) {
        String username = JWTUtils.getUsername();
        LOGGER.info("[ORDER HISTORY][{}] page={}, size={}, status={}", username, page, size, status);

        List<Cart> carts = fetchCarts(username, status);
        int total = carts.size();
        List<Cart> pagedCarts = carts.stream().skip((long) page * size).limit(size).toList();

        List<OrderHistoryDetailResponse> orders = pagedCarts.stream()
                .map(cart -> buildDetailResponse(cart, username))
                .toList();

        LOGGER.info("[ORDER HISTORY][{}] Trả về {}/{} đơn hàng", username, orders.size(), total);

        return OrderHistoryResponse.builder()
                .page(page)
                .size(size)
                .total(total)
                .orders(orders)
                .build();
    }

    private List<Cart> fetchCarts(String username, String status) {
        if (StringUtils.isNotNullOrEmpty(status)) {
            return cartRepository.findAllByUsernameAndStatusOrderByCreatedAtDesc(username, status.toUpperCase());
        }
        return cartRepository.findAllByUsernameOrderByCreatedAtDesc(username);
    }

    private OrderHistoryDetailResponse buildDetailResponse(Cart cart, String username) {
        List<CartItem> cartItems = cartItemRepository.getCartItems(username, cart.getId());
        List<OrderHistoryItemResponse> itemResponses = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Optional<Product> productOpt = productRepository.findById(cartItem.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                itemResponses.add(OrderHistoryItemResponse.builder()
                        .productName(product.getName())
                        .imageUrl(product.getImageUrl())
                        .price(product.getPrice().intValue())
                        .quantity(cartItem.getQuantity())
                        .totalPrice(itemTotal)
                        .build());
                totalAmount = totalAmount.add(itemTotal);
            }
        }

        OrderStatus orderStatus = resolveOrderStatus(cart.getStatus());

        return OrderHistoryDetailResponse.builder()
                .orderId(cart.getId())
                .status(cart.getStatus())
                .statusLabel(orderStatus != null ? orderStatus.getLabel() : cart.getStatus())
                .totalAmount(totalAmount)
                .itemCount(itemResponses.size())
                .items(itemResponses)
                .createdAt(cart.getCreatedAt())
                .build();
    }

    private OrderStatus resolveOrderStatus(String statusValue) {
        for (OrderStatus s : OrderStatus.values()) {
            if (s.getValue().equals(statusValue)) {
                return s;
            }
        }
        return null;
    }
}
