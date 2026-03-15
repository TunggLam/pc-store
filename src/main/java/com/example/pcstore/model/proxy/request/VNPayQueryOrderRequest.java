package com.example.pcstore.model.proxy.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VNPayQueryOrderRequest {

    /**
     * Mã hệ thống merchant tự sinh ứng với mỗi yêu cầu truy vấn giao dịch
     * Mã này là duy nhất dùng để phân biệt các yêu cầu truy vấn giao dịch. Không được trùng lặp trong ngày
     */
    @JsonProperty("vnp_RequestId")
    private String requestId;

    /**
     * Phiên bản api mà merchant kết nối
     * Phiên bản hiện tại là 2.1.0
     */
    @JsonProperty("vnp_Version")
    private String version;

    /**
     * Mã API sử dụng, mã cho giao dịch thanh toán là "querydr"
     */
    @JsonProperty("vnp_Command")
    private String command;

    /**
     * Mã định danh kết nối thanh toán
     * Các mã định danh kết nối tương ứng giữa các hệ thống
     * Thanh toán PAY được quy định tại tham số "vnp_TmnCode"
     */
    @JsonProperty("vnp_TmnCode")
    private String tmnCode;

    /**
     * Giống mã gửi sang VNPAY khi gửi yêu cầu thanh toán
     * Các mã giao dịch thanh toán tương ứng giữa các hệ thống
     * Thanh toán PAY được quy định tại tham số "vnp_TxnRef"
     */
    @JsonProperty("vnp_TxnRef")
    private String txnRef;

    /**
     * Mô tả thông tin yêu cầu ( Request description)
     */
    @JsonProperty("vnp_OrderInfo")
    private String orderInfo;

    /**
     * Mã giao dịch ghi nhận tại hệ thống VNPAY
     * Các mã giao dịch VNPAY phản hồi tương ứng giữa các hệ thống
     * Thanh toán PAY được quy định tại tham số "vnp_TransactionNo"
     */
    @JsonProperty("vnp_TransactionNo")
    private String transactionNo;

    /**
     * Thời gian ghi nhận giao dịch tại hệ thống của merchant tính theo GMT+7
     * Định dạng: yyyyMMddHHmmss, tham khảo giá trị: Thanh toán PAY giống vnp_CreateDate của vnp_Command=pay
     */
    @JsonProperty("vnp_TransactionDate")
    private String transactionDate;

    /**
     * Thời gian phát sinh request (thời gian phát sinh yêu cầu truy vấn giao dịch) GMT+7
     * Định dạng: yyyyMMddHHmmss
     */
    @JsonProperty("vnp_CreateDate")
    private String createDate;

    /**
     * Địa chỉ IP của máy chủ thực hiện gọi API
     */
    @JsonProperty("vnp_IpAddr")
    private String ipAddress;

    /**
     * Mã kiểm tra (checksum) để đảm bảo dữ liệu không bị thay đổi trong quá trình gửi yêu cầu tử hệ thống merchant sang VNPAY.
     * Quy tắc tạo checksum: data = vnp_RequestId + “|” + vnp_Version + “|” + vnp_Command + “|” + vnp_TmnCode + “|” + vnp_TxnRef + “|” + vnp_TransactionDate + “|” + vnp_CreateDate + “|” + vnp_IpAddr + “|” + vnp_OrderInfo;
     * checksum = hashWithSecureType(secretKey, data);
     */
    @JsonProperty("vnp_SecureHash")
    private String secureHash;

}
