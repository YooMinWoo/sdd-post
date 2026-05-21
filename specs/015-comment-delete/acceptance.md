# 015 댓글 삭제 인수 조건

## 문서 완료 조건

- `DELETE /posts/{postId}/comments/{commentId}` API가 문서화되어 있다.
- 댓글 삭제 API가 인증 API라고 명시되어 있다.
- 댓글 작성자 본인만 삭제할 수 있다고 명시되어 있다.
- 댓글 삭제 방식이 물리 삭제라고 명시되어 있다.
- 성공 응답이 `204 No Content`라고 명시되어 있다.
- 게시글 없음, 댓글 없음, 권한 없음, 잘못된 요청 응답이 문서화되어 있다.

## 기능 인수 조건

- 댓글 작성자가 유효한 accessToken으로 삭제 API를 호출하면 `204 No Content`를 반환한다.
- 삭제 성공 응답은 본문을 포함하지 않는다.
- 삭제된 댓글은 댓글 목록 조회와 댓글 수 집계에 포함되지 않는다.
- 토큰 없이 삭제 API를 호출하면 `401 Unauthorized`, `code=UNAUTHORIZED`를 반환한다.
- 존재하지 않거나 삭제된 게시글의 댓글 삭제 요청은 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 존재하지 않는 댓글 또는 요청 게시글에 속하지 않는 댓글 삭제 요청은 `404 Not Found`, `code=COMMENT_NOT_FOUND`를 반환한다.
- 댓글 작성자가 아닌 회원의 삭제 요청은 `403 Forbidden`, `code=COMMENT_DELETE_FORBIDDEN`을 반환한다.
- 잘못된 게시글 id, 댓글 id, 요청자 회원 id이면 `400 Bad Request`, `code=INVALID_REQUEST`를 반환한다.
- 가능한 경우 `.\gradlew.bat test`가 성공한다.
