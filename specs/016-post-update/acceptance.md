# 016 게시글 수정 인수 조건

## 문서 완료 조건

- `PATCH /posts/{postId}` API가 문서화되어 있다.
- 게시글 수정 API가 인증 API라고 명시되어 있다.
- 게시글 작성자 본인만 수정할 수 있다고 명시되어 있다.
- `title`, `content`가 모두 필수라고 명시되어 있다.
- 성공 응답 구조가 문서화되어 있다.
- 게시글 없음, 권한 없음, 잘못된 요청 응답이 문서화되어 있다.

## 기능 인수 조건

- 게시글 작성자가 유효한 accessToken으로 수정 API를 호출하면 `200 OK`를 반환한다.
- 성공 응답은 수정된 `title`, `content`, 작성자 정보, 생성 시각, 댓글 수를 포함한다.
- 수정된 게시글을 다시 조회하면 변경된 제목과 본문이 반환된다.
- 토큰 없이 수정 API를 호출하면 `401 Unauthorized`, `code=UNAUTHORIZED`를 반환한다.
- 존재하지 않거나 삭제된 게시글의 수정 요청은 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 게시글 작성자가 아닌 회원의 수정 요청은 `403 Forbidden`, `code=POST_UPDATE_FORBIDDEN`을 반환한다.
- 잘못된 게시글 id 또는 요청자 회원 id이면 `400 Bad Request`, `code=INVALID_REQUEST`를 반환한다.
- 제목이 비어 있으면 `400 Bad Request`, `code=POST_TITLE_REQUIRED`를 반환한다.
- 제목이 100자를 초과하면 `400 Bad Request`, `code=POST_TITLE_TOO_LONG`을 반환한다.
- 본문이 비어 있거나 5,000자를 초과하면 `400 Bad Request`, `code=INVALID_REQUEST`를 반환한다.
- 가능한 경우 `.\gradlew.bat test`가 성공한다.
