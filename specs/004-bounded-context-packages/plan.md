# 004 bounded context 패키지 분리 계획

## 아키텍처

기존 계층 우선 패키지에서 bounded context 우선 패키지로 전환한다. 각 bounded context 내부에는 domain, application, adapter 계층을 유지한다.

## 패키지 배치

```text
com.example.post
  board
    domain
    application
    adapter
  member
    domain
    application
    adapter
  global
    config
    web
```

## 변경 전략

- 게시글 작성 관련 클래스는 `board` 아래로 이동한다.
- 회원가입 관련 클래스는 `member` 아래로 이동한다.
- `ErrorResponse`, `GlobalExceptionHandler`는 `global.web`으로 이동한다.
- `OpenApiConfig`, `SecurityConfig`는 `global.config`로 이동한다.
- import와 테스트 패키지를 새 위치에 맞게 갱신한다.

## 테스트 전략

- 리팩터링 후 `.\gradlew.bat test`로 전체 기능 회귀를 검증한다.
- API 경로와 응답 구조는 기존 테스트로 동일성을 확인한다.
