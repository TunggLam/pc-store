package com.example.pcstore.scheduler;

import com.example.pcstore.entity.Cart;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderCleanupScheduler {

    private static final Logger LOGGER = LoggingFactory.getLogger(OrderCleanupScheduler.class);

    private final CartRepository cartRepository;

    /**
     * Chạy mỗi 5 phút, tự động hủy các đơn hàng PENDING quá 15 phút chưa được thanh toán.
     * Áp dụng cho trường hợp user đóng trình duyệt hoặc không quay về callback sau khi rời VNPay.
     * Lưu ý: kho hàng không bị trừ ở bước init nên không cần hoàn kho.
     */
    @Scheduled(fixedRate = 300000)
    public void cancelExpiredOrders() {
        List<Cart> expiredCarts = cartRepository.findExpiredPendingOrders();
        if (expiredCarts.isEmpty()) {
            return;
        }

        LOGGER.info("[SCHEDULER][CLEANUP] Tìm thấy {} đơn hàng PENDING quá hạn, tiến hành hủy", expiredCarts.size());

        for (Cart cart : expiredCarts) {
            cartRepository.updateStatusById(cart.getId(), "CANCELLED");
            LOGGER.info("[SCHEDULER][CLEANUP] Auto-cancelled expired order: {}", cart.getId());
        }
    }
}
