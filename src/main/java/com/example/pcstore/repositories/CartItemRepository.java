package com.example.pcstore.repositories;

import com.example.pcstore.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

    @Query(value = "select * from cart_item where username = :username and product_id = :productId and cart_id = :cartId order by created_at desc limit 1", nativeQuery = true)
    CartItem getCartItem(@Param("productId") String productId, @Param("username") String username, @Param("cartId") String cartId);

    @Query(value = "select * from cart_item where username = :username and cart_id = :cartId order by updated_at desc", nativeQuery = true)
    List<CartItem> getCartItems(@Param("username") String username, @Param("cartId") String cartId);

    Optional<CartItem> findByUsernameAndProductIdAndCartId(String username, String productId, String cartId);
}

