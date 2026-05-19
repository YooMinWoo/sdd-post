# ADR 0001: DDD and Hexagonal Architecture

## Status

Accepted

## Context

이 프로젝트는 Spring Boot 기반 게시판 애플리케이션이다. 게시글, 댓글, 작성자, 권한, 검색 등 기능이 확장될 수 있으므로 기능별 요구사항과 도메인 규칙을 명확히 분리해서 관리해야 한다.

프레임워크 중심 구조만 사용하면 도메인 규칙이 Controller, Service, Entity에 흩어질 위험이 있다. 또한 DB, Web, Security 같은 기술 선택이 도메인 설계에 직접 영향을 주기 쉽다.

## Decision

프로젝트의 기본 아키텍처 스타일로 DDD와 헥사고날 아키텍처를 채택한다.

- 도메인 규칙은 domain 계층에 둔다.
- 유스케이스는 application 계층에서 Inbound Port로 표현한다.
- 저장소와 외부 시스템 의존성은 Outbound Port로 표현한다.
- Web, Persistence, 외부 API 연동은 adapter 계층에서 구현한다.
- 기능 구현 전 SDD 문서를 작성해 요구사항, 설계, 작업, 인수 조건을 먼저 확정한다.

## Consequences

장점:

- 도메인 규칙을 테스트하기 쉬워진다.
- 기술 구현 변경이 도메인에 미치는 영향을 줄일 수 있다.
- 기능별 설계 근거가 `specs/` 문서에 남는다.
- 장기적으로 기능 확장과 리팩터링 비용을 낮출 수 있다.

비용:

- 단순 CRUD 기능에도 초기 문서와 계층 분리가 필요하다.
- 작은 기능에서는 파일 수가 늘어날 수 있다.
- 팀원이 포트/어댑터와 DDD 용어에 익숙해야 한다.

## Related Documents

- [../ARCHITECTURE.md](../ARCHITECTURE.md)
- [../CONVENTIONS.md](../CONVENTIONS.md)
- [../TESTING.md](../TESTING.md)
- [../DOMAIN_GLOSSARY.md](../DOMAIN_GLOSSARY.md)

