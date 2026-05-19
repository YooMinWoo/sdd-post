# Testing

이 문서는 테스트 전략과 실행 기준을 정의한다.

## 기본 원칙

- 테스트는 도메인 규칙과 유스케이스를 우선 검증한다.
- 테스트 범위는 변경 위험과 계층 책임에 맞춘다.
- 외부 의존성은 가능한 한 포트 단위로 대체한다.
- 테스트 이름은 검증하려는 동작을 드러내야 한다.

## 테스트 계층

### Domain Test

- 도메인 객체의 불변식과 상태 전이를 검증한다.
- Spring Context 없이 실행한다.
- 가장 빠르고 자주 작성해야 하는 테스트다.

### Application Test

- 유스케이스 흐름을 검증한다.
- Outbound Port는 Fake 또는 Mock으로 대체한다.
- 트랜잭션, 권한, 예외 흐름을 필요에 따라 검증한다.

### Adapter Test

- Web Adapter는 요청 검증, 응답 코드, 응답 본문을 검증한다.
- Persistence Adapter는 매핑과 쿼리를 검증한다.
- 외부 API Adapter는 응답 변환과 실패 처리를 검증한다.

### End-to-End 또는 Integration Test

- 여러 계층의 연결을 검증해야 할 때 제한적으로 작성한다.
- 비용이 높은 테스트이므로 핵심 사용자 흐름 위주로 둔다.

## 실행 명령

Windows 환경 기준:

```powershell
.\gradlew.bat test
```

필요한 경우 컴파일만 먼저 확인한다.

```powershell
.\gradlew.bat compileJava
```

## 기능별 테스트 계획

각 기능의 `specs/{번호}-{기능명}/plan.md`에는 다음을 포함한다.

- 도메인 테스트 대상
- 애플리케이션 테스트 대상
- 어댑터 테스트 대상
- 생략하는 테스트와 그 이유

## 완료 기준

- 새 도메인 규칙에는 Domain Test가 있어야 한다.
- 새 유스케이스에는 Application Test가 있어야 한다.
- 새 HTTP API에는 Web Adapter Test가 있어야 한다.
- 구현 후 가능한 경우 `.\gradlew.bat test`가 통과해야 한다.
- 테스트를 실행하지 못한 경우 이유를 작업 결과에 명시한다.

