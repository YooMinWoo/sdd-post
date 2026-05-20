# 009 게시글 상세 조회 인수 조건

## 문서 완료 조건

- `GET /posts/{postId}` API가 문서화되어 있다.
- 응답에 게시글 id, 제목, 본문, 작성자 회원 id, 작성자 현재 닉네임, 생성 시각을 포함한다고 명시되어 있다.
- 게시글 없음 응답은 `404 Not Found`, `code=POST_NOT_FOUND`, 한글 메시지로 문서화되어 있다.
- 조회 API는 공개 API이며 인증을 요구하지 않는다고 명시되어 있다.
- 목록 조회, 검색, 조회수 증가는 비범위로 명시되어 있다.

## 후속 구현 완료 조건

- 존재하는 게시글 id로 `GET /posts/{postId}`를 호출하면 `200 OK`와 표준 성공 응답을 반환한다.
- 응답의 `author`는 저장 당시 닉네임이 아니라 회원 현재 닉네임이다.
- 존재하지 않는 게시글 id로 호출하면 `404 Not Found`, `code=POST_NOT_FOUND`, `message=게시글을 찾을 수 없습니다.`를 반환한다.
- 잘못된 게시글 id로 호출하면 `400 Bad Request`, `code=INVALID_REQUEST`를 반환한다.
- 가능한 경우 `.\gradlew.bat test`가 성공한다.
