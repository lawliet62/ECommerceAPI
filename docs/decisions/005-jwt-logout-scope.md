# JWT 로그아웃 범위

## Context

현재 인증 방식은 상태를 저장하지 않는 stateless JWT 인증이다.

`SecurityConfig`는 `SessionCreationPolicy.STATELESS`로 서버 측 세션 상태를 사용하지 않으며, 인증이 필요한 요청은 `JwtAuthenticationFilter`에서 access token을 검증해 식별한다.

이 모델에서는 서버가 로그인 세션을 보관하지 않으므로, 일반적인 세션 기반 로그아웃처럼 서버에서 세션을 무효화할 대상이 없다.

## Decision

현재 프로젝트 범위에서는 서버 측 로그아웃을 구현하지 않는다.

로그아웃은 클라이언트가 발급받은 JWT를 폐기하는 방식으로 처리한다.

## Policy

- 프로젝트가 stateless access token만 발급하는 동안에는 `/auth/logout`을 추가하지 않는다.
- 현재 범위에서는 token blacklist, token revocation 저장소, refresh token 영속화를 도입하지 않는다.
- refresh token, 서버 측 토큰 저장소, 강제 계정 차단, 즉시 권한 회수 같은 요구사항이 생기면 이 결정을 재검토하고 서버 측 로그아웃/토큰 폐기 흐름을 설계한다.

## Reason

서버 측 토큰 상태가 없는 상태에서 로그아웃 엔드포인트를 추가해도 이미 발급된 access token을 실제로 무효화할 수 없다.

토큰 폐기 저장소나 blacklist를 추가하면 현재 제품 범위를 넘어서는 구현 복잡도와 운영 복잡도가 생긴다.

현재 우선순위는 인증 구조를 단순하게 유지하면서, 공개 API, 인증 사용자 API, 관리자 전용 API의 접근 제어 규칙을 명확히 검증하는 것이다.
