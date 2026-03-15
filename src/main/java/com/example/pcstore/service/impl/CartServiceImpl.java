package com.example.pcstore.service.impl;

import com.example.pcstore.constant.Constant;
import com.example.pcstore.entity.Cart;
import com.example.pcstore.entity.CartItem;
import com.example.pcstore.entity.Product;
import com.example.pcstore.exception.BusinessException;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.model.request.AddCartRequest;
import com.example.pcstore.model.request.UpdateCartRequest;
import com.example.pcstore.model.response.CartItemResponse;
import com.example.pcstore.model.response.CartResponse;
import com.example.pcstore.repositories.CartItemRepository;
import com.example.pcstore.repositories.CartRepository;
import com.example.pcstore.repositories.ProductRepository;
import com.example.pcstore.repositories.UserProfileRepository;
import com.example.pcstore.repositories.spectification.CartSpecification;
import com.example.pcstore.service.CartService;
import com.example.pcstore.utils.JWTUtils;
import com.example.pcstore.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final Logger LOGGER = LoggingFactory.getLogger(CartServiceImpl.class);

    private static final List<String> TYPES = List.of("ADD", "DECREASE");
    private static final String ADD = "ADD";

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserProfileRepository userProfileRepository;
    private final CartItemRepository cartItemRepository;
    private final CartSpecification cartSpecification;

    @Override
    public CartResponse getCart() {
        String username = JWTUtils.getUsername();

        Cart cart = cartRepository.getCartPending(username);
        LOGGER.info("[CART][GET CARTS][{}] Cart: {}", username, cart);

        String address = userProfileRepository.getAddressByUsername(username);
        LOGGER.info("[CART][GET CARTS][{}] Địa chỉ của đơn hàng: {}", username, address);

        if (cart == null) {
            return new CartResponse(address);
        }

        List<CartItemResponse> cartItemsResponse = new ArrayList<>();

        List<CartItem> cartItems = cartItemRepository.getCartItems(username, cart.getId());
        LOGGER.info("[CART][GET CARTS][{}] List Cart Item: {}", username, cartItems);

        for (CartItem cartItem : cartItems) {
            productRepository.findById(cartItem.getProductId()).ifPresent(product -> mapAndAddToCartItems(cartItem, product, cartItemsResponse));
        }

        List<BigDecimal> prices = cartItemsResponse.stream().map(CartItemResponse::getTotalPrice).toList();

        BigDecimal totalPrice = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        LOGGER.info("[CART][GET CARTS][{}] Tổng số tiền của đơn hàng: {}", username, totalPrice);

        return new CartResponse(cart.getId(), cartItemsResponse, totalPrice, address);
    }

    @Override
    public CartResponse getCartHistory(String cartId) {
        String username = JWTUtils.getUsername();

        List<Cart> carts = cartSpecification.findAllById(cartId);
        LOGGER.info("[CART][GET CART HISTORY][{}] Danh sách đơn hàng: {}", username, carts);


        return null;
    }

    @Override
    public void addCart(AddCartRequest request) {
        String username = JWTUtils.getUsername();

        /* Tìm xem người dùng đã có giỏ hàng chưa */
        Product product = getProduct(request.getProductId());
        LOGGER.info("[CART][ADD][{}] Thông tin sản phẩm: {}", username, product);

        BigDecimal amount = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        LOGGER.info("[CART][ADD][{}] Số tiền khi thêm vào giỏ: {}", username, amount);

        Cart cart = cartRepository.findByUsernameAndStatusOrderByCreatedAtDesc(username, "PENDING").orElse(null);
        LOGGER.info("[CART][ADD][{}] Thông tin giỏ hàng: {}", username, cart);

        /* Nếu chưa có giỏ hàng thì tạo mới 1 giỏ hàng */
        if (cart == null) {
            cart = saveNewCart(username);
            LOGGER.info("[CART][ADD][{}] Tạo mới giỏ hàng: {}", username, cart);

            /* Vì chưa có giỏ hàng nên sẽ tạo mới luôn cart item */
            saveNewCartItem(request, cart, username, amount);
            LOGGER.info("[CART][ADD][{}] Thêm mới sản phẩm trong giỏ hàng thành công", username);
        } else {
            /* Nếu đã có giỏ hàng thì tìm cart item theo carId và productId */
            CartItem cartItem = cartItemRepository.getCartItem(request.getProductId(), username, cart.getId());
            LOGGER.info("[CART][ADD][{}] Thông tin sản phẩm trong giỏ: {}", username, cartItem);

            if (cartItem == null) {
                saveNewCartItem(request, cart, username, amount);
                LOGGER.info("[CART][ADD][{}] Thêm mới sản phẩm trong giỏ hàng thành công", username);
            } else {
                updateQuantityCartItem(request, cartItem, amount);
                LOGGER.info("[CART][ADD][{}] Cập nhật sản phẩm trong giỏ hàng thành công", username);
            }
            cartRepository.save(cart);
            LOGGER.info("[CART][ADD][{}] Cập nhật tổng số tiền của giỏ hàng thành công", username);
        }

    }

    @Override
    public void removeCart(String cartId, String productId) {
        String username = JWTUtils.getUsername();
        Optional<CartItem> cartItem = cartItemRepository.findByUsernameAndProductIdAndCartId(username, productId, cartId);
        cartItem.ifPresent(cartItemRepository::delete);
    }

    @Override
    public void updateCart(UpdateCartRequest request) {
        String username = JWTUtils.getUsername();

        if (!TYPES.contains(request.getType())) {
            throw new BusinessException("Loại cập nhật sản phẩm không hợp lệ");
        }

        Cart cart = cartRepository.getCartPending(username);
        if (cart == null) {
            throw new BusinessException("Không tìm thấy thông tin giỏ hàng");
        }

        CartItem cartItem = cartItemRepository.getCartItem(request.getProductId(), username, cart.getId());
        if (cartItem == null) {
            throw new BusinessException("Không tìm thấy thông tin sản phẩm trong giỏ hàng");
        }

        Product product = getProduct(request.getProductId());

        if (StringUtils.equals(request.getType(), ADD)) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItem.setTotalAmount(BigDecimal.valueOf(cartItem.getQuantity()).multiply(product.getPrice()));
            cartItemRepository.save(cartItem);
        } else {
            if (cartItem.getQuantity() > 0) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                cartItem.setTotalAmount(BigDecimal.valueOf(cartItem.getQuantity()).multiply(product.getPrice()));
                cartItemRepository.save(cartItem);
            }
            if (cartItem.getQuantity() == 0) {
                cartItemRepository.delete(cartItem);
            }
        }
    }

    private Product getProduct(String productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new BusinessException(Constant.PRODUCT_NOT_FOUND);
        }
        return product;
    }

    private Cart saveNewCart(String username) {
        Cart cart = new Cart();
        cart.setStatus("PENDING");
        cart.setUsername(username);
        cartRepository.save(cart);
        return cart;
    }

    private void updateQuantityCartItem(AddCartRequest request, CartItem cartItem, BigDecimal amount) {
        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        cartItem.setTotalAmount(cartItem.getTotalAmount().add(amount));
        cartItemRepository.save(cartItem);
    }

    private void saveNewCartItem(AddCartRequest request, Cart cart, String username, BigDecimal amount) {
        CartItem cartItem = new CartItem();
        cartItem.setCartId(cart.getId());
        cartItem.setProductId(request.getProductId());
        cartItem.setQuantity(request.getQuantity());
        cartItem.setUsername(username);
        cartItem.setTotalAmount(amount);
        cartItemRepository.save(cartItem);
    }

    private static void mapAndAddToCartItems(CartItem cartItem, Product product, List<CartItemResponse> cartItemsResponse) {
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setProductId(product.getId());
        cartItemResponse.setProductName(product.getName());
        cartItemResponse.setPrice(product.getPrice().intValue());
        cartItemResponse.setQuantity(cartItem.getQuantity());
        cartItemResponse.setImageUrl(product.getImageUrl());
        cartItemResponse.setTotalPrice(new BigDecimal(cartItem.getQuantity()).multiply(product.getPrice()));
        cartItemsResponse.add(cartItemResponse);
    }

}


