# 설계 결정 기록

이 디렉터리는 현재 코드만으로 선택 이유를 알기 어려운 주요 결정을 기록한다.

| 번호 | 결정 | 상태 |
|---|---|---|
| [001](001-inventory-deduction-timing.md) | 결제 완료 시 재고 차감 | 적용 중 |
| [002](002-payment-failure-api.md) | API로 결제 실패 시뮬레이션 | 적용 중 |
| [004](004-authenticated-user-not-found-policy.md) | 인증 사용자 조회 실패를 `USER_NOT_FOUND`로 처리 | 적용 중 |
| [005](005-jwt-logout-scope.md) | stateless access token에서 서버 측 로그아웃 미구현 | 적용 중 |
| [006](006-allow-direct-cross-domain-dependencies.md) | 작은 모놀리스에서 도메인 간 직접 의존 허용 | 적용 중 |

새 ADR은 배경, 고려한 선택지, 결정, 결과와 재검토 조건을 포함한다. 결정이 대체되면 기존 기록을 삭제하기보다 상태와 후속 ADR을 표시한다.
