# 011 게시글 삭제 인수 조건

## 문서 완료 조건

- `DELETE /posts/{postId}` API가 문서화되어 있다.
- 삭제 API가 인증 API라고 명시되어 있다.
- 작성자 본인만 삭제할 수 있다고 명시되어 있다.
- 삭제 방식이 `deletedAt`을 사용하는 소프트 삭제라고 명시되어 있다.
- 삭제 성공 응답이 `204 No Content`라고 명시되어 있다.
- 권한 없음 응답이 `403 Forbidden`, `code=POST_DELETE_FORBIDDEN`, 한글 메시지로 문서화되어 있다.
- 게시글 없음과 이미 삭제된 게시글 응답이 `404 Not Found`, `code=POST_NOT_FOUND`로 문서화되어 있다.
- 삭제된 게시글은 상세 조회와 목록 조회에서 제외된다고 명시되어 있다.

## 기능 인수 조건

- 작성자가 유효한 accessToken으로 `DELETE /posts/{postId}`를 호출하면 `204 No Content`를 반환한다.
- 삭제 성공 응답은 본문을 포함하지 않는다.
- 삭제된 게시글은 `deletedAt` 값을 가진다.
- 삭제된 게시글을 `GET /posts/{postId}`로 조회하면 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 삭제된 게시글은 `GET /posts` 목록 응답에 포함되지 않는다.
- 토큰 없이 삭제 API를 호출하면 `401 Unauthorized`, `code=UNAUTHORIZED`를 반환한다.
- 잘못된 accessToken으로 삭제 API를 호출하면 `401 Unauthorized`, `code=INVALID_ACCESS_TOKEN`을 반환한다.
- 작성자가 아닌 회원이 삭제 API를 호출하면 `403 Forbidden`, `code=POST_DELETE_FORBIDDEN`, `message=게시글 삭제 권한이 없습니다.`를 반환한다.
- 존재하지 않는 게시글 id로 삭제 API를 호출하면 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 이미 삭제된 게시글 id로 다시 삭제 API를 호출하면 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 잘못된 게시글 id로 삭제 API를 호출하면 `400 Bad Request`, `code=INVALID_REQUEST`를 반환한다.
- 가능한 경우 `.\gradlew.bat test`가 성공한다.
