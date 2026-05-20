# 006 표준 API 응답과 Swagger 정리 인수 조건

- 게시글 작성, 회원가입, 로그인, 토큰 재발급 성공 응답은 `success=true`, 한글 `message`, `data`를 포함하고 `code`는 포함하지 않는다.
- 최초 구현의 실패 응답은 `success=false`, 에러 코드 `message`, 요청 `path`, `timestamp`, `errors`를 포함하고 `code`는 포함하지 않는다.
- 한글 에러 응답 확장 후 실패 응답은 `success=false`, 영문 `code`, 한글 `message`, 요청 `path`, `timestamp`, `errors`를 포함한다.
- 로그아웃은 `204 No Content`와 빈 본문을 반환한다.
- 컨트롤러에는 긴 JSON 예시 기반 Swagger 응답 선언이나 직접 작성한 `@Operation`이 남아 있지 않다.
- Swagger 응답 문서는 API별 문서 어노테이션으로 선언된다.
- 전체 테스트가 통과한다.
