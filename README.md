# SDD Post

SDD 기반으로 설계하고 에이전틱 코딩 흐름으로 확장하는 Spring Boot 게시판 API 프로젝트입니다.

## 프로젝트 소개

이 프로젝트는 게시글과 회원 인증 기능을 중심으로 한 백엔드 API 예제입니다. 기능을 바로 구현하기보다 먼저 명세를 작성하고, 그 명세를 기준으로 도메인 모델과 애플리케이션 흐름을 설계한 뒤 구현하는 Spec-Driven Development 방식을 따릅니다.

또한 에이전틱 코딩 환경에서 기능 명세, 구현 계획, 작업 목록, 인수 조건을 함께 관리하며 점진적으로 기능을 확장하는 구조를 실험합니다.

## 주요 특징

- Spec-Driven Development 기반 기능 개발
- DDD와 헥사고날 아키텍처 지향 패키지 구조
- 게시글 작성, 상세 조회, 목록 조회 API
- 회원가입, 로그인, 토큰 재발급, 로그아웃 기능
- JWT 기반 인증과 Spring Security 적용
- Swagger/OpenAPI 기반 API 문서화
- 도메인, 애플리케이션, 어댑터 계층별 테스트 구성

## 기술 스택

- Java 17
- Spring Boot
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Redis
- JWT
- H2 / PostgreSQL
- Gradle
- Swagger / springdoc-openapi

## 문서 구조

```text
docs/   프로젝트 공통 아키텍처, 컨벤션, 테스트, 용어 문서
specs/  기능별 spec, plan, tasks, acceptance 문서
src/    Spring Boot 애플리케이션 소스 코드
```

각 기능은 `specs/{번호}-{기능명}/` 아래에 명세와 구현 계획을 먼저 정리한 뒤 개발하는 것을 기본 원칙으로 합니다.
