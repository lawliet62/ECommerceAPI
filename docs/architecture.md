# 아키텍처

## 전체 구조

애플리케이션은 하나의 Spring Boot 프로세스와 하나의 MySQL 데이터베이스로 구성된 계층형 모놀리스다.

도메인별 패키지는 대체로 다음 책임을 가진다.

- `controller`: HTTP 경로, 요청 검증, 인증 주체 추출, 응답 상태
- `service`: 유스케이스 조정, Repository 접근, 트랜잭션 경계
- `entity`: 값 검증과 상태 전이
- `repository`: Spring Data JPA 기반 영속성 및 소유권 범위 조회
- `dto`: API 요청과 응답 모델

공통 예외는 `common.exception`, JWT 인증은 `security`, 접근 정책은 `config.SecurityConfig`에 있다.

## 데이터 모델

- `AppUser` 1:1 `Cart`
- `Cart` 1:N `CartItem`
- `CartItem` N:1 `Product`
- `AppUser` 1:N `Order`
- `Order` 1:N `OrderItem`
- `OrderItem` N:1 `Product`
- `Order` 1:N `Payment`

`OrderItem`은 `Product`를 참조하면서도 주문 당시 상품명과 가격을 별도 스냅샷으로 저장한다. 데이터베이스 제약의 공식 원본은 `src/main/resources/db/migration`의 Flyway 파일이다.

## 주요 흐름

### 인증

1. 회원가입 서비스가 이메일 중복을 검사하고 비밀번호를 BCrypt로 암호화한다.
2. 로그인 서비스가 비밀번호를 검증하고 사용자 ID를 subject로 가진 JWT를 발급한다.
3. `JwtAuthenticationFilter`가 Bearer token을 검증하고 사용자가 DB에 존재할 때 사용자 ID와 역할을 SecurityContext에 넣는다.

서버 세션은 생성하지 않는다.

### 장바구니

상품 추가 시 사용자 장바구니가 없으면 생성한다. 활성 상품과 현재 재고를 검증하고, 동일 상품이 이미 있으면 수량을 증가시킨다. 장바구니에 담는 행위는 재고를 예약하지 않는다.

### 주문 생성

`OrderService.createOrder`의 하나의 쓰기 트랜잭션에서 다음 작업을 수행한다.

1. 사용자와 장바구니 조회
2. 빈 장바구니, 상품 활성 상태, 현재 재고 검증
3. 현재 상품 가격으로 총액 계산
4. `PENDING_PAYMENT` 주문 생성
5. 상품명·가격 스냅샷을 가진 주문 항목 생성
6. 사용한 장바구니 항목 제거

이 트랜잭션에서는 재고를 차감하지 않는다.

### 결제 완료

`PaymentService.completePayment`의 하나의 쓰기 트랜잭션에서 다음 작업을 수행한다.

1. 소유권이 확인된 `PENDING` 결제 조회
2. 주문이 `PENDING_PAYMENT`인지 재검증
3. 주문 항목이 참조하는 상품 재고 차감
4. 결제를 `SUCCESS`로 전환
5. 주문을 `PAID`로 전환

어느 단계에서든 예외가 발생하면 해당 트랜잭션의 변경은 롤백된다. 결제 실패와 취소는 결제 상태만 변경하며 주문은 결제 대기 상태로 유지한다.

## 트랜잭션 경계

서비스 클래스는 기본적으로 `@Transactional(readOnly = true)`이며 상태를 변경하는 공개 서비스 메서드에 `@Transactional`을 선언한다.

- 회원가입
- 상품 생성·수정·재고·활성 상태 변경
- 장바구니 추가·수정·제거
- 주문 생성·취소
- 결제 생성·완료·실패·취소

컨트롤러와 Repository는 트랜잭션 경계가 아니다.

## 인증과 인가

- 공개: `/auth/register`, `/auth/login`, `/products/**`
- 관리자: `/admin/**`, `ROLE_ADMIN` 필요
- 인증 필요: `/cart/**`, `/orders/**`, `/payments/**` 및 그 밖의 경로

장바구니·주문·결제 소유권은 서비스가 인증 사용자 ID를 Repository 조회 조건에 포함해 보장한다. 인증된 `ADMIN`도 일반 인증 API에는 접근할 수 있다.

인증 실패는 401이며 관리자 권한 부족은 Spring Security의 기본 접근 거부 처리 대상이다.

## 외부 시스템 경계

현재 외부 시스템은 로컬 MySQL뿐이다. `payment.gateway.PaymentGateway` 인터페이스는 선언되어 있지만 메서드나 구현체가 없고 서비스에서도 사용하지 않는다. 결제 결과는 HTTP API를 통한 내부 시뮬레이션으로 처리한다.
