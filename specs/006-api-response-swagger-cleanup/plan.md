# 006 표준 API 응답과 Swagger 정리 계획

## 아키텍처

공통 Web 응답 정책과 Swagger 문서화 정책은 `global.web` 하위에 둔다. 도메인과 애플리케이션 계층은 표준 HTTP 응답 포맷이나 Swagger 구현에 의존하지 않는다.

## 구현

- `global.web.ApiResponse<T>`를 추가해 성공/실패 응답 생성 팩터리를 제공한다.
- `GlobalExceptionHandler`는 기존 `ErrorResponse` 대신 `ApiResponse<Void>`를 반환한다.
- `PostController`, `AuthController`는 성공 응답 DTO를 `ApiResponse.success(...)`로 감싼다.
- 이 명세의 최초 구현은 `ApiResponse<T>`에 `code` 필드를 두지 않고 실패 응답의 `message`에 에러 코드를 담는다.
- 이후 한글 에러 응답 정책은 `007-global-exception-policy`와 `008-post-create-auth-korean-errors`에 따라 실패 응답에 `code` 필드를 추가하고 `message`에는 한글 설명을 담는 방향으로 확장한다.
- `logout`은 기존처럼 `204 No Content`를 반환한다.
- `global.web.swagger` 패키지에 `CreatePostApiDocs`, `SignupApiDocs`, `LoginApiDocs`, `RefreshTokenApiDocs`, `LogoutApiDocs`를 추가한다.
- 컨트롤러에서 직접 작성한 긴 Swagger 응답 예시와 `ErrorResponse` 참조를 제거한다.

## 문서

- `docs/CONVENTIONS.md`에 API별 Swagger 문서 어노테이션 우선 사용 규칙을 추가한다.
- `docs/INDEX.md`에 새 기능 명세 링크를 추가한다.

## 테스트 전략

- Web 어댑터 테스트의 성공 응답 검증 경로를 `$.data.*`로 변경한다.
- 성공 응답은 `code` 필드가 없고 한글 `message`를 반환하는지 검증한다.
- 최초 구현의 실패 응답은 `success=false`, 에러 코드 `message`, `path`, `timestamp`, `errors`를 검증한다.
- 한글 에러 응답 확장 후에는 실패 응답의 `code=<ERROR_CODE>`, `message=<한글 설명>`을 검증한다.
- 로그아웃은 `204 No Content`와 빈 본문을 유지하는지 검증한다.
- `.\gradlew.bat test`로 전체 테스트를 실행한다.
