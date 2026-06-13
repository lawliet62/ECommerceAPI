package org.example.ecommerceapi.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.example.ecommerceapi.common.exception.BusinessException;
import org.example.ecommerceapi.common.exception.ErrorCode;
import org.example.ecommerceapi.domain.cart.dto.CartItemResponse;
import org.example.ecommerceapi.domain.cart.dto.CartResponse;
import org.example.ecommerceapi.domain.cart.entity.Cart;
import org.example.ecommerceapi.domain.cart.entity.CartItem;
import org.example.ecommerceapi.domain.cart.repository.CartItemRepository;
import org.example.ecommerceapi.domain.cart.repository.CartRepository;
import org.example.ecommerceapi.domain.product.entity.Product;
import org.example.ecommerceapi.domain.product.repository.ProductRepository;
import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.example.ecommerceapi.domain.user.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final AppUserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public CartItemResponse addItem(Long userId, Long productId, int quantity) {
        AppUser user = getUser(userId);
        Cart cart = getOrCreateCart(user);
        Product product = getActiveProduct(productId);

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .map(item -> {
                    validateStock(product, item.getQuantity() + quantity);
                    return item.increaseQuantity(quantity);
                })
                .orElseGet(() -> {
                    validateStock(product, quantity);
                    return cartItemRepository.save(CartItem.create(cart, product, quantity));
                });
        return CartItemResponse.from(cartItem);
    }

    public CartResponse getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cart -> {
                    List<CartItemResponse> items = cartItemRepository.findAllByCart(cart).stream()
                            .map(CartItemResponse::from)
                            .toList();

                    return CartResponse.of(cart.getId(), items);
                })
                .orElseGet(CartResponse::empty);
    }

    @Transactional
    public CartItemResponse updateItem(Long userId, Long cartItemId, int quantity) {
        AppUser user = getUser(userId);
        Cart cart = getCartOrThrow(user);
        CartItem cartItem = getCartItem(cartItemId, cart);

        validateStock(cartItem.getProduct(), quantity);
        cartItem.updateQuantity(quantity);

        return CartItemResponse.from(cartItem);
    }

    @Transactional
    public void removeItem(Long userId, Long cartItemId) {
        AppUser user = getUser(userId);
        Cart cart = getCartOrThrow(user);
        CartItem cartItem = getCartItem(cartItemId, cart);

        cartItemRepository.delete(cartItem);
    }

    private Cart getCartOrThrow(AppUser user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));
    }

    private CartItem getCartItem(Long cartItemId, Cart cart) {
        return cartItemRepository.findByIdAndCart(cartItemId, cart)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));
    }

    private AppUser getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Cart getOrCreateCart(AppUser user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.create(user)));
    }

    private Product getActiveProduct(Long productId) {
        return productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private void validateStock(Product product, int quantity) {
        if (product.getStock() < quantity) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
        }
    }

}