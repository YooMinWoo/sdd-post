# 002 API 에러 처리와 Swagger 문서화 명세

## 목표

API 에러 응답을 전역에서 일관되게 처리하고, Swagger 문서를 한글로 상세하게 제공한다.

## 사용자 이야기

API 사용자는 실패 응답의 구조와 의미를 일관되게 이해하고, Swagger 문서에서 요청, 응답, 에러 예시를 한글로 확인하고 싶다.

## 범위

- Web 계층의 예외 처리를 `@RestControllerAdvice` 기반 전역 처리로 통합한다.
- API 에러 응답을 구조화한다.
- 게시글 작성 API의 Swagger 설명, 요청/응답 스키마, 예시, 에러 응답을 한글로 문서화한다.
- SDD 문서와 공통 문서의 작성 언어를 한글로 정한다.

## 비범위

- Bean Validation 기반 필드 검증 도입
- 인증/인가 에러 정책
- 다국어 문서 전환
- 외부 배포 환경별 Swagger 접근 제어

## 에러 응답 규칙

- 에러 응답은 `code`, `message`, `path`, `timestamp`, `errors`를 포함한다.
- `code`는 클라이언트가 판별하기 쉬운 영문 대문자 스네이크 케이스를 사용한다.
- `message`는 한글 사용자 메시지를 기본으로 한다.
- 도메인 입력 오류는 `INVALID_REQUEST`와 `400 Bad Request`로 응답한다.
- JSON 파싱 실패는 `MALFORMED_JSON`과 `400 Bad Request`로 응답한다.
- 예상하지 못한 서버 오류는 `INTERNAL_SERVER_ERROR`와 `500 Internal Server Error`로 응답한다.

## Swagger 문서 규칙

- API 제목, 설명, 태그, DTO 필드 설명은 한글로 작성한다.
- 기술 식별자, HTTP 필드명, 에러 코드는 영문을 유지한다.
- 게시글 작성 API에는 성공 예시와 400 에러 예시를 포함한다.
- Swagger UI는 `/swagger-ui/index.html`, OpenAPI JSON은 `/v3/api-docs` 기본 경로를 사용한다.
