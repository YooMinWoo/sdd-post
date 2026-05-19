# 컨벤션

이 문서는 프로젝트 전반의 개발 규칙과 문서 작성 규칙을 정의한다.

## SDD 문서 규칙

모든 기능은 구현 전에 아래 파일을 먼저 작성한다.

```text
specs/{번호}-{기능명}/spec.md
specs/{번호}-{기능명}/plan.md
specs/{번호}-{기능명}/tasks.md
specs/{번호}-{기능명}/acceptance.md
```

규칙:

- `{번호}`는 세 자리 숫자를 사용한다. 예: `001`
- `{기능명}`은 영문 소문자와 하이픈을 사용한다. 예: `post-create`
- SDD 문서, 공통 문서, Swagger 설명은 한글 작성을 기본으로 한다.
- 패키지명, 클래스명, 메서드명, HTTP 필드명, 에러 코드 같은 기술 식별자는 영문을 유지한다.
- 문서는 기능 구현보다 먼저 커밋 가능한 상태여야 한다.
- 기능 범위가 바뀌면 구현 전에 `spec.md`와 `plan.md`를 먼저 갱신한다.

## Java 규칙

- Java 17 기준으로 작성한다.
- 패키지명은 소문자를 사용한다.
- 클래스명은 PascalCase를 사용한다.
- 메서드와 필드는 camelCase를 사용한다.
- 상수는 UPPER_SNAKE_CASE를 사용한다.
- 불필요한 약어를 피하고 도메인 용어를 우선한다.

## 계층별 명명

- Bounded Context Package: `com.example.post.{bounded-context}`
- Inbound Port: `{UseCase}UseCase`
- Outbound Port: `{Domain}RepositoryPort`, `{ExternalSystem}Port`
- Application Service: `{UseCase}Service`
- Web Controller: `{Resource}Controller`
- Request DTO: `{Action}{Resource}Request`
- Response DTO: `{Action}{Resource}Response`
- Persistence Adapter: `{Domain}PersistenceAdapter`

예시:

```text
com.example.post.board
com.example.post.member
CreatePostUseCase
PostRepositoryPort
CreatePostService
PostController
CreatePostRequest
PostPersistenceAdapter
```

## 도메인 규칙

- 도메인 객체는 자신의 불변식 검증을 스스로 수행한다.
- 단순 데이터 운반만 하는 도메인 객체는 피한다.
- 외부 입력 검증과 도메인 불변식 검증을 구분한다.
- 도메인 예외는 의미 있는 이름이나 안정적인 메시지로 표현한다.

## 애플리케이션 규칙

- Application Service는 유스케이스 흐름을 조율한다.
- 트랜잭션 경계는 Application Service에 둔다.
- Application Service는 Controller DTO에 의존하지 않는다.
- 포트 인터페이스는 구현 기술을 드러내지 않는다.
- 다른 bounded context의 애플리케이션 서비스나 어댑터에 직접 의존하지 않는다.

## 어댑터 규칙

- Web Adapter는 HTTP 상태, 요청, 응답 변환을 담당한다.
- Persistence Adapter는 도메인 모델과 영속성 모델 간 변환을 담당한다.
- 외부 API Adapter는 호출, 타임아웃, 응답 변환과 실패 처리를 명확히 다룬다.
- 전역 예외 처리, 공통 응답, 공통 설정은 `global` 패키지에 둔다.

## 문서 갱신 규칙

- 공통 규칙이 바뀌면 `docs/CONVENTIONS.md`를 갱신한다.
- 아키텍처 방향이 바뀌면 `docs/ARCHITECTURE.md`와 ADR을 갱신한다.
- 도메인 용어가 추가되면 `docs/DOMAIN_GLOSSARY.md`를 갱신한다.
- 새 문서가 추가되면 `docs/INDEX.md`를 갱신한다.

## 커밋 메시지 규칙

- 작업 완료 보고에는 변경 범위에 맞는 추천 `git commit` 메시지를 1개 제시한다.
- 커밋 메시지는 Conventional Commits 형식을 따른다.
- 형식은 `<type>: <한글 요약>`을 사용한다.
- 권장 타입은 `feat`, `fix`, `docs`, `test`, `refactor`, `chore`다.
- 예시: `feat: 전역 에러 처리와 Swagger 문서화 추가`
- 커밋은 추천만 하며, 사용자가 요청하기 전에는 자동으로 실행하지 않는다.
