# 007 전역 예외 처리 정책 인수 조건

- 신규 도메인/애플리케이션 검증 실패는 `IllegalArgumentException`이 아닌 명시적 예외 또는 안정적인 에러 코드로 표현하도록 문서화되어 있다.
- 전역 에러 응답은 `success=false`, `code=<ERROR_CODE>`, `message=<한글 설명>`, `data=null`, `path`, `timestamp`, `errors` 구조를 유지하도록 명시되어 있다.
- 예외별 HTTP 상태와 에러 코드를 표로 확인할 수 있다.
- 컨트롤러는 예외를 직접 처리하지 않고 전역 예외 핸들러가 응답 변환을 담당한다고 명시되어 있다.
- `IllegalArgumentException`은 신규 기능의 기본 예외가 아니라 fallback 또는 기존 호환 처리로만 남긴다고 명시되어 있다.
