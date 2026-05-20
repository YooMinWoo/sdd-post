# 007 전역 예외 처리 정책 계획

## 아키텍처

예외 정책은 DDD와 헥사고날 아키텍처 경계를 따른다. 도메인과 애플리케이션 계층은 의미 있는 예외와 에러 코드만 표현하고, HTTP 상태와 응답 변환은 `global.web.GlobalExceptionHandler`가 담당한다.

## 향후 구현 방향

- 공통 예외 기준으로 `BusinessException`을 두고, 에러 코드는 `ErrorCode` 인터페이스를 구현한 bounded context별 enum으로 분리한다.
- 각 에러 코드 enum 값은 코드 문자열과 설명을 제공한다.
- 도메인 불변식 실패는 도메인 의미가 드러나는 예외로 표현한다.
- 애플리케이션 유스케이스 실패는 유스케이스 의미가 드러나는 예외로 표현한다.
- 기존 `DuplicateEmailException`, `InvalidCredentialsException`, `InvalidRefreshTokenException`은 유지하되 공통 예외 계층으로 편입할 수 있다.
- 전역 예외 핸들러는 커스텀 예외를 먼저 처리하고, 마지막 fallback으로만 `IllegalArgumentException`과 `Exception`을 처리한다.
- `IllegalArgumentException` fallback은 기존 코드 호환을 위한 임시 안전망으로 두며 신규 기능의 기본 예외로 사용하지 않는다.

## 에러 응답 매핑

| BC | 예외/상황 | 응답 message | 설명 | HTTP 상태 |
| --- | --- | --- | --- | --- |
| global | 요청 본문 파싱 실패 | `MALFORMED_JSON` | 요청 본문 형식이 올바르지 않습니다. | `400 Bad Request` |
| global | 일반 요청 값 검증 실패 | `INVALID_REQUEST` | 요청 값이 올바르지 않습니다. | `400 Bad Request` |
| board | 게시글 제목 누락 | `POST_TITLE_REQUIRED` | 게시글 제목은 필수입니다. | `400 Bad Request` |
| board | 게시글 제목 길이 초과 | `POST_TITLE_TOO_LONG` | 게시글 제목은 최대 100자까지 허용됩니다. | `400 Bad Request` |
| member | 이메일 형식 오류 | `INVALID_EMAIL` | 이메일 형식이 올바르지 않습니다. | `400 Bad Request` |
| member | 비밀번호 누락 | `PASSWORD_REQUIRED` | 비밀번호는 필수입니다. | `400 Bad Request` |
| member | 비밀번호 길이 부족 | `PASSWORD_TOO_SHORT` | 비밀번호는 최소 8자 이상이어야 합니다. | `400 Bad Request` |
| member | 중복 이메일 | `DUPLICATE_EMAIL` | 이미 가입된 이메일입니다. | `409 Conflict` |
| member | 로그인 실패 | `INVALID_CREDENTIALS` | 이메일 또는 비밀번호가 올바르지 않습니다. | `401 Unauthorized` |
| member | refreshToken 오류 | `INVALID_REFRESH_TOKEN` | 유효하지 않은 refreshToken입니다. | `401 Unauthorized` |
| global | 알 수 없는 예외 | `INTERNAL_SERVER_ERROR` | 예상하지 못한 서버 오류가 발생했습니다. | `500 Internal Server Error` |

## 기존 명세와의 관계

- `001-post-create`, `003-member-signup`의 `IllegalArgumentException` 기반 도메인 검증 계획은 기존 구현 당시 정책이다.
- 신규 예외 정책은 이 문서를 우선 기준으로 삼고, 향후 구현 단계에서 기존 명세와 코드를 새 예외 체계로 점진적으로 갱신한다.
- `002-api-error-swagger`의 `IllegalArgumentException` 처리 규칙은 fallback 정책으로 축소한다.

## 테스트 전략

- 실제 구현 단계에서는 Web 어댑터 테스트에서 예외별 HTTP 상태와 `message=<ERROR_CODE>`를 검증한다.
- 도메인/애플리케이션 테스트는 구체 예외 타입 또는 에러 코드를 검증한다.
- fallback `IllegalArgumentException` 테스트는 기존 호환 경로로 최소화한다.
