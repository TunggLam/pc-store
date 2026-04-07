package com.example.pcstore.service;

import com.example.pcstore.model.request.UpdateOrderStatusRequest;
import com.example.pcstore.model.response.AdminOrderResponse;
import com.example.pcstore.model.response.AdminOrdersResponse;
import com.example.pcstore.model.response.OrderStatisticsResponse;

public interface AdminOrderService {

    /**
     * Lấy danh sách tất cả đơn hàng (có filter + phân trang)
     *
     * @param page     số trang (bắt đầu từ 0)
     * @param size     số bản ghi mỗi trang
     * @param username filter theo username khách hàng
     * @param status   filter theo trạng thái đơn hàng
     */
    AdminOrdersResponse getOrders(int page, int size, String username, String status);

    /**
     * Lấy chi tiết một đơn hàng theo orderId (cartId)
     *
     * @param orderId ID của đơn hàng (cart.id)
     */
    AdminOrderResponse getOrderDetail(String orderId);

    /**
     * Cập nhật trạng thái đơn hàng
     *
     * @param orderId ID của đơn hàng
     * @param request request chứa status mới
     */
    void updateOrderStatus(String orderId, UpdateOrderStatusRequest request);

    /**
     * Lấy thống kê đơn hàng (tổng số, theo trạng thái, doanh thu)
     */
    OrderStatisticsResponse getOrderStatistics();
}
