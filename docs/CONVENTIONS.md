# Conventions

이 문서는 프로젝트 전반의 개발 규칙을 정의한다.

## SDD 문서 규칙

모든 기능은 구현 전에 아래 파일을 작성한다.

```text
specs/{번호}-{기능명}/spec.md
specs/{번호}-{기능명}/plan.md
specs/{번호}-{기능명}/tasks.md
specs/{번호}-{기능명}/acceptance.md
```

규칙:

- `{번호}`는 세 자리 숫자를 사용한다. 예: `001`
- `{기능명}`은 영문 소문자와 하이픈을 사용한다. 예: `post-create`
- 문서는 기능 구현보다 먼저 커밋 가능한 상태여야 한다.
- 기능 범위가 변경되면 구현 전에 `spec.md`와 `plan.md`를 먼저 갱신한다.

## Java 규칙

- Java 17 기준으로 작성한다.
- 패키지명은 소문자를 사용한다.
- 클래스명은 PascalCase를 사용한다.
- 메서드와 필드는 camelCase를 사용한다.
- 상수는 UPPER_SNAKE_CASE를 사용한다.
- 불필요한 약어를 피하고 도메인 용어를 우선한다.

## 계층별 명명

- Inbound Port: `{UseCase}UseCase`
- Outbound Port: `{Domain}RepositoryPort`, `{ExternalSystem}Port`
- Application Service: `{UseCase}Service`
- Web Controller: `{Resource}Controller`
- Request DTO: `{Action}{Resource}Request`
- Response DTO: `{Action}{Resource}Response`
- Persistence Adapter: `{Domain}PersistenceAdapter`

예시:

```text
CreatePostUseCase
PostRepositoryPort
CreatePostService
PostController
CreatePostRequest
PostPersistenceAdapter
```

## 도메인 규칙

- 도메인 객체는 자신의 불변식 검증을 스스로 수행한다.
- 단순 데이터 운반만 하는 도메인 객체를 피한다.
- 외부 입력 검증과 도메인 불변식 검증을 구분한다.
- 도메인 예외는 의미 있는 이름으로 표현한다.

## 애플리케이션 규칙

- Application Service는 유스케이스 흐름을 조율한다.
- 트랜잭션 경계는 Application Service에 둔다.
- Application Service는 Controller DTO에 의존하지 않는다.
- 포트 인터페이스는 구현 기술을 드러내지 않는다.

## 어댑터 규칙

- Web Adapter는 HTTP 상태, 요청, 응답 변환을 담당한다.
- Persistence Adapter는 도메인 모델과 영속성 모델 간 변환을 담당한다.
- 외부 API Adapter는 장애, 타임아웃, 응답 변환을 명확히 처리한다.

## 문서 갱신 규칙

- 공통 규칙이 바뀌면 `docs/CONVENTIONS.md`를 갱신한다.
- 아키텍처 방향이 바뀌면 `docs/ARCHITECTURE.md`와 ADR을 갱신한다.
- 도메인 용어가 추가되면 `docs/DOMAIN_GLOSSARY.md`를 갱신한다.
- 새 문서가 추가되면 `docs/INDEX.md`를 갱신한다.

