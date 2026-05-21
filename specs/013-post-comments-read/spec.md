# 013 게시글 댓글 조회 및 댓글 수 명세

## 목표

게시글 상세와 목록에서 댓글 수를 확인하고, 게시글의 댓글은 별도 API로 페이지 단위 조회할 수 있게 한다.

## 사용자 이야기

게시판 사용자는 게시글 상세 화면에서 본문과 댓글 수를 먼저 확인한 뒤, 댓글 목록은 독립적으로 더 불러오고 싶다.

게시판 사용자는 목록 화면에서 게시글마다 댓글이 몇 개인지 확인해 참여가 많은 게시글을 구분하고 싶다.

## 범위

- 게시글 상세 조회 응답에 `commentCount`를 포함한다.
- 게시글 목록 조회 응답의 각 게시글 항목에 `commentCount`를 포함한다.
- 게시글 댓글 목록을 별도 API로 페이지 단위 조회한다.
- 댓글 목록은 게시글 id로 조회하며, 댓글 생성 시각 기준 최신순으로 정렬한다.
- 댓글 항목에는 댓글 id, 작성자 회원 id, 작성자 현재 닉네임, 본문, 생성 시각을 포함한다.
- 댓글 목록 페이지는 `page`, `size` 요청 파라미터로 제어한다.
- 댓글이 없는 게시글은 빈 댓글 목록과 댓글 페이징 메타데이터를 반환한다.
- 현재 댓글 삭제 기능이 없으므로 `commentCount`는 해당 게시글에 작성된 전체 댓글 수로 정의한다.
- 조회 대상 게시글이 없거나 이미 삭제된 게시글이면 기존처럼 `404 Not Found`와 한글 에러 메시지를 반환한다.

## 비범위

- 게시글 상세 조회 응답에 댓글 목록 포함
- 댓글 작성, 수정, 삭제
- 대댓글 조회
- 댓글 검색
- 댓글 최신순 외 정렬 옵션
- 댓글 작성자 닉네임 스냅샷 제공
- 댓글 수 캐시 또는 비정규화 컬럼 추가
- 댓글 수 기준 게시글 정렬
- 댓글 신고, 좋아요, 첨부파일 포함 조회

## 도메인 규칙

- 게시글 id는 양수여야 한다.
- 댓글 목록 `page`는 0 이상이어야 한다.
- 댓글 목록 `size`는 1 이상 100 이하여야 한다.
- 댓글은 생성 시각 기준 최신순으로 정렬한다.
- 댓글 작성자 닉네임은 댓글에 저장된 `authorMemberId`로 회원 정보를 조회해 제공한다.
- 댓글 작성자 닉네임은 조회 시점의 회원 현재 닉네임을 사용한다.
- 댓글 작성자 닉네임 조회는 댓글별 개별 조회가 아니라 배치 조회로 처리한다.
- 게시글 상세과 목록의 댓글 수 조회는 게시글별 개별 조회가 아니라 배치 조회로 처리한다.
- 조회 대상 게시글이 없거나 이미 삭제된 게시글이면 댓글 목록 조회를 수행하지 않는다.

## API

### 게시글 상세 조회

```http
GET /posts/{postId}
```

성공 응답:

```http
200 OK
```

```json
{
  "success": true,
  "message": "게시글을 조회했습니다.",
  "data": {
    "id": 1,
    "title": "안녕하세요",
    "content": "첫 번째 게시글입니다.",
    "authorMemberId": 1,
    "author": "minu",
    "createdAt": "2026-05-20T00:00:00Z",
    "commentCount": 2
  },
  "path": null,
  "timestamp": "2026-05-21T00:00:00Z",
  "errors": []
}
```

### 게시글 댓글 목록 조회

```http
GET /posts/{postId}/comments?page=0&size=10
```

성공 응답:

```http
200 OK
```

```json
{
  "success": true,
  "message": "댓글 목록을 조회했습니다.",
  "data": {
    "items": [
      {
        "id": 2,
        "authorMemberId": 3,
        "author": "jane",
        "content": "두 번째 댓글입니다.",
        "createdAt": "2026-05-21T02:00:00Z"
      },
      {
        "id": 1,
        "authorMemberId": 2,
        "author": "kim",
        "content": "첫 번째 댓글입니다.",
        "createdAt": "2026-05-21T01:00:00Z"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 2,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "path": null,
  "timestamp": "2026-05-21T00:00:00Z",
  "errors": []
}
```

댓글이 없는 게시글 응답:

```json
{
  "success": true,
  "message": "댓글 목록을 조회했습니다.",
  "data": {
    "items": [],
    "page": 0,
    "size": 10,
    "totalElements": 0,
    "totalPages": 0,
    "first": true,
    "last": true
  },
  "path": null,
  "timestamp": "2026-05-21T00:00:00Z",
  "errors": []
}
```

게시글 없음 또는 이미 삭제된 게시글 응답:

```http
404 Not Found
```

```json
{
  "success": false,
  "code": "POST_NOT_FOUND",
  "message": "게시글을 찾을 수 없습니다.",
  "data": null,
  "path": "/posts/999/comments",
  "timestamp": "2026-05-21T00:00:00Z",
  "errors": []
}
```

잘못된 게시글 id 또는 댓글 페이징 요청:

```http
400 Bad Request
```

```json
{
  "success": false,
  "code": "INVALID_REQUEST",
  "message": "요청 값이 올바르지 않습니다.",
  "data": null,
  "path": "/posts/1/comments",
  "timestamp": "2026-05-21T00:00:00Z",
  "errors": []
}
```

### 게시글 목록 조회

```http
GET /posts?page=0&size=10
```

성공 응답:

```http
200 OK
```

```json
{
  "success": true,
  "message": "게시글 목록을 조회했습니다.",
  "data": {
    "posts": [
      {
        "id": 2,
        "title": "두 번째 게시글",
        "authorMemberId": 1,
        "author": "minu",
        "createdAt": "2026-05-20T01:00:00Z",
        "commentCount": 3
      },
      {
        "id": 1,
        "title": "첫 번째 게시글",
        "authorMemberId": 1,
        "author": "minu",
        "createdAt": "2026-05-20T00:00:00Z",
        "commentCount": 0
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 2,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "path": null,
  "timestamp": "2026-05-21T00:00:00Z",
  "errors": []
}
```
