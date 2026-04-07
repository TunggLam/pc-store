package com.example.pcstore.repositories;

import com.example.pcstore.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

    @Query(value = "select * from cart_item where username = :username and product_id = :productId and cart_id = :cartId order by created_at desc limit 1", nativeQuery = true)
    CartItem getCartItem(@Param("productId") String productId, @Param("username") String username, @Param("cartId") String cartId);

    @Query(value = "select * from cart_item where username = :username and cart_id = :cartId order by updated_at desc", nativeQuery = true)
    List<CartItem> getCartItems(@Param("username") String username, @Param("cartId") String cartId);

    Optional<CartItem> findByUsernameAndProductIdAndCartId(String username, String productId, String cartId);

    @Query(value = """
            SELECT ci.product_id,
                   SUM(ci.quantity)     AS total_sold,
                   SUM(ci.total_amount) AS total_revenue
            FROM cart_item ci
            JOIN cart c ON c.id = ci.cart_id
            WHERE c.status = 'COMPLETED'
            GROUP BY ci.product_id
            ORDER BY total_sold DESC
            LIMIT 5
            """, nativeQuery = true)
    List<Object[]> findTopSellingProducts();

    @Query(value = """
            SELECT COALESCE(SUM(ci.total_amount), 0)
            FROM cart_item ci
            JOIN cart c ON c.id = ci.cart_id
            WHERE c.status = 'COMPLETED'
            """, nativeQuery = true)
    BigDecimal sumRevenueFromCompletedOrders();
}

