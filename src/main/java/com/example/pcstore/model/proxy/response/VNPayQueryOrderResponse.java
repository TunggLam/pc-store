package com.example.pcstore.model.proxy.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VNPayQueryOrderResponse {

    /**
     * Mã hệ thống VNPAY tự sinh ứng với mỗi yêu cầu truy vấn giao dịch
     * Mã này là duy nhất dùng để phân biệt các yêu cầu truy vấn giao dịch.
     * Không trùng lặp trong ngày.
     */
    @JsonProperty("vnp_ResponseId")
    private String responseId;

    /**
     * Mã API sử dụng, mã cho giao dịch thanh toán là "querydr"
     */
    @JsonProperty("vnp_Command")
    private String command;

    /**
     * Mã định danh kết nối của hệ thống
     * Các mã định danh kết nối tương ứng giữa các hệ thống
     * Thanh toán PAYđược quy định tại tham số "vnp_TmnCode"
     */
    @JsonProperty("vnp_TmnCode")
    private String tmnCode;

    /**
     * Mã tham chiếu của giao dịch tại hệ thống của merchant
     * Mã này do merchant gửi sang khi yêu cầu thanh toán. VNPAY gửi lại để merchant cập nhật
     * Tham khảo các giá trị qua các hệ thống thanh toán
     * Thanh toán PAYđược quy định tại tham số "vnp_TxnRef"
     */
    @JsonProperty("vnp_TxnRef")
    private String txnRef;

    /**
     * Số tiền merchant gửi yêu cầu sang VNPAY thanh toán cho giao dịch.
     * Tham khảo các giá trị qua các hệ thống thanh toán:
     * Thanh toán PAYđược quy định tại tham số "vnp_Amount"
     */
    @JsonProperty("vnp_Amount")
    private Long amount;

    /**
     * Mô tả thông tin yêu cầu (Request description)
     */
    @JsonProperty("vnp_OrderInfo")
    private String orderInfo;

    /**
     * Mã phản hồi kết quả xử lý của API
     * Quy định mã trả lời 00 ứng với yêu cầu querydr được thực hiện thành công
     * Tham khảo thêm tại bảng mã lỗi.
     * Chú ý: Đây là kết quả phản hồi của hệ thống. Kết quả của giao dịch (thành công/ không thành công)
     */
    @JsonProperty("vnp_ResponseCode")
    private String responseCode;

    /**
     * Mô tả thông tin tương ứng với vnp_ResponseCode
     */
    @JsonProperty("vnp_Message")
    private String message;

    /**
     * Mã Ngân hàng hoặc mã Ví điện tử thanh toán.
     */
    @JsonProperty("vnp_BankCode")
    private String bankCode;

    /**
     * Thời gian khách hàng thanh toán, ghi nhận tại VNPAY. Định dạng: yyyyMMddHHmmss
     */
    @JsonProperty("vnp_PayDate")
    private String payDate;

    /**
     * Mã giao dịch ghi nhận tại hệ thống VNPAY
     */
    @JsonProperty("vnp_TransactionNo")
    private String transactionNo;

    /**
     * Loại giao dịch tại hệ thống VNPAY:
     * 01: GD thanh toán
     * 02: Giao dịch hoàn trả toàn phần
     * 03: Giao dịch hoàn trả một phần
     */
    @JsonProperty("vnp_TransactionType")
    private String transactionType;

    /**
     * Tình trạng thanh toán của giao dịch tại Cổng thanh toán VNPAY
     */
    @JsonProperty("vnp_TransactionStatus")
    private String transactionStatus;

    /**
     * Mã khuyến mại
     * Trong trường hợp khách hàng áp dụng mã QR khuyễn mãi khi thanh toán.
     */
    @JsonProperty("vnp_PromotionCode")
    private String promotionCode;

    /**
     * Số tiền khuyến mại
     * Trong trường hợp khách hàng áp dụng mã QR khuyễn mãi khi thanh toán.
     */
    @JsonProperty("vnp_PromotionAmount")
    private String promotionAmount;

    /**
     * Mã kiểm tra (checksum) để đảm bảo dữ liệu không bị thay đổi trong quá trình VNPAY trả kết quả về merchant.
     * Quy tắc tạo checksum: data = vnp_ResponseId + “|” + vnp_Command + “|” + vnp_ResponseCode + “|” + vnp_Message + “|” + vnp_TmnCode + “|” + vnp_TxnRef + “|” + vnp_Amount + “|” vnp_BankCode + “|” + vnp_PayDate + “|” + vnp_TransactionNo + “|” + vnp_TransactionType + “|” + vnp_TransactionStatus + “|” + vnp_OrderInfo + “|” + vnp_PromotionCode + “|” + vnp_PromotionAmount;
     * checksum = hashWithSecureType(secretKey, data);
     */
    @JsonProperty("vnp_SecureHash")
    private String secureHash;

}
