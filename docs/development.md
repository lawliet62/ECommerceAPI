# 로컬 개발과 검증

## 요구 사항

- Java 21
- Docker와 Docker Compose 또는 별도로 실행 중인 MySQL
- 저장소에 포함된 Gradle Wrapper

## 데이터베이스

`compose.yaml`은 다음 개발용 MySQL을 실행한다.

- 이미지: `mysql:9.7`
- 포트: `3306`
- 데이터베이스: `mydatabase`
- 사용자: `myuser`

```powershell
docker compose up -d
docker compose ps
```

애플리케이션 시작 시 Flyway가 `src/main/resources/db/migration`을 순서대로 적용하고 Hibernate는 스키마를 `validate`한다.

현재 `application.yml`의 DB 암호와 JWT secret은 로컬 개발용 고정값이다. 운영 비밀정보로 사용해서는 안 된다.

## 실행

Windows:

```powershell
.\gradlew.bat bootRun
```

macOS/Linux:

```shell
./gradlew bootRun
```

## 테스트와 빌드

```powershell
.\gradlew.bat test
.\gradlew.bat build
```

테스트는 엔티티 규칙, 서비스 흐름, DTO 검증, MVC 컨트롤러, JWT 필터와 보안 경로를 포함한다. 변경 시 우선 검증할 흐름은 다음과 같다.

- 재고가 충분하거나 부족한 주문·결제 완료
- 주문 상품 가격 스냅샷
- 결제 성공·실패·취소 상태 전이
- 장바구니·주문·결제 소유권
- 비관리자의 관리자 API 차단
- 요청 검증과 공통 오류 응답

Lombok `@NonNull`이 생성한 단순 null 검사 자체를 반복 검증하기보다 애플리케이션이 직접 정의한 도메인 규칙과 상태 전이를 우선 테스트한다. null 여부가 도메인 규칙이면 명시적으로 검증하고 테스트한다.

## 스키마 변경

- 기존에 적용된 Flyway 파일을 수정하지 않는다.
- 다음 버전 번호의 새 마이그레이션을 추가한다.
- JPA 매핑, 데이터베이스 제약, `docs/domain`의 규칙을 함께 검토한다.
- 데이터 손실 가능성이 있는 변경은 적용 전에 승인을 받는다.

## 문서 변경

현재 동작이나 정책을 바꾼 작업은 같은 변경에서 관련 공식 문서를 갱신한다. 코드만으로 선택 이유를 알 수 없는 결정은 `docs/decisions`에 ADR로 기록하고 색인에 추가한다.
