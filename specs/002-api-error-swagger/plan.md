# 002 API 에러 처리와 Swagger 문서화 계획

## 아키텍처

공통 Web 정책은 `adapter.in.web`과 `config` 계층에 둔다. 도메인과 애플리케이션 계층은 Swagger와 Spring Web 예외 처리 구현에 의존하지 않는다.

## Web 에러 처리

- `GlobalExceptionHandler`를 추가해 컨트롤러별 예외 처리를 제거한다.
- `ErrorResponse`를 구조화된 공통 응답 DTO로 확장한다.
- `IllegalArgumentException`, `HttpMessageNotReadableException`, 기타 `Exception`을 각각 명확한 HTTP 상태와 에러 코드로 변환한다.

## Swagger

- `springdoc-openapi-starter-webmvc-ui`를 추가한다.
- `OpenApiConfig`에서 API 제목, 설명, 버전, 서버, 태그를 한글로 정의한다.
- `PostController`, 요청 DTO, 응답 DTO, 에러 DTO에 Swagger 어노테이션을 추가한다.
- 게시글 작성 API는 성공 응답과 잘못된 요청 응답 예시를 제공한다.

## 문서

- SDD 문서는 한글로 작성한다.
- 기존 게시글 작성 명세를 한글로 번역한다.
- 공통 문서 색인과 컨벤션 문서를 UTF-8 한글로 정리한다.

## 테스트 전략

- Web 어댑터 테스트에서 전역 예외 핸들러가 구조화된 400 응답을 반환하는지 검증한다.
- JSON 파싱 실패 응답을 검증한다.
- `.\gradlew.bat test`로 전체 테스트를 실행한다.
