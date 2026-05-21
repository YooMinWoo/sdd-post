# 017 댓글 수정 인수 조건

## 문서 완료 조건

- `PATCH /posts/{postId}/comments/{commentId}` API가 문서화되어 있다.
- 댓글 수정 API가 인증 API라고 명시되어 있다.
- 댓글 작성자 본인만 수정할 수 있다고 명시되어 있다.
- `content`가 필수라고 명시되어 있다.
- 성공 응답 구조가 문서화되어 있다.
- 게시글 없음, 댓글 없음, 권한 없음, 잘못된 요청 응답이 문서화되어 있다.

## 기능 인수 조건

- 댓글 작성자가 유효한 accessToken으로 수정 API를 호출하면 `200 OK`를 반환한다.
- 성공 응답은 수정된 `content`, 댓글 id, 작성자 정보, 생성 시각을 포함한다.
- 수정된 댓글을 목록 조회하면 변경된 본문이 반환된다.
- 토큰 없이 수정 API를 호출하면 `401 Unauthorized`, `code=UNAUTHORIZED`를 반환한다.
- 존재하지 않거나 삭제된 게시글의 댓글 수정 요청은 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 존재하지 않는 댓글 또는 요청 게시글에 속하지 않는 댓글 수정 요청은 `404 Not Found`, `code=COMMENT_NOT_FOUND`를 반환한다.
- 댓글 작성자가 아닌 회원의 수정 요청은 `403 Forbidden`, `code=COMMENT_UPDATE_FORBIDDEN`을 반환한다.
- 잘못된 게시글 id, 댓글 id, 요청자 회원 id이면 `400 Bad Request`, `code=INVALID_REQUEST`를 반환한다.
- 댓글 본문이 비어 있으면 `400 Bad Request`, `code=COMMENT_CONTENT_REQUIRED`를 반환한다.
- 댓글 본문이 1,000자를 초과하면 `400 Bad Request`, `code=COMMENT_CONTENT_TOO_LONG`을 반환한다.
- 가능한 경우 `.\gradlew.bat test`가 성공한다.
