package com.example.pcstore.repositories;

import com.example.pcstore.entity.PaymentVNPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentVNPayRepository extends JpaRepository<PaymentVNPay, String> {

    @Query(value = "select * from payment_vnpay where invoice_no = :invoiceNo order by created_at desc limit 1", nativeQuery = true)
    PaymentVNPay getByInvoiceNo(@Param("invoiceNo") String invoiceNo);

}
