package org.example.ecommerceapi.domain.cart.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void addItem_withoutCart_createsCartAndCartItem() {
        Long userId = 1L;
        Long productId = 1L;
        AppUser user = createUser();
        Product product = createProduct();

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(productRepository.findByIdAndActiveTrue(productId))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartAndProduct(any(Cart.class), eq(product)))
                .thenReturn(Optional.empty());

        when(cartItemRepository.save(any(CartItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CartItemResponse response = cartService.addItem(userId, productId, 1);

        verify(cartRepository).save(any(Cart.class));
        verify(cartItemRepository).save(any(CartItem.class));

        assertEquals(product.getName(), response.productName());
        assertEquals(product.getPrice(), response.price());
        assertEquals(1, response.quantity());
        assertEquals(BigDecimal.valueOf(10000), response.subtotal());
    }

    @Test
    void addItem_withExistingCartAndMissingCartItem_createsCartItem() {
        Long userId = 1L;
        Long productId = 1L;
        AppUser user = createUser();
        Cart cart = Cart.create(user);
        Product product = createProduct();

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));

        when(productRepository.findByIdAndActiveTrue(productId))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartAndProduct(any(Cart.class), eq(product)))
                .thenReturn(Optional.empty());

        when(cartItemRepository.save(any(CartItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CartItemResponse response = cartService.addItem(userId, productId, 1);

        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartItemRepository).save(any(CartItem.class));

        assertEquals(product.getName(), response.productName());
        assertEquals(product.getPrice(), response.price());
        assertEquals(1, response.quantity());
        assertEquals(BigDecimal.valueOf(10000), response.subtotal());
    }

    @Test
    void addItem_withExistingProductInCart_increasesQuantity() {
        Long userId = 1L;
        Long productId = 1L;
        AppUser user = createUser();
        Cart cart = Cart.create(user);
        Product product = createProduct();
        CartItem cartItem = CartItem.create(cart, product, 1);

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));

        when(productRepository.findByIdAndActiveTrue(productId))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartAndProduct(any(Cart.class), eq(product)))
                .thenReturn(Optional.of(cartItem));

        CartItemResponse response = cartService.addItem(userId, productId, 1);

        verify(cartItemRepository, never()).save(any(CartItem.class));
        verify(cartRepository, never()).save(any(Cart.class));

        assertEquals(product.getName(), response.productName());
        assertEquals(product.getPrice(), response.price());
        assertEquals(2, response.quantity());
        assertEquals(BigDecimal.valueOf(20000), response.subtotal());
    }

    @Test
    void addItem_withInactiveOrMissingProduct_throwsProductNotFound() {
        Long userId = 1L;
        Long productId = 1L;
        AppUser user = createUser();
        Cart cart = Cart.create(user);

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));

        when(productRepository.findByIdAndActiveTrue(productId))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cartService.addItem(userId, productId, 1)
        );

        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void addItem_whenRequestedQuantityExceedsStock_throwsInsufficientStock() {
        Long userId = 1L;
        Long productId = 1L;
        AppUser user = createUser();
        Cart cart = Cart.create(user);
        Product product = createProduct();

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));

        when(productRepository.findByIdAndActiveTrue(productId))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartAndProduct(any(Cart.class), eq(product)))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cartService.addItem(userId, productId, 20)
        );

        verify(cartItemRepository, never()).save(any(CartItem.class));

        assertEquals(ErrorCode.INSUFFICIENT_STOCK, exception.getErrorCode());
    }

    @Test
    void addItem_whenTotalQuantityExceedsStock_throwsInsufficientStock() {
        Long userId = 1L;
        Long productId = 1L;
        AppUser user = createUser();
        Cart cart = Cart.create(user);
        Product product = createProduct();
        CartItem cartItem = CartItem.create(cart, product, 7);

        when(cartRepository.findByUserId(userId))
                .thenReturn(Optional.of(cart));

        when(productRepository.findByIdAndActiveTrue(productId))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartAndProduct(any(Cart.class), eq(product)))
                .thenReturn(Optional.of(cartItem));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cartService.addItem(userId, productId, 8)
        );

        verify(cartItemRepository, never()).save(any(CartItem.class));

        assertEquals(ErrorCode.INSUFFICIENT_STOCK, exception.getErrorCode());
    }

    @Test
    void getCart_withoutCart_returnsEmptyResponse() {
        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.empty());

        CartResponse response = cartService.getCart(1L);
        assertEquals(CartResponse.empty(), response);
    }

    @Test
    void getCart_withCart_returnsItems() {
        AppUser user = createUser();
        Cart cart = Cart.create(user);
        Product product1 = createProduct();
        Product product2 = createProduct();
        CartItem cartItem1 = CartItem.create(cart, product1, 7);
        CartItem cartItem2 = CartItem.create(cart, product2, 3);

        List<CartItem> cartItems = List.of(cartItem1, cartItem2);
        List<CartItemResponse> cartItemResponses = cartItems.stream()
                .map(CartItemResponse::from)
                .toList();

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findAllByCart(cart))
                .thenReturn(cartItems);

        CartResponse response = cartService.getCart(1L);

        assertEquals(cartItemResponses, response.items());
    }

    @Test
    void updateItem_updatesQuantity() {
        AppUser user = createUser();
        Cart cart = Cart.create(user);
        Product product = createProduct();
        CartItem cartItem = CartItem.create(cart, product, 1);

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByIdAndCart(1L, cart))
                .thenReturn(Optional.of(cartItem));

        CartItemResponse response = cartService.updateItem(1L, 1L, 2);

        assertEquals(2, response.quantity());
        assertEquals(BigDecimal.valueOf(20000), response.subtotal());
    }

    @Test
    void updateItem_withMissingCart_throwsCartNotFound() {
        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cartService.updateItem(1L, 1L, 2)
        );

        assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateItem_whenCartItemNotFoundInCart_throwsCartItemNotFound() {
        AppUser user = createUser();
        Cart cart = Cart.create(user);

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByIdAndCart(1L, cart))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cartService.updateItem(1L, 1L, 2)
        );

        assertEquals(ErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateItem_whenQuantityExceedsStock_throwsInsufficientStock() {
        AppUser user = createUser();
        Cart cart = Cart.create(user);
        Product product = createProduct();
        CartItem cartItem = CartItem.create(cart, product, 1);

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByIdAndCart(1L, cart))
                .thenReturn(Optional.of(cartItem));

        BusinessException exception = assertThrows(
                BusinessException.class,

                () -> cartService.updateItem(1L, 1L, 20)
        );

        assertEquals(ErrorCode.INSUFFICIENT_STOCK, exception.getErrorCode());
    }

    @Test
    void removeItem_deletesCartItem() {
        AppUser user = createUser();
        Cart cart = Cart.create(user);
        CartItem cartItem = CartItem.create(cart, createProduct(), 1);

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByIdAndCart(1L, cart))
                .thenReturn(Optional.of(cartItem));

        cartService.removeItem(1L, 1L);

        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void removeItem_withMissingCart_throwsCartNotFound() {
        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,

                () -> cartService.removeItem(1L, 1L)
        );

        assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void removeItem_whenCartItemNotFoundInCart_throwsCartItemNotFound() {
        AppUser user = createUser();
        Cart cart = Cart.create(user);

        when(cartRepository.findByUserId(1L))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByIdAndCart(1L, cart))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,

                () -> cartService.removeItem(1L, 1L)
        );

        assertEquals(ErrorCode.CART_ITEM_NOT_FOUND, exception.getErrorCode());
    }

    private AppUser createUser() {
        return AppUser.createUser("user@example.com", "encodedPassword");
    }

    private Product createProduct() {
        return Product.create("Keyboard", BigDecimal.valueOf(10000), 10);
    }
}
