# 주문 도메인 규칙

## 범위

주문 도메인은 장바구니 기반 주문 생성, 주문 상품 스냅샷, 주문 총액, 주문 취소를 관리한다.

## 비즈니스 규칙

- 주문은 반드시 한 사용자에게 속한다.
- 주문은 존재하는 장바구니를 기준으로만 생성할 수 있다.
- 빈 장바구니로는 주문을 생성할 수 없다.
- 주문 생성 시 모든 장바구니 상품은 활성 상품이어야 한다.
- 주문 생성 시 모든 장바구니 상품은 충분한 재고가 있어야 한다.
- 주문 총액은 현재 상품 가격과 장바구니 수량으로 계산한다.
- 주문 총액은 0보다 커야 한다.
- 새 주문의 기본 상태는 `PENDING_PAYMENT`다.
- 주문 상품에는 상품명과 가격을 스냅샷으로 저장한다.
- 주문 상품 수량은 1 이상 99 이하여야 한다.
- 주문 상품 생성 후 주문에 사용한 장바구니 상품은 삭제한다.
- 주문은 주문 소유자만 조회할 수 있다.
- 주문은 `PENDING_PAYMENT` 상태에서만 취소할 수 있다.

## DB 무결성 및 제약 조건

- `orders.user_id`는 `NOT NULL`이며 `users.id`를 참조한다.
- `orders.status`는 `NOT NULL`이다.
- `orders.status`는 `PENDING_PAYMENT`, `PAID`, `CANCELLED` 값만 허용한다.
- `orders.total_amount`는 `NOT NULL`이며 `DECIMAL(15, 2)`로 저장한다.
- `orders.total_amount`는 0보다 커야 한다.
- `order_item.order_id`와 `order_item.product_id`는 `NOT NULL`이다.
- `order_item.order_id`는 `orders.id`를 참조한다.
- `order_item.product_id`는 `product.id`를 참조한다.
- `order_item.product_name_snapshot`은 `NOT NULL`이며 100자로 제한한다.
- `order_item.price_snapshot`은 `NOT NULL`이며 `DECIMAL(15, 2)`로 저장한다.
- `order_item.price_snapshot`은 0보다 커야 한다.
- `order_item.quantity`는 1 이상 99 이하로 제한한다.

## 애플리케이션에서 보장하는 규칙

- 주문 총액과 주문 상품 합계의 일관성은 `OrderService`에서 계산하며 DB 제약으로 강제하지 않는다.
- 주문 상태 전이 규칙은 `Order` 애그리거트에서 보장한다.
- 주문 생성 시 상품 활성 여부와 재고 검증은 `OrderService`에서 수행한다.
- 현재 설계는 주문 생성 시점이 아니라 결제 완료 시점에 재고를 차감한다.
