# 013 게시글 댓글 조회 및 댓글 수 인수 조건

## 문서 완료 조건

- `GET /posts/{postId}` 응답의 `commentCount`가 문서화되어 있다.
- `GET /posts/{postId}/comments?page=0&size=10` API가 문서화되어 있다.
- 댓글 목록 응답의 `items`, `page`, `size`, `totalElements`, `totalPages`, `first`, `last` 구조가 문서화되어 있다.
- 댓글 항목의 `id`, `authorMemberId`, `author`, `content`, `createdAt` 필드가 문서화되어 있다.
- 댓글 정렬이 생성 시각 기준 최신순이라고 명시되어 있다.
- 댓글 페이징 규칙이 `page >= 0`, `1 <= size <= 100`으로 명시되어 있다.
- 게시글 목록 응답의 `posts[].commentCount`가 문서화되어 있다.
- 댓글이 없는 게시글은 빈 댓글 목록과 `totalElements=0`을 반환한다고 명시되어 있다.
- 게시글이 없거나 삭제된 게시글이면 댓글 목록 조회를 수행하지 않고 `POST_NOT_FOUND`를 반환한다고 명시되어 있다.

## 기능 인수 조건

- 게시글 상세 조회 성공 응답은 게시글 기본 정보와 `commentCount`를 포함한다.
- 게시글 상세 조회 성공 응답은 댓글 목록을 포함하지 않는다.
- 게시글 댓글 목록 조회 성공 응답은 댓글 페이지를 포함한다.
- 댓글 목록은 생성 시각 기준 최신순으로 반환된다.
- 댓글 항목은 댓글 id, 작성자 회원 id, 작성자 현재 닉네임, 본문, 생성 시각을 포함한다.
- 댓글이 없는 게시글의 댓글 목록을 조회하면 `items=[]`, `totalElements=0`을 반환한다.
- 댓글 목록 `page`가 0보다 작으면 `400 Bad Request`, `code=INVALID_REQUEST`를 반환한다.
- 댓글 목록 `size`가 1보다 작거나 100보다 크면 `400 Bad Request`, `code=INVALID_REQUEST`를 반환한다.
- 존재하지 않는 게시글 id로 댓글 목록을 조회하면 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 이미 삭제된 게시글 id로 댓글 목록을 조회하면 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 게시글 목록 조회 성공 응답의 각 게시글 항목은 `commentCount`를 포함한다.
- 댓글이 없는 게시글의 `commentCount`는 0이다.
- 가능한 경우 `.\gradlew.bat test`가 성공한다.
