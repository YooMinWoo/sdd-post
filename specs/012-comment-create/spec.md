# 012 댓글 작성 명세

## 목표

로그인한 회원이 삭제되지 않은 게시글에 댓글을 작성할 수 있게 한다.

## 사용자 이야기

게시판 사용자는 게시글을 읽은 뒤 자신의 의견을 댓글로 남기고 싶다.

## 범위

- 게시글 id로 대상 게시글을 지정해 댓글 1건을 작성한다.
- 댓글 작성 API는 유효한 accessToken이 필요한 인증 API로 둔다.
- 댓글 작성자는 인증된 회원 id로 식별한다.
- 댓글 본문을 저장 전 검증한다.
- 삭제되지 않은 게시글에만 댓글 작성을 허용한다.
- 댓글 작성 성공 시 생성된 댓글 id만 응답한다.
- 대상 게시글이 없거나 이미 삭제된 게시글이면 `404 Not Found`와 한글 에러 메시지를 반환한다.

## 비범위

- 댓글 목록 조회
- 게시글 상세 조회 응답에 댓글 포함
- 댓글 수정
- 댓글 삭제
- 대댓글
- 댓글 작성자 닉네임 스냅샷 저장
- 댓글 좋아요, 신고, 첨부파일
- 관리자 권한 댓글 관리
- 댓글 수 집계와 게시글 목록/상세 응답 반영

## 도메인 규칙

- 게시글 id는 양수여야 한다.
- 댓글 작성 요청자는 인증된 회원이어야 한다.
- 댓글 작성자 회원 id는 인증 정보의 회원 id를 사용한다.
- 댓글 본문은 필수다.
- 댓글 본문은 검증 전에 앞뒤 공백을 제거한다.
- 댓글 본문은 trim 후 빈 문자열일 수 없다.
- 댓글 본문은 최대 1,000자까지 허용한다.
- 댓글은 생성 시각을 가진다.
- 삭제된 게시글은 댓글 작성 가능한 대상 게시글로 보지 않는다.
- 댓글 작성 시 작성자 닉네임 조회는 수행하지 않는다.

## API

```http
POST /posts/{postId}/comments
Authorization: Bearer access.jwt.token
Content-Type: application/json
```

요청:

```json
{
  "content": "좋은 글입니다."
}
```

성공 응답:

```http
201 Created
```

```json
{
  "success": true,
  "message": "댓글이 생성되었습니다.",
  "data": {
    "id": 1
  },
  "path": null,
  "timestamp": "2026-05-21T00:00:00Z",
  "errors": []
}
```

인증 실패 응답:

```http
401 Unauthorized
```

```json
{
  "success": false,
  "code": "UNAUTHORIZED",
  "message": "로그인이 필요합니다.",
  "data": null,
  "path": "/posts/1/comments",
  "timestamp": "2026-05-21T00:00:00Z",
  "errors": []
}
```

잘못된 accessToken 응답:

```http
401 Unauthorized
```

```json
{
  "success": false,
  "code": "INVALID_ACCESS_TOKEN",
  "message": "유효하지 않은 accessToken입니다.",
  "data": null,
  "path": "/posts/1/comments",
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

잘못된 게시글 id 응답:

```http
400 Bad Request
```

```json
{
  "success": false,
  "code": "INVALID_REQUEST",
  "message": "요청 값이 올바르지 않습니다.",
  "data": null,
  "path": "/posts/0/comments",
  "timestamp": "2026-05-21T00:00:00Z",
  "errors": []
}
```

댓글 본문 누락 또는 빈 값 응답:

```http
400 Bad Request
```

```json
{
  "success": false,
  "code": "COMMENT_CONTENT_REQUIRED",
  "message": "댓글 본문은 필수입니다.",
  "data": null,
  "path": "/posts/1/comments",
  "timestamp": "2026-05-21T00:00:00Z",
  "errors": []
}
```

댓글 본문 길이 초과 응답:

```http
400 Bad Request
```

```json
{
  "success": false,
  "code": "COMMENT_CONTENT_TOO_LONG",
  "message": "댓글 본문은 최대 1,000자까지 허용됩니다.",
  "data": null,
  "path": "/posts/1/comments",
  "timestamp": "2026-05-21T00:00:00Z",
  "errors": []
}
```
