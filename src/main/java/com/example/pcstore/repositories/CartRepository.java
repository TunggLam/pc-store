package com.example.pcstore.repositories;

import com.example.pcstore.entity.Cart;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
