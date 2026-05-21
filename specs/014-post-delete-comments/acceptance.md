# 014 게시글 삭제 시 댓글 삭제 인수 조건

## 문서 완료 조건

- 게시글 삭제 성공 시 해당 게시글 댓글도 삭제된다고 명시되어 있다.
- 댓글 삭제 방식이 물리 삭제라고 명시되어 있다.
- 게시글 삭제와 댓글 삭제가 같은 트랜잭션 안에서 처리된다고 명시되어 있다.
- 게시글 삭제 API 응답 계약이 기존 `204 No Content`로 유지된다고 명시되어 있다.
- 삭제 실패 경로에서는 댓글 삭제를 수행하지 않는다고 명시되어 있다.

## 기능 인수 조건

- 작성자가 유효한 accessToken으로 `DELETE /posts/{postId}`를 호출하면 `204 No Content`를 반환한다.
- 삭제 성공 후 해당 게시글에 속한 댓글은 댓글 저장소에서 삭제된다.
- 삭제 성공 후 다른 게시글의 댓글은 삭제되지 않는다.
- 댓글이 없는 게시글을 삭제해도 `204 No Content`를 반환한다.
- 삭제 성공 후 `GET /posts/{postId}/comments`를 호출하면 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 존재하지 않는 게시글 id로 삭제를 요청하면 댓글 삭제를 수행하지 않고 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 이미 삭제된 게시글 id로 삭제를 요청하면 댓글 삭제를 수행하지 않고 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 작성자가 아닌 회원이 삭제를 요청하면 댓글 삭제를 수행하지 않고 `403 Forbidden`, `code=POST_DELETE_FORBIDDEN`을 반환한다.
- 잘못된 게시글 id 또는 요청자 회원 id이면 댓글 삭제를 수행하지 않고 `400 Bad Request`, `code=INVALID_REQUEST`를 반환한다.
- 가능한 경우 `.\gradlew.bat test`가 성공한다.
