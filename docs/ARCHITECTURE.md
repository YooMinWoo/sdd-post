# Architecture

이 프로젝트는 Spring Boot 기반 게시판 애플리케이션이며, DDD와 헥사고날 아키텍처를 기준으로 개발한다.

## 목표

- 도메인 규칙을 프레임워크와 분리한다.
- 유스케이스 중심으로 기능을 설계한다.
- 입출력 기술(Web, DB, Security, 외부 API)을 어댑터로 격리한다.
- 테스트 가능한 구조를 유지한다.

## 계층 방향

의존성은 바깥에서 안쪽으로만 흐른다.

```text
adapter -> application -> domain
```

도메인 계층은 애플리케이션 계층, 어댑터 계층, Spring, JPA에 의존하지 않는다.

## 권장 패키지 구조

기능이 생기기 전까지 실제 패키지를 미리 만들지 않는다. 기능 구현 시에는 `plan.md`에서 패키지 구조를 먼저 확정한다.

기본 방향:

```text
com.example.post
  domain
    {aggregate}
  application
    port
      in
      out
    service
  adapter
    in
      web
    out
      persistence
  config
```

## DDD 기준

- Aggregate: 일관성 경계를 가진 도메인 객체 묶음
- Entity: 식별자를 가지며 생명주기가 있는 도메인 객체
- Value Object: 값으로 동일성을 판단하는 불변 객체
- Domain Service: 특정 Entity나 Value Object에 자연스럽게 속하지 않는 도메인 규칙
- Repository Port: 도메인 저장소 요구사항을 표현하는 출력 포트

## 헥사고날 기준

- Inbound Adapter: HTTP Controller, CLI, 메시지 소비자 등 외부 입력을 받는 구성요소
- Inbound Port: 유스케이스를 외부에 노출하는 애플리케이션 인터페이스
- Application Service: 유스케이스 흐름과 트랜잭션 경계를 담당
- Outbound Port: 저장소, 외부 API, 메시징 등 외부 기능에 대한 요구사항
- Outbound Adapter: JPA Repository, 외부 API Client 등 실제 기술 구현

## Spring 사용 기준

- Spring Annotation은 주로 application, adapter, config 계층에서 사용한다.
- domain 계층에는 가능한 한 Spring Annotation을 두지 않는다.
- JPA Entity와 도메인 모델을 분리할지 여부는 각 기능의 `plan.md`에서 결정한다.
- 트랜잭션 경계는 Application Service에 둔다.

## 기능 추가 절차

1. `specs/{번호}-{기능명}/spec.md`로 요구사항과 도메인 규칙을 정의한다.
2. `plan.md`로 계층별 설계, 포트, 어댑터, 테스트 전략을 정의한다.
3. `tasks.md`로 구현 단위를 체크박스로 나눈다.
4. `acceptance.md`로 완료 조건을 정의한다.
5. 구현 전에 `spec.md`와 `plan.md`를 다시 검토한다.
6. 구현 후 `tasks.md`와 필요한 공통 문서를 갱신한다.

