package com.example.pcstore.service;

import com.example.pcstore.model.request.AddCartRequest;
import com.example.pcstore.model.request.UpdateCartRequest;
import com.example.pcstore.model.response.CartResponse;

public interface CartService {

    CartResponse getCart();

    CartResponse getCartHistory(String cartId);

    void addCart(AddCartRequest request);

    void removeCart(String cartId, String id);

    void updateCart(UpdateCartRequest request);
}

