package com.example.pcstore.service;

import com.example.pcstore.model.response.OrderHistoryResponse;

public interface OrderHistoryService {

    /**
     * Lấy lịch sử đơn hàng của user đang đăng nhập.
     *
     * @param page   số trang (bắt đầu từ 0)
     * @param size   số bản ghi mỗi trang
     * @param status lọc theo trạng thái (nullable — không lọc nếu null/blank)
     */
    OrderHistoryResponse getMyOrders(int page, int size, String status);
}
