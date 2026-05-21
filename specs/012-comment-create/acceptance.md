# 012 댓글 작성 인수 조건

## 문서 완료 조건

- `POST /posts/{postId}/comments` API가 문서화되어 있다.
- 댓글 작성 API가 인증 API라고 명시되어 있다.
- 댓글 작성자는 인증 회원 id로 식별한다고 명시되어 있다.
- 댓글 본문 필수, trim, 최대 1,000자 규칙이 명시되어 있다.
- 댓글 작성 성공 응답이 `201 Created`이고 댓글 id만 반환한다고 명시되어 있다.
- 게시글 없음과 이미 삭제된 게시글 응답이 `404 Not Found`, `code=POST_NOT_FOUND`로 문서화되어 있다.
- 댓글 본문 누락 또는 빈 값 응답이 `400 Bad Request`, `code=COMMENT_CONTENT_REQUIRED`, 한글 메시지로 문서화되어 있다.
- 댓글 본문 길이 초과 응답이 `400 Bad Request`, `code=COMMENT_CONTENT_TOO_LONG`, 한글 메시지로 문서화되어 있다.
- 댓글 조회, 수정, 삭제, 대댓글이 비범위라고 명시되어 있다.

## 기능 인수 조건

- 인증 회원이 유효한 accessToken으로 `POST /posts/{postId}/comments`를 호출하면 `201 Created`를 반환한다.
- 댓글 작성 성공 응답은 `success=true`, `message=댓글이 생성되었습니다.`, `data.id`를 포함한다.
- 저장된 댓글은 게시글 id, 작성자 회원 id, trim된 본문, 생성 시각을 가진다.
- 토큰 없이 댓글 작성 API를 호출하면 `401 Unauthorized`, `code=UNAUTHORIZED`를 반환한다.
- 잘못된 accessToken으로 댓글 작성 API를 호출하면 `401 Unauthorized`, `code=INVALID_ACCESS_TOKEN`을 반환한다.
- 존재하지 않는 게시글 id로 댓글 작성 API를 호출하면 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 이미 삭제된 게시글 id로 댓글 작성 API를 호출하면 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 잘못된 게시글 id로 댓글 작성 API를 호출하면 `400 Bad Request`, `code=INVALID_REQUEST`를 반환한다.
- 댓글 본문이 누락되거나 trim 후 빈 문자열이면 `400 Bad Request`, `code=COMMENT_CONTENT_REQUIRED`, `message=댓글 본문은 필수입니다.`를 반환한다.
- 댓글 본문이 1,000자를 초과하면 `400 Bad Request`, `code=COMMENT_CONTENT_TOO_LONG`, `message=댓글 본문은 최대 1,000자까지 허용됩니다.`를 반환한다.
- 가능한 경우 `.\gradlew.bat test`가 성공한다.
