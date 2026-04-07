package com.example.pcstore.service.impl;

import com.example.pcstore.entity.Cart;
import com.example.pcstore.entity.Category;
import com.example.pcstore.entity.Product;
import com.example.pcstore.entity.UserProfile;
import com.example.pcstore.enums.OrderStatus;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.response.DashboardResponse;
import com.example.pcstore.model.response.LowStockProductItem;
import com.example.pcstore.model.response.MonthlyRevenueItem;
import com.example.pcstore.model.response.RecentOrderItem;
import com.example.pcstore.model.response.RecentUserItem;
import com.example.pcstore.model.response.TopSellingProductItem;
import com.example.pcstore.repositories.CartItemRepository;
import com.example.pcstore.repositories.CartRepository;
import com.example.pcstore.repositories.CategoryRepository;
import com.example.pcstore.repositories.ProductRepository;
import com.example.pcstore.repositories.UserProfileRepository;
import com.example.pcstore.service.DashboardService;
import com.example.pcstore.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private static final Logger LOGGER = LoggingFactory.getLogger(DashboardServiceImpl.class);

    private final UserProfileRepository userProfileRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public DashboardResponse getDashboard() {
        String adminUsername = JWTUtils.getUsername();
        LOGGER.info("[DASHBOARD][{}] Bắt đầu tổng hợp dữ liệu dashboard", adminUsername);

        DashboardResponse response = DashboardResponse.builder()
                .totalUsers(userProfileRepository.count())
                .totalProducts(productRepository.count())
                .totalCategories(categoryRepository.count())
                .totalOrders(cartRepository.count())
                .pendingOrders(cartRepository.countByStatus(OrderStatus.PENDING.getValue()))
                .totalRevenue(getTotalRevenue())
                .monthlyRevenue(buildMonthlyRevenue())
                .topSellingProducts(buildTopSellingProducts())
                .recentOrders(buildRecentOrders())
                .recentUsers(buildRecentUsers())
                .lowStockProducts(buildLowStockProducts())
                .build();

        LOGGER.info("[DASHBOARD][{}] Tổng hợp xong: users={}, products={}, orders={}, revenue={}",
                adminUsername, response.getTotalUsers(), response.getTotalProducts(),
                response.getTotalOrders(), response.getTotalRevenue());

        return response;
    }

    // ===== TOTAL REVENUE =====

    private BigDecimal getTotalRevenue() {
        BigDecimal revenue = cartItemRepository.sumRevenueFromCompletedOrders();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    // ===== MONTHLY REVENUE =====

    private List<MonthlyRevenueItem> buildMonthlyRevenue() {
        List<Object[]> rows = cartRepository.findMonthlyRevenue();
        List<MonthlyRevenueItem> result = new ArrayList<>();
        for (Object[] row : rows) {
            String month = (String) row[0];
            BigDecimal revenue = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;
            long orderCount = row[2] != null ? ((Number) row[2]).longValue() : 0L;
            result.add(MonthlyRevenueItem.builder()
                    .month(month)
                    .revenue(revenue)
                    .orderCount(orderCount)
                    .build());
        }
        return result;
    }

    // ===== TOP SELLING PRODUCTS =====

    private List<TopSellingProductItem> buildTopSellingProducts() {
        List<Object[]> rows = cartItemRepository.findTopSellingProducts();
        List<TopSellingProductItem> result = new ArrayList<>();
        for (Object[] row : rows) {
            String productId = (String) row[0];
            long totalSold = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            BigDecimal totalRevenue = row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO;

            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) continue;

            result.add(TopSellingProductItem.builder()
                    .productId(productId)
                    .productName(product.getName())
                    .imageUrl(product.getImageUrl())
                    .totalSold(totalSold)
                    .totalRevenue(totalRevenue)
                    .build());
        }
        return result;
    }

    // ===== RECENT ORDERS =====

    private List<RecentOrderItem> buildRecentOrders() {
        List<Cart> carts = cartRepository.findAllOrderByCreatedAtDesc()
                .stream().limit(5).toList();
        List<RecentOrderItem> result = new ArrayList<>();
        for (Cart cart : carts) {
            UserProfile userProfile = userProfileRepository.findByUsername(cart.getUsername()).orElse(null);
            String customerName = userProfile != null
                    ? userProfile.getFirstName() + " " + userProfile.getLastName()
                    : cart.getUsername();

            BigDecimal totalAmount = cartItemRepository.getCartItems(cart.getUsername(), cart.getId())
                    .stream()
                    .map(item -> item.getTotalAmount() != null ? item.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            OrderStatus orderStatus = resolveOrderStatus(cart.getStatus());

            result.add(RecentOrderItem.builder()
                    .orderId(cart.getId())
                    .customerName(customerName)
                    .username(cart.getUsername())
                    .totalAmount(totalAmount)
                    .status(cart.getStatus())
                    .statusLabel(orderStatus != null ? orderStatus.getLabel() : cart.getStatus())
                    .createdAt(cart.getCreatedAt())
                    .build());
        }
        return result;
    }

    // ===== RECENT USERS =====

    private List<RecentUserItem> buildRecentUsers() {
        return userProfileRepository.findUserProfilesOrderByCreatedAtDesc()
                .stream()
                .limit(5)
                .map(u -> RecentUserItem.builder()
                        .username(u.getUsername())
                        .fullName(u.getFirstName() + " " + u.getLastName())
                        .email(u.getEmail())
                        .createdAt(u.getCreatedAt())
                        .build())
                .toList();
    }

    // ===== LOW STOCK PRODUCTS =====

    private List<LowStockProductItem> buildLowStockProducts() {
        List<Product> products = productRepository.findLowStockProducts();
        List<LowStockProductItem> result = new ArrayList<>();
        for (Product product : products) {
            Category category = categoryRepository.findById(product.getCategoryId()).orElse(null);
            result.add(LowStockProductItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .imageUrl(product.getImageUrl())
                    .quantity(product.getQuantity() != null ? product.getQuantity() : 0)
                    .categoryName(category != null ? category.getName() : null)
                    .build());
        }
        return result;
    }

    // ===== HELPER =====

    private OrderStatus resolveOrderStatus(String statusValue) {
        for (OrderStatus s : OrderStatus.values()) {
            if (s.getValue().equals(statusValue)) return s;
        }
        return null;
    }
}
