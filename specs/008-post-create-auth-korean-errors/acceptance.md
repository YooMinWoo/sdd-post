# 008 게시글 작성 인증과 한글 에러 응답 인수 조건

## 문서 완료 조건

- `POST /posts`가 Bearer accessToken 기반 인증 필요 API로 문서화되어 있다.
- 게시글 작성자는 요청 본문이 아니라 인증된 회원 id로 결정한다고 명시되어 있다.
- 닉네임은 저장 작성자 식별 값으로 사용하지 않고, 생성 응답에도 포함하지 않는다고 명시되어 있다.
- 생성 성공 응답은 생성된 게시글 id만 포함한다고 명시되어 있다.
- 실패 응답은 영문 `code`와 한글 `message`를 함께 제공한다고 명시되어 있다.
- Domain Model과 JPA Entity에 Lombok 생성자/빌더를 기본 도입하지 않는다고 명시되어 있다.
- 인증 실패와 게시글 검증 실패의 HTTP 상태, 에러 코드, 한글 메시지를 표 또는 예시로 확인할 수 있다.
- 후속 구현 테스트 항목이 `tasks.md`와 이 문서에 명시되어 있다.

## 후속 구현 완료 조건

- 토큰 없이 `POST /posts`를 호출하면 `401 Unauthorized`, `code=UNAUTHORIZED`, `message=로그인이 필요합니다.`를 반환한다.
- 유효하지 않거나 만료된 accessToken으로 호출하면 `401 Unauthorized`, `code=INVALID_ACCESS_TOKEN`, `message=유효하지 않은 accessToken입니다.`를 반환한다.
- 유효한 accessToken으로 호출하면 `201 Created`를 반환하고 응답 `data`에는 생성된 게시글 id만 포함한다.
- 저장된 게시글의 `authorMemberId`는 토큰 `sub` 값이다.
- 요청 본문에 `author`나 닉네임이 포함되어도 저장 작성자 id를 결정하지 않는다.
- 조회 기능이 추가되면 작성자 표시는 저장된 `authorMemberId`로 회원 현재 닉네임을 조회해 반환한다.
- 대표 실패 응답은 모두 `success=false`, `code`, 한글 `message`, `data=null`, `path`, `timestamp`, `errors`를 포함한다.
- 가능한 경우 `.\gradlew.bat test`가 성공한다.
