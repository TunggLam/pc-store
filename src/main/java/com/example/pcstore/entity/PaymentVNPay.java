package com.example.pcstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_vnpay")
public class PaymentVNPay extends BaseEntity {

    /**
     * Username khách hàng
     */
    @Column(name = "username")
    private String username;

    /**
     * 1 - VNPAYQR: Thanh toán bằng qr code
     * 2 - VNPAYEWALLET: Thanh toán qua ví VN Pay
     * 3 - VNBANK: Thanh toán qua thẻ ngân hàng
     */
    @Column(name = "card_type")
    private String cardType;

    /**
     * Created Date của đơn hàng
     * Lưu ý: Cột này khác vơới cột create_at
     * Cột này để lấy ra khi query đơn hàng
     */
    @Column(name = "transaction_create_date")
    private String transactionCreateDate;

    @Column(name = "transaction_expired_date")
    private String transactionExpiredDate;

    /**
     * Mã đơn hàng
     */
    @Column(name = "invoice_no")
    private String invoiceNo;

    /**
     * mô tả
     */
    @Column(name = "description")
    private String description;

    /**
     * số tiền cần thanh toán
     */
    @Column(name = "amount")
    private BigInteger amount;

    /**
     * mã kết quả thanh toán vnpay
     */
    @Column(name = "callback_result_code")
    private String callbackResultCode;

    /**
     * vị trí
     */
    @Column(name = "location", columnDefinition = "TEXT")
    private String location;

    /**
     * Kinh độ
     */
    @Column(name = "longitude")
    private String longitude;

    /**
     * Vỹ độ
     */
    @Column(name = "latitude")
    private String latitude;

    /**
     * Địa chỉ Ip của khách hàng
     * Khi gửi sang VNPay thì bắt buộc phải truyền địa chỉ ip
     */
    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "transaction_no")
    private String transactionNo;

    @Column(name = "transaction_status")
    private String transactionStatus;

    @Column(name = "bank_transaction_no")
    private String bankTranNo;

    @Column(name = "status")
    private String status;
}

