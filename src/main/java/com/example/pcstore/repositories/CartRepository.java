package com.example.pcstore.repositories;

import com.example.pcstore.entity.Cart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CartRepository extends JpaRepository<Cart, String>, JpaSpecificationExecutor<Cart> {

    @Query(value = "select * from cart where username = :username and status = 'PENDING' order by created_at desc limit 1 ", nativeQuery = true)
    Cart getCartPending(@Param("username") String username);

    Optional<Cart> findByUsernameAndStatusOrderByCreatedAtDesc(String username, String status);

    @Modifying
    @Transactional
    @Query(value = "update cart set status = :status where username = :username and status = 'PENDING' ", nativeQuery = true)
    void updateStatus(@Param("username") String username, @Param("status") String status);

    @Modifying
    @Transactional
    @Query(value = "UPDATE cart SET status = :status WHERE id = :cartId AND status = 'PENDING'", nativeQuery = true)
    void updateStatusById(@Param("cartId") String cartId, @Param("status") String status);

    @Query(value = "SELECT * FROM cart WHERE status = 'PENDING' AND created_at < NOW() - INTERVAL '15 minutes'", nativeQuery = true)
    List<Cart> findExpiredPendingOrders();

    @Query(value = "SELECT * FROM cart ORDER BY created_at DESC", nativeQuery = true)
    List<Cart> findAllOrderByCreatedAtDesc();

    @Query(value = "SELECT * FROM cart WHERE status = :status ORDER BY created_at DESC", nativeQuery = true)
    List<Cart> findAllByStatusOrderByCreatedAtDesc(@Param("status") String status);

    @Query(value = "SELECT * FROM cart WHERE username = :username ORDER BY created_at DESC", nativeQuery = true)
    List<Cart> findAllByUsernameOrderByCreatedAtDesc(@Param("username") String username);

    @Query(value = "SELECT * FROM cart WHERE username = :username AND status = :status ORDER BY created_at DESC", nativeQuery = true)
    List<Cart> findAllByUsernameAndStatusOrderByCreatedAtDesc(@Param("username") String username, @Param("status") String status);

    long countByStatus(String status);

    @Query(value = """
            SELECT TO_CHAR(c.created_at, 'YYYY-MM') AS month,
                   COALESCE(SUM(ci.total_amount), 0) AS revenue,
                   COUNT(DISTINCT c.id) AS order_count
            FROM cart c
            JOIN cart_item ci ON ci.cart_id = c.id
            WHERE c.status = 'COMPLETED'
              AND c.created_at >= NOW() - INTERVAL '12 months'
            GROUP BY TO_CHAR(c.created_at, 'YYYY-MM')
            ORDER BY month ASC
            """, nativeQuery = true)
    List<Object[]> findMonthlyRevenue();
}
