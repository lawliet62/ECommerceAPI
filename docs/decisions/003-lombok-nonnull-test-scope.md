# Lombok NonNull Test Scope

## Context

엔티티 정적 팩토리 메서드의 일부 매개변수에 Lombok의 `@NonNull`을 사용한다.

예를 들어 `CartItem.create(@NonNull Cart cart, @NonNull Product product, int quantity)`는 `cart`와 `product`가 `null`일 경우 Lombok이 생성한 null 체크에 의해 예외가 발생한다.

이때 테스트 코드에서 단순히 `null` 매개변수를 전달했을 때 예외가 발생하는지를 모두 검증할지 결정이 필요했다.

## Decision

현재 프로젝트에서는 **Lombok의 `@NonNull`로 방어되는 단순한 null 체크 케이스는 별도의 테스트 케이스로 작성하지 않는다.**

대신 엔티티가 직접 가지고 있는 도메인 규칙과 상태 변경 동작을 우선적으로 테스트한다.

## Policy

- Lombok의 `@NonNull`만으로 처리되는 단순 null 방어는 테스트 우선순위에서 제외한다.
- 수량, 가격, 재고, 상태 전이처럼 엔티티가 직접 검증하거나 변경하는 도메인 규칙은 테스트한다.
- null 여부 자체가 중요한 도메인 규칙으로 명확히 다뤄져야 하는 경우에는 Lombok에만 의존하지 않고 명시적인 검증 메서드를 작성한 뒤 테스트한다.
- `@NonNull`을 사용하는 경우 null 입력 시 발생하는 예외 타입은 Lombok 구현에 따라 `NullPointerException`이므로, 이를 도메인 예외로 간주하지 않는다.

## Reason

Lombok의 `@NonNull` 동작을 테스트하는 것은 애플리케이션의 도메인 규칙을 검증한다기보다 Lombok이 생성한 코드를 검증하는 성격이 강하다.

현재 학습 목표는 프레임워크나 라이브러리의 기본 동작을 반복 검증하는 것이 아니라, 직접 설계한 도메인 규칙과 서비스 흐름을 안정적으로 검증하는 것이다.

따라서 `CartItem` 테스트에서는 `cart`와 `product`의 단순 null 체크보다 `quantity` 유효성, 수량 증가, 수량 변경, 최대 수량 초과와 같은 장바구니 아이템의 핵심 규칙을 우선한다.
