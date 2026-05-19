# 002 API 에러 처리와 Swagger 문서화 인수 조건

## 성공 조건

- 게시글 작성 API에서 도메인 입력 오류가 발생하면 구조화된 `400 Bad Request` 응답을 반환한다.
- 잘못된 JSON 요청은 `MALFORMED_JSON` 코드의 `400 Bad Request` 응답을 반환한다.
- 컨트롤러 내부에 개별 `@ExceptionHandler`가 남아 있지 않다.
- Swagger UI에서 게시글 작성 API의 한글 설명, 요청 필드 설명, 성공 응답 예시, 에러 응답 예시를 확인할 수 있다.
- SDD 문서와 공통 문서 작성 규칙은 한글 문서화를 기본으로 명시한다.

## 완료 기준

- 관련 테스트가 갱신되어 통과한다.
- 가능한 경우 `.\gradlew.bat test`를 실행한다.
- 완료된 작업은 `tasks.md`에 반영한다.
