# 017 댓글 수정 명세

## 목표

로그인한 댓글 작성자가 자신이 작성한 댓글 본문을 수정할 수 있게 한다.

## 사용자 이야기

게시판 사용자는 자신이 작성한 댓글의 오타나 내용을 수정하고 싶다.

## 범위

- 게시글 id와 댓글 id로 댓글 1건의 본문을 수정한다.
- 댓글 수정 API는 유효한 accessToken이 필요한 인증 API로 둔다.
- 로그인한 회원이 댓글 작성자인 경우에만 수정을 허용한다.
- 요청 본문은 `content`를 필수로 받는다.
- 댓글 본문 검증은 댓글 작성과 같은 규칙을 사용한다.
- 대상 게시글이 없거나 이미 삭제된 게시글이면 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 대상 댓글이 없거나 요청 게시글에 속하지 않으면 `404 Not Found`, `code=COMMENT_NOT_FOUND`를 반환한다.
- 수정 권한이 없으면 `403 Forbidden`, `code=COMMENT_UPDATE_FORBIDDEN`을 반환한다.
- 수정 성공 시 수정된 댓글 정보를 반환한다.

## 비범위

- 댓글 작성자 변경
- 댓글 수정 이력 조회
- 댓글 수정 시각 컬럼 추가
- 관리자 권한 수정
- 대댓글 수정 정책
- 첨부파일 수정

## 도메인 규칙

- 게시글 id, 댓글 id, 요청자 회원 id는 양수여야 한다.
- 댓글 수정 요청자는 인증된 회원이어야 한다.
- 수정 대상 게시글은 존재하며 삭제되지 않은 게시글이어야 한다.
- 수정 대상 댓글은 요청한 게시글에 속해야 한다.
- 댓글 작성자 회원 id와 수정 요청자 회원 id가 같아야 수정할 수 있다.
- 댓글 본문은 공백 제거 후 비어 있으면 안 되고 최대 1,000자까지 허용한다.

## API

```http
PATCH /posts/{postId}/comments/{commentId}
Authorization: Bearer access.jwt.token
Content-Type: application/json
```

```json
{
  "content": "수정된 댓글입니다."
}
```

성공 응답:

```http
200 OK
```

```json
{
  "success": true,
  "message": "댓글이 수정되었습니다.",
  "data": {
    "id": 1,
    "authorMemberId": 1,
    "author": "minu",
    "content": "수정된 댓글입니다.",
    "createdAt": "2026-05-21T00:00:00Z"
  },
  "path": null,
  "timestamp": "2026-05-22T00:00:00Z",
  "errors": []
}
```

수정 권한 없음 응답:

```http
403 Forbidden
```

```json
{
  "success": false,
  "code": "COMMENT_UPDATE_FORBIDDEN",
  "message": "댓글 수정 권한이 없습니다.",
  "data": null,
  "path": "/posts/1/comments/1",
  "timestamp": "2026-05-22T00:00:00Z",
  "errors": []
}
```
