package com.example.pcstore.repositories;

import com.example.pcstore.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, String> {

    @Query(value = "SELECT * FROM payment_history WHERE order_history_id = :orderId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<PaymentHistory> findByOrderHistoryId(@Param("orderId") String orderId);

    @Query(value = "SELECT * FROM payment_history WHERE payment_id = :paymentId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<PaymentHistory> findByPaymentId(@Param("paymentId") String paymentId);
}
