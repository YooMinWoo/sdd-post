# 008 게시글 작성 인증과 한글 에러 응답 계획

## 아키텍처

게시글 작성 인증은 Web 어댑터와 보안 설정에서 처리하고, 게시글 생성 유스케이스는 인증된 회원 id를 입력으로 받는다. 도메인 계층은 Spring Security, JWT, HTTP에 의존하지 않는다.

## 구현 방향

- `SecurityConfig`는 `/auth/signup`, `/auth/login`, `/auth/refresh`, `/auth/logout` 같은 인증 관련 공개 API만 허용하고 `POST /posts`는 인증을 요구한다.
- JWT accessToken 검증은 refreshToken 검증과 분리한다. `TokenProviderPort`는 `AccessTokenMemberClaims(memberId, email, nickname)`를 반환한다.
- 인증 필터는 `Authorization: Bearer <token>` 헤더를 검증하고 `global.security.AuthenticatedMemberPrincipal`을 Spring Security 인증 객체에 담는다.
- `PostController`는 `@AuthenticationPrincipal AuthenticatedMemberPrincipal`의 회원 id를 사용해 `CreatePostCommand`를 생성한다.
- `CreatePostRequest`의 `author` 필드는 제거한다. 하위 호환이 필요한 경우 받더라도 저장 작성자 결정에는 사용하지 않는다.
- `CreatePostCommand`는 제목, 본문, 인증 회원 id를 가진다.
- `Post` 도메인 모델은 `author` 문자열 대신 `authorMemberId`를 가진다.
- `posts` 테이블은 `author` 문자열 컬럼 대신 `author_member_id` 단순 컬럼을 가진다. DB 마이그레이션 정책이 아직 없으므로 FK 제약은 후속 명세에서 다룬다.
- 생성 성공 응답에 작성자 표시가 필요하면 `authorMemberId`로 회원 정보를 조회해 현재 닉네임을 조합한다.
- 전역 예외 처리는 `ErrorCode.code()`를 실패 응답 `code`로, `ErrorCode.description()`을 실패 응답 `message`로 사용한다.
- 인증 실패 에러 코드는 회원/인증 컨텍스트의 `MemberErrorCode`에 둔다.

## Lombok 정책

- Domain Model에는 `@Getter` 정도만 선택적으로 허용하고 `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`는 사용하지 않는다.
- JPA Entity에는 JPA 요구사항을 만족하는 `@NoArgsConstructor(access = AccessLevel.PROTECTED)`만 제한적으로 허용한다.
- JPA Entity의 `@AllArgsConstructor`, `@Builder`는 불필요한 생성 경로를 늘리므로 기본 도입하지 않는다.
- 기존 private constructor, 정적 `from(...)`, `toDomain()` 변환 방식은 유지한다.

## 에러 응답 매핑

| 상황 | 에러 코드 | 한글 메시지 | HTTP 상태 |
| --- | --- | --- | --- |
| Authorization 헤더 없음 | `UNAUTHORIZED` | 로그인이 필요합니다. | `401 Unauthorized` |
| Bearer 형식 오류 | `INVALID_ACCESS_TOKEN` | 유효하지 않은 accessToken입니다. | `401 Unauthorized` |
| accessToken 서명 오류 | `INVALID_ACCESS_TOKEN` | 유효하지 않은 accessToken입니다. | `401 Unauthorized` |
| accessToken 만료 | `INVALID_ACCESS_TOKEN` | 유효하지 않은 accessToken입니다. | `401 Unauthorized` |
| refreshToken으로 게시글 작성 시도 | `INVALID_ACCESS_TOKEN` | 유효하지 않은 accessToken입니다. | `401 Unauthorized` |
| 게시글 제목 누락 | `POST_TITLE_REQUIRED` | 게시글 제목은 필수입니다. | `400 Bad Request` |
| 게시글 제목 길이 초과 | `POST_TITLE_TOO_LONG` | 게시글 제목은 최대 100자까지 허용됩니다. | `400 Bad Request` |

## 기존 명세와의 관계

- `001-post-create`의 비범위였던 인증/인가는 이 명세에서 게시글 작성 API에 한해 범위로 편입한다.
- `005-member-login`의 accessToken 발급 정책을 게시글 작성 인증에 사용한다.
- `005-member-login`의 `sub` claim을 게시글 작성자 회원 id로 사용하고, `nickname` claim은 저장 값으로 사용하지 않는다.
- `007-global-exception-policy`의 기존 `message=<ERROR_CODE>` 규칙은 `code=<ERROR_CODE>`, `message=<한글 설명>`으로 갱신한다.
- `006-api-response-swagger-cleanup`의 공통 응답 구조는 유지하되 실패 응답에 `code` 필드를 추가하는 방향으로 확장한다.

## 테스트 전략

- Web 어댑터 테스트에서 토큰 없이 `POST /posts` 호출 시 `401`과 `code=UNAUTHORIZED`, 한글 `message`를 검증한다.
- 잘못된 accessToken, 만료된 accessToken, refreshToken 사용 시 `401`과 `code=INVALID_ACCESS_TOKEN`을 검증한다.
- 유효한 accessToken으로 호출하면 `201 Created`와 토큰 `sub` 기반 `authorMemberId` 저장을 검증한다.
- 요청 본문에 `author`나 닉네임이 포함되어도 저장 작성자 id를 바꾸지 않는지 검증한다.
- 회원 닉네임 변경 후 조회 기능이 생기면 저장된 `authorMemberId`로 최신 닉네임을 반환하는지 검증한다.
- 도메인 모델 생성은 정적 팩터리만 사용하고, 필수 값 누락 시 명시적 에러 코드가 발생하는지 검증한다.
- 전역 예외 처리 테스트에서 대표 예외가 영문 `code`와 한글 `message`를 함께 반환하는지 검증한다.
