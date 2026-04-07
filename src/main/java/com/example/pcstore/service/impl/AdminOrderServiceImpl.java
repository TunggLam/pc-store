package com.example.pcstore.service.impl;

import com.example.pcstore.constant.Constant;
import com.example.pcstore.entity.Cart;
import com.example.pcstore.entity.CartItem;
import com.example.pcstore.entity.PaymentVNPay;
import com.example.pcstore.entity.Product;
import com.example.pcstore.entity.UserProfile;
import com.example.pcstore.enums.OrderStatus;
import com.example.pcstore.exception.BusinessException;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.request.UpdateOrderStatusRequest;
import com.example.pcstore.model.response.AdminOrderItemResponse;
import com.example.pcstore.model.response.AdminOrderPaymentResponse;
import com.example.pcstore.model.response.AdminOrderResponse;
import com.example.pcstore.model.response.AdminOrdersResponse;
import com.example.pcstore.model.response.OrderStatisticsResponse;
import com.example.pcstore.repositories.CartItemRepository;
import com.example.pcstore.repositories.CartRepository;
import com.example.pcstore.repositories.PaymentHistoryRepository;
import com.example.pcstore.repositories.PaymentVNPayRepository;
import com.example.pcstore.repositories.ProductRepository;
import com.example.pcstore.repositories.UserProfileRepository;
import com.example.pcstore.service.AdminOrderService;
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
public class AdminOrderServiceImpl implements AdminOrderService {

    private static final Logger LOGGER = LoggingFactory.getLogger(AdminOrderServiceImpl.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserProfileRepository userProfileRepository;
    private final PaymentVNPayRepository paymentVNPayRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Override
    public AdminOrdersResponse getOrders(int page, int size, String username, String status) {
        String adminUsername = JWTUtils.getUsername();
        LOGGER.info("[ADMIN ORDER][{}][GET ORDERS] page={}, size={}, username={}, status={}", adminUsername, page, size, username, status);

        List<Cart> carts = fetchCarts(username, status);
        LOGGER.info("[ADMIN ORDER][{}][GET ORDERS] Tổng số đơn hàng tìm được: {}", adminUsername, carts.size());

        int total = carts.size();
        List<Cart> pagedCarts = carts.stream().skip((long) page * size).limit(size).toList();

        List<AdminOrderResponse> orders = pagedCarts.stream()
                .map(cart -> buildOrderResponse(cart, false))
                .toList();

        return AdminOrdersResponse.builder()
                .page(page)
                .size(size)
                .total(total)
                .orders(orders)
                .build();
    }

    @Override
    public AdminOrderResponse getOrderDetail(String orderId) {
        String adminUsername = JWTUtils.getUsername();
        LOGGER.info("[ADMIN ORDER][{}][GET ORDER DETAIL] orderId={}", adminUsername, orderId);

        Cart cart = cartRepository.findById(orderId).orElse(null);
        if (cart == null) {
            LOGGER.error("[ADMIN ORDER][{}][GET ORDER DETAIL] Không tìm thấy đơn hàng: {}", adminUsername, orderId);
            throw new BusinessException(Constant.ORDER_NOT_FOUND);
        }

        return buildOrderResponse(cart, true);
    }

    @Override
    public void updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        String adminUsername = JWTUtils.getUsername();
        LOGGER.info("[ADMIN ORDER][{}][UPDATE STATUS] orderId={}, newStatus={}", adminUsername, orderId, request.getStatus());

        if (!OrderStatus.isValid(request.getStatus())) {
            throw new BusinessException(Constant.ORDER_STATUS_INVALID);
        }

        Cart cart = cartRepository.findById(orderId).orElse(null);
        if (cart == null) {
            LOGGER.error("[ADMIN ORDER][{}][UPDATE STATUS] Không tìm thấy đơn hàng: {}", adminUsername, orderId);
            throw new BusinessException(Constant.ORDER_NOT_FOUND);
        }

        String currentStatus = cart.getStatus();
        if (OrderStatus.COMPLETED.getValue().equals(currentStatus) || OrderStatus.CANCELLED.getValue().equals(currentStatus)) {
            LOGGER.error("[ADMIN ORDER][{}][UPDATE STATUS] Đơn hàng {} đang ở trạng thái {} không thể cập nhật", adminUsername, orderId, currentStatus);
            throw new BusinessException(Constant.ORDER_STATUS_CANNOT_UPDATE);
        }

        cart.setStatus(request.getStatus().toUpperCase());
        cartRepository.save(cart);
        LOGGER.info("[ADMIN ORDER][{}][UPDATE STATUS] Cập nhật trạng thái đơn hàng {} thành {} thành công", adminUsername, orderId, request.getStatus());
    }

    @Override
    public OrderStatisticsResponse getOrderStatistics() {
        String adminUsername = JWTUtils.getUsername();
        LOGGER.info("[ADMIN ORDER][{}][STATISTICS] Lấy thống kê đơn hàng", adminUsername);

        List<Cart> allCarts = cartRepository.findAllOrderByCreatedAtDesc();

        long pendingCount = countByStatus(allCarts, OrderStatus.PENDING.getValue());
        long processingCount = countByStatus(allCarts, OrderStatus.PROCESSING.getValue());
        long shippedCount = countByStatus(allCarts, OrderStatus.SHIPPED.getValue());
        long completedCount = countByStatus(allCarts, OrderStatus.COMPLETED.getValue());
        long cancelledCount = countByStatus(allCarts, OrderStatus.CANCELLED.getValue());

        BigDecimal totalRevenue = calculateTotalRevenue(allCarts, OrderStatus.COMPLETED.getValue());

        LOGGER.info("[ADMIN ORDER][{}][STATISTICS] total={}, pending={}, processing={}, shipped={}, completed={}, cancelled={}, revenue={}",
                adminUsername, allCarts.size(), pendingCount, processingCount, shippedCount, completedCount, cancelledCount, totalRevenue);

        return OrderStatisticsResponse.builder()
                .totalOrders(allCarts.size())
                .pendingOrders(pendingCount)
                .processingOrders(processingCount)
                .shippedOrders(shippedCount)
                .completedOrders(completedCount)
                .cancelledOrders(cancelledCount)
                .totalRevenue(totalRevenue)
                .build();
    }

    private List<Cart> fetchCarts(String username, String status) {
        boolean hasUsername = StringUtils.isNotNullOrEmpty(username);
        boolean hasStatus = StringUtils.isNotNullOrEmpty(status);

        if (hasUsername && hasStatus) {
            return cartRepository.findAllByUsernameAndStatusOrderByCreatedAtDesc(username, status.toUpperCase());
        }
        if (hasUsername) {
            return cartRepository.findAllByUsernameOrderByCreatedAtDesc(username);
        }
        if (hasStatus) {
            return cartRepository.findAllByStatusOrderByCreatedAtDesc(status.toUpperCase());
        }
        return cartRepository.findAllOrderByCreatedAtDesc();
    }

    private AdminOrderResponse buildOrderResponse(Cart cart, boolean includePayment) {
        List<CartItem> cartItems = cartItemRepository.getCartItems(cart.getUsername(), cart.getId());
        List<AdminOrderItemResponse> itemResponses = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Optional<Product> productOpt = productRepository.findById(cartItem.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                itemResponses.add(AdminOrderItemResponse.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .imageUrl(product.getImageUrl())
                        .price(product.getPrice().intValue())
                        .quantity(cartItem.getQuantity())
                        .totalPrice(itemTotal)
                        .build());
                totalAmount = totalAmount.add(itemTotal);
            }
        }

        Optional<UserProfile> userProfileOpt = userProfileRepository.findByUsername(cart.getUsername());
        String customerName = null;
        String customerEmail = null;
        String customerPhone = null;
        String shippingAddress = null;
        if (userProfileOpt.isPresent()) {
            UserProfile userProfile = userProfileOpt.get();
            customerName = userProfile.getFirstName() + " " + userProfile.getLastName();
            customerEmail = userProfile.getEmail();
            customerPhone = userProfile.getPhoneNumber();
            shippingAddress = userProfile.getAddress();
        }

        AdminOrderPaymentResponse paymentResponse = null;
        if (includePayment) {
            paymentResponse = buildPaymentResponse(cart.getId());
        }

        OrderStatus orderStatus = resolveOrderStatus(cart.getStatus());

        return AdminOrderResponse.builder()
                .orderId(cart.getId())
                .username(cart.getUsername())
                .customerName(customerName)
                .customerEmail(customerEmail)
                .customerPhone(customerPhone)
                .shippingAddress(shippingAddress)
                .status(cart.getStatus())
                .statusLabel(orderStatus != null ? orderStatus.getLabel() : cart.getStatus())
                .items(itemResponses)
                .totalAmount(totalAmount)
                .payment(paymentResponse)
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    private AdminOrderPaymentResponse buildPaymentResponse(String orderId) {
        return paymentHistoryRepository.findByOrderHistoryId(orderId)
                .flatMap(paymentHistory -> {
                    PaymentVNPay payment = paymentVNPayRepository.findById(paymentHistory.getPaymentId()).orElse(null);
                    if (payment == null) return Optional.empty();
                    return Optional.of(AdminOrderPaymentResponse.builder()
                            .paymentId(payment.getId())
                            .invoiceNo(payment.getInvoiceNo())
                            .cardType(payment.getCardType())
                            .amount(payment.getAmount())
                            .transactionNo(payment.getTransactionNo())
                            .transactionStatus(payment.getTransactionStatus())
                            .transactionCreateDate(payment.getTransactionCreateDate())
                            .status(payment.getStatus())
                            .build());
                })
                .orElse(null);
    }

    private long countByStatus(List<Cart> carts, String status) {
        return carts.stream().filter(c -> status.equals(c.getStatus())).count();
    }

    private BigDecimal calculateTotalRevenue(List<Cart> carts, String status) {
        return carts.stream()
                .filter(cart -> status.equals(cart.getStatus()))
                .flatMap(cart -> cartItemRepository.getCartItems(cart.getUsername(), cart.getId()).stream())
                .map(CartItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
