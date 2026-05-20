# 010 게시글 목록 페이징 조회 인수 조건

## 문서 인수 조건

- 게시글 목록 조회가 페이지 단위로 동작한다고 명시되어 있다.
- 목록 정렬 기준이 생성 시각 기준 최신순이라고 명시되어 있다.
- 목록 항목 응답에는 `content`가 포함되지 않는다고 명시되어 있다.
- 잘못된 페이지 번호와 페이지 크기 요청이 `400 Bad Request`, `INVALID_REQUEST`로 문서화되어 있다.

## 기능 인수 조건

- `GET /posts?page=0&size=10`을 호출하면 `200 OK`와 표준 성공 응답을 반환한다.
- 응답 `data.posts`는 최신 게시글이 먼저 오도록 정렬된다.
- 각 목록 항목에는 id, title, authorMemberId, author, createdAt이 포함된다.
- 각 목록 항목에는 content가 포함되지 않는다.
- 작성자 닉네임 조회는 게시글별 개별 조회가 아니라 배치 조회로 처리된다.
- 게시글이 없으면 빈 `posts` 목록과 페이징 메타데이터를 반환한다.
- `page`가 0 미만이면 `400 Bad Request`, `code=INVALID_REQUEST`를 반환한다.
- `size`가 1 미만 또는 100 초과이면 `400 Bad Request`, `code=INVALID_REQUEST`를 반환한다.
