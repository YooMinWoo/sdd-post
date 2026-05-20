# 007 전역 예외 처리 정책 명세

## 목표

도메인과 애플리케이션 규칙 위반을 `IllegalArgumentException` 하나로 처리하지 않고, 의미 있는 예외와 안정적인 에러 코드로 구분한다. 전역 예외 처리는 예외 타입 또는 에러 코드를 기준으로 표준 API 실패 응답을 생성한다.

## 사용자 이야기

API 사용자는 실패 원인을 안정적인 에러 코드로 구분하고 싶다. 개발자는 어떤 규칙이 깨졌는지 예외 타입과 코드만 보고 이해할 수 있어야 하며, 컨트롤러마다 예외 처리를 반복하고 싶지 않다.

## 범위

- 신규 기능에서 `IllegalArgumentException`을 도메인/애플리케이션 검증 실패의 기본 표현으로 사용하지 않는 정책을 정의한다.
- 요청 값 누락, 길이 초과, 형식 오류, 인증 실패, 중복 리소스 등 실패 유형별 에러 코드를 bounded context별 enum으로 정의한다.
- 전역 예외 핸들러가 커스텀 예외를 우선 처리하고 fallback에서만 `IllegalArgumentException`을 처리하는 방향을 정의한다.
- 실패 응답은 기존 표준 API 응답 정책을 확장해 `code`에 대표 에러 코드를, `message`에 한글 설명을 담는다.

## 비범위

- Java 코드 구현
- 기존 테스트 수정
- Bean Validation 도입
- 다국어 에러 메시지 정책

## 예외 처리 원칙

- 도메인/애플리케이션 규칙 위반은 의미 있는 커스텀 예외로 표현한다.
- `IllegalArgumentException`은 임시 호환 처리나 예상하지 못한 프로그래밍 오류 fallback으로만 남긴다.
- 도메인 예외는 Spring, Web, HTTP 상태 코드에 의존하지 않는다.
- 에러 코드 enum은 코드 문자열과 설명을 함께 제공하되, HTTP 상태는 포함하지 않는다.
- HTTP 상태와 표준 API 응답 변환은 전역 예외 핸들러가 담당한다.
- 컨트롤러는 예외를 직접 처리하지 않는다.
- 실패 응답의 `code`는 안정적인 에러 코드 문자열을 사용한다.
- 실패 응답의 `message`는 에러 코드의 한글 설명을 사용한다.

## 권장 에러 코드

공통 에러는 `GlobalErrorCode`, 게시글 에러는 `BoardErrorCode`, 회원/인증 에러는 `MemberErrorCode`에 둔다.

| BC | 상황 | 에러 코드 | 설명 | HTTP 상태 |
| --- | --- | --- | --- | --- |
| global | 요청 본문 형식 오류 | `MALFORMED_JSON` | 요청 본문 형식이 올바르지 않습니다. | `400 Bad Request` |
| global | 일반 요청 값 검증 실패 | `INVALID_REQUEST` | 요청 값이 올바르지 않습니다. | `400 Bad Request` |
| board | 게시글 제목 누락 | `POST_TITLE_REQUIRED` | 게시글 제목은 필수입니다. | `400 Bad Request` |
| board | 게시글 제목 길이 초과 | `POST_TITLE_TOO_LONG` | 게시글 제목은 최대 100자까지 허용됩니다. | `400 Bad Request` |
| member | 회원 이메일 형식 오류 | `INVALID_EMAIL` | 이메일 형식이 올바르지 않습니다. | `400 Bad Request` |
| member | 비밀번호 누락 | `PASSWORD_REQUIRED` | 비밀번호는 필수입니다. | `400 Bad Request` |
| member | 비밀번호 길이 부족 | `PASSWORD_TOO_SHORT` | 비밀번호는 최소 8자 이상이어야 합니다. | `400 Bad Request` |
| member | 중복 이메일 | `DUPLICATE_EMAIL` | 이미 가입된 이메일입니다. | `409 Conflict` |
| member | 로그인 실패 | `INVALID_CREDENTIALS` | 이메일 또는 비밀번호가 올바르지 않습니다. | `401 Unauthorized` |
| member | 인증 필요 | `UNAUTHORIZED` | 로그인이 필요합니다. | `401 Unauthorized` |
| member | accessToken 오류 | `INVALID_ACCESS_TOKEN` | 유효하지 않은 accessToken입니다. | `401 Unauthorized` |
| member | refreshToken 오류 | `INVALID_REFRESH_TOKEN` | 유효하지 않은 refreshToken입니다. | `401 Unauthorized` |
| global | 예상하지 못한 서버 오류 | `INTERNAL_SERVER_ERROR` | 예상하지 못한 서버 오류가 발생했습니다. | `500 Internal Server Error` |

## 표준 실패 응답

- 실패 응답은 `success=false`, `code=<ERROR_CODE>`, `message=<한글 설명>`, `data=null`, `path`, `timestamp`, `errors`를 포함한다.
- 세부 필드 오류가 필요한 경우 `errors` 배열에 필드명, 필드별 에러 코드, 한글 메시지를 담는다.
- 대표 에러 코드는 항상 `code`에 유지한다.
