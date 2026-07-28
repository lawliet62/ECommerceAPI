# ECommerceAPI

사용자 인증, 상품 관리, 장바구니, 주문, 결제 상태 전이를 구현한 Spring Boot REST API다.

## 주요 기능

- 이메일 회원가입과 로그인, stateless JWT 인증
- 공개 상품 검색·조회
- 관리자 상품 생성·수정·재고 및 활성 상태 관리
- 사용자별 장바구니 관리
- 장바구니 기반 주문 생성과 상품명·가격 스냅샷
- 결제 생성·조회 및 완료·실패·취소 시뮬레이션
- 결제 완료 시 재고 차감과 주문 상태 전이

현재 실제 PG 연동이나 카드 결제 처리는 제공하지 않는다.

## 기술 스택

- Java 21
- Spring Boot 4.0.6
- Spring MVC, Spring Data JPA, Spring Security
- MySQL 9.7, Flyway
- JWT (`jjwt`)
- Gradle

## 로컬 실행

```powershell
docker compose up -d
.\gradlew.bat bootRun
```

테스트:

```powershell
.\gradlew.bat test
```

애플리케이션은 기본적으로 `localhost:3306/mydatabase`의 MySQL에 연결한다. 상세 설정과 명령은 [로컬 개발 문서](docs/development.md)를 참고한다.

## 문서

- [제품 목적과 범위](docs/product.md)
- [아키텍처와 주요 흐름](docs/architecture.md)
- [도메인 규칙](docs/domain)
- [설계 결정](docs/decisions/README.md)
- [현재 제한 사항](docs/known-limitations.md)
- [에이전트 작업 지침](AGENTS.md)
