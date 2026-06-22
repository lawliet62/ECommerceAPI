package org.example.ecommerceapi.domain.order.service;

import org.example.ecommerceapi.common.exception.BusinessException;
import org.example.ecommerceapi.common.exception.ErrorCode;
import org.example.ecommerceapi.domain.cart.entity.Cart;
import org.example.ecommerceapi.domain.cart.entity.CartItem;
import org.example.ecommerceapi.domain.cart.repository.CartItemRepository;
import org.example.ecommerceapi.domain.cart.repository.CartRepository;
import org.example.ecommerceapi.domain.order.dto.OrderResponse;
import org.example.ecommerceapi.domain.order.dto.OrderSummaryResponse;
import org.example.ecommerceapi.domain.order.entity.Order;
import org.example.ecommerceapi.domain.order.entity.OrderItem;
import org.example.ecommerceapi.domain.order.entity.OrderStatus;
import org.example.ecommerceapi.domain.order.repository.OrderItemRepository;
import org.example.ecommerceapi.domain.order.repository.OrderRepository;
import org.example.ecommerceapi.domain.product.entity.Product;
import org.example.ecommerceapi.domain.user.entity.AppUser;
import org.example.ecommerceapi.domain.user.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_withValidCart_createsOrderAndOrderItems() {
        AppUser user = createUser();
        Product product1 = createProduct("Keyboard");
        Product product2 = createProduct("Mouse");
        Cart cart = Cart.create(user);
        CartItem cartItem1 = CartItem.create(cart, product1, 2);
        CartItem cartItem2 = CartItem.create(cart, product2, 3);
        List<CartItem> cartItems = List.of(cartItem1, cartItem2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findAllByCart(cart)).thenReturn(cartItems);
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(orderItemRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.createOrder(1L);

        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).saveAll(anyList());
        verify(cartItemRepository).deleteAll(cartItems);

        assertEquals(OrderStatus.PENDING_PAYMENT, response.status());
        assertEquals(BigDecimal.valueOf(50000), response.totalAmount());
        assertEquals(2, response.items().size());

        assertEquals("Keyboard", response.items().get(0).productName());
        assertEquals(BigDecimal.valueOf(10000), response.items().get(0).price());
        assertEquals(2, response.items().get(0).quantity());
        assertEquals(BigDecimal.valueOf(20000), response.items().get(0).subtotal());

        assertEquals("Mouse", response.items().get(1).productName());
        assertEquals(BigDecimal.valueOf(10000), response.items().get(1).price());
        assertEquals(3, response.items().get(1).quantity());
        assertEquals(BigDecimal.valueOf(30000), response.items().get(1).subtotal());
    }

    @Test
    void createOrder_withMissingCart_throwsCartNotFound() {
        AppUser user = createUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> orderService.createOrder(1L)
                );

        assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createOrder_withEmptyCart_throwsEmptyCart() {
        AppUser user = createUser();
        Cart cart = Cart.create(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findAllByCart(cart)).thenReturn(List.of());

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> orderService.createOrder(1L)
                );

        assertEquals(ErrorCode.EMPTY_CART, exception.getErrorCode());
    }

    @Test
    void createOrder_withInactiveProduct_throwsProductNotOrderable() {
        AppUser user = createUser();
        Product product1 = createProduct("Keyboard");
        Product product2 = createProduct("Mouse");
        product2.deactivate();
        Cart cart = Cart.create(user);
        CartItem cartItem1 = CartItem.create(cart, product1, 2);
        CartItem cartItem2 = CartItem.create(cart, product2, 3);
        List<CartItem> cartItems = List.of(cartItem1, cartItem2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findAllByCart(cart)).thenReturn(cartItems);

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> orderService.createOrder(1L)
                );

        assertEquals(ErrorCode.PRODUCT_NOT_ORDERABLE, exception.getErrorCode());
    }

    @Test
    void createOrder_withInsufficientStock_throwsInsufficientStock() {
        AppUser user = createUser();
        Product product1 = createProduct("Keyboard");
        Product product2 = createProduct("Mouse");
        Cart cart = Cart.create(user);
        CartItem cartItem1 = CartItem.create(cart, product1, 2);
        CartItem cartItem2 = CartItem.create(cart, product2, 13);
        List<CartItem> cartItems = List.of(cartItem1, cartItem2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findAllByCart(cart)).thenReturn(cartItems);

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> orderService.createOrder(1L)
                );

        assertEquals(ErrorCode.INSUFFICIENT_STOCK, exception.getErrorCode());
    }

    @Test
    void findOrder_withOwnedOrder_returnsOrderResponse() {
        AppUser user = createUser();
        Product product1 = createProduct("Keyboard");
        Product product2 = createProduct("Mouse");
        Order order = Order.create(user, BigDecimal.valueOf(50000));
        OrderItem orderItem1 = OrderItem.create(order, product1, 2);
        OrderItem orderItem2 = OrderItem.create(order, product2, 3);

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrder(order)).thenReturn(List.of(orderItem1, orderItem2));

        OrderResponse response = orderService.findOrder(1L, 1L);

        assertEquals(OrderStatus.PENDING_PAYMENT, response.status());
        assertEquals(BigDecimal.valueOf(50000), response.totalAmount());
        assertEquals(2, response.items().size());

        assertEquals("Keyboard", response.items().get(0).productName());
        assertEquals(BigDecimal.valueOf(10000), response.items().get(0).price());
        assertEquals(2, response.items().get(0).quantity());
        assertEquals(BigDecimal.valueOf(20000), response.items().get(0).subtotal());

        assertEquals("Mouse", response.items().get(1).productName());
        assertEquals(BigDecimal.valueOf(10000), response.items().get(1).price());
        assertEquals(3, response.items().get(1).quantity());
        assertEquals(BigDecimal.valueOf(30000), response.items().get(1).subtotal());
    }

    @Test
    void findOrder_withMissingOrOtherUsersOrder_throwsOrderNotFound() {
        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> orderService.findOrder(1L, 1L));

        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void findOrders_returnsUserOrderSummaries() {
        AppUser user = createUser();
        Order order1 = Order.create(user, BigDecimal.valueOf(50000));
        Order order2 = Order.create(user, BigDecimal.valueOf(30000));
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = createOrderPage(order1, order2);

        when(orderRepository.findAllByUserId(1L, pageable)).thenReturn(page);

        Page<OrderSummaryResponse> responses = orderService.findOrders(1L, pageable);

        assertEquals(2, responses.getTotalElements());
        assertEquals(OrderStatus.PENDING_PAYMENT, responses.getContent().get(0).status());
        assertEquals(OrderStatus.PENDING_PAYMENT, responses.getContent().get(1).status());
        assertEquals(BigDecimal.valueOf(50000), responses.getContent().get(0).totalAmount());
        assertEquals(BigDecimal.valueOf(30000), responses.getContent().get(1).totalAmount());
    }

    @Test
    void cancelOrder_whenPendingPayment_cancelsOrder() {
        Order order = Order.create(createUser(), BigDecimal.valueOf(50000));

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L, 1L);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void cancelOrder_whenNotCancelable_throwsOrderNotCancelable() {
        Order order = Order.create(createUser(), BigDecimal.valueOf(50000));
        order.cancel();

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        BusinessException exception =
                assertThrows(
                        BusinessException.class,
                        () -> orderService.cancelOrder(1L, 1L)
                );

        assertEquals(ErrorCode.ORDER_NOT_CANCELABLE, exception.getErrorCode());
    }

    private AppUser createUser() {
        return AppUser.createUser("user@example.com", "encodedPassword");
    }

    private Product createProduct(String name) {
        return Product.create(name, BigDecimal.valueOf(10000), 10);
    }

    private Pageable createPageable() {
        return PageRequest.of(0, 10);
    }

    private Page<Order> createOrderPage(Order... orders) {
        return new PageImpl<>(List.of(orders), createPageable(), orders.length);
    }
}
