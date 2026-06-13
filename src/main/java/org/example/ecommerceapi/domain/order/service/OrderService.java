package org.example.ecommerceapi.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.example.ecommerceapi.common.exception.BusinessException;
import org.example.ecommerceapi.common.exception.ErrorCode;
import org.example.ecommerceapi.domain.cart.entity.Cart;
import org.example.ecommerceapi.domain.cart.entity.CartItem;
import org.example.ecommerceapi.domain.cart.repository.CartItemRepository;
import org.example.ecommerceapi.domain.cart.repository.CartRepository;
import org.example.ecommerceapi.domain.order.dto.OrderItemResponse;
import org.example.ecommerceapi.domain.order.dto.OrderResponse;
import org.example.ecommerceapi.domain.order.dto.OrderSummaryResponse;
import org.example.ecommerceapi.domain.order.entity.Order;
import org.example.ecommerceapi.domain.order.entity.OrderItem;
import org.example.ecommerceapi.domain.order.repository.OrderItemRepository;
import org.example.ecommerceapi.domain.order.repository.OrderRepository;
import org.example.ecommerceapi.domain.product.entity.Product;
import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.example.ecommerceapi.domain.user.repository.AppUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AppUserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public OrderResponse createOrder(Long userId) {
        AppUser user = getUser(userId);
        Cart cart = getCart(user);
        List<CartItem> cartItems = getCartItems(cart);

        validateOrderableItems(cartItems);

        BigDecimal totalAmount = calculateTotalAmount(cartItems);

        Order order = Order.create(user, totalAmount);
        Order savedOrder = orderRepository.save(order);

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> OrderItem.create(
                        savedOrder,
                        cartItem.getProduct().getName(),
                        cartItem.getProduct().getPrice(),
                        cartItem.getQuantity()
                ))
                .toList();

        List<OrderItemResponse> items = orderItemRepository.saveAll(orderItems).stream()
                .map(OrderItemResponse::from)
                .toList();

        cartItemRepository.deleteAll(cartItems);

        return OrderResponse.of(
                savedOrder.getId(),
                savedOrder.getStatus(),
                savedOrder.getTotalAmount(),
                items
        );
    }

    public OrderResponse findOrder(Long orderId, Long userId) {
        AppUser user = getUser(userId);
        Order order = getOrder(orderId, user);
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order);

        List<OrderItemResponse> items = orderItems.stream()
                .map(OrderItemResponse::from)
                .toList();

        return OrderResponse.of(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                items
        );
    }

    public Page<OrderSummaryResponse> findOrders(Long userId, Pageable pageable) {
        AppUser user = getUser(userId);

        return orderRepository.findAllByUser(user, pageable)
                .map(OrderSummaryResponse::from);
    }

    private AppUser getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Cart getCart(AppUser user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));
    }

    private List<CartItem> getCartItems(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findAllByCart(cart);

        if (cartItems.isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_CART);
        }

        return cartItems;
    }

    private Order getOrder(Long orderId, AppUser user) {
        return orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    private BigDecimal calculateTotalAmount(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> cartItem.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateOrderableItems(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (!product.isActive()) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_ORDERABLE);
            }

            if (product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
            }
        }
    }

}
