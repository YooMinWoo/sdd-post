# 004 bounded context 패키지 분리 명세

## 목표

패키지 구조를 bounded context 기준으로 분리해 게시판 기능과 회원 기능의 책임 경계를 명확히 한다.

## 사용자 이야기

개발자는 기능이 늘어나도 각 도메인 경계를 쉽게 파악하고, 관련 도메인/애플리케이션/어댑터 코드를 한 컨텍스트 안에서 관리하고 싶다.

## 범위

- 게시글 작성 기능을 `board` bounded context로 이동한다.
- 회원가입 기능을 `member` bounded context로 이동한다.
- 전역 에러 처리, Swagger 설정, 보안 설정은 `global` 패키지로 이동한다.
- 테스트 패키지도 운영 코드와 같은 bounded context 구조로 맞춘다.
- 관련 문서의 패키지 구조 설명을 갱신한다.

## 비범위

- API 경로 변경
- 도메인 규칙 변경
- DB 테이블명 변경
- 기능 추가

## 패키지 규칙

- bounded context는 `com.example.post.{context}` 바로 아래에 둔다.
- 각 bounded context 내부에는 기존 헥사고날 계층을 유지한다.
- 공통 관심사는 `com.example.post.global` 아래에 둔다.
- 컨텍스트 간 직접 참조는 최소화하고, 공통 응답/설정만 `global`에서 재사용한다.
