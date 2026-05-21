# 015 댓글 삭제 명세

## 목표

로그인한 댓글 작성자가 자신이 작성한 댓글을 삭제할 수 있게 한다.

## 사용자 이야기

게시판 사용자는 자신이 작성한 댓글이 더 이상 노출되지 않도록 삭제하고 싶다.

## 범위

- 게시글 id와 댓글 id로 댓글 1건을 삭제한다.
- 댓글 삭제 API는 유효한 accessToken이 필요한 인증 API로 둔다.
- 로그인한 회원이 댓글 작성자인 경우에만 삭제를 허용한다.
- 댓글은 물리 삭제로 처리한다.
- 삭제 성공 시 응답 본문 없이 `204 No Content`를 반환한다.
- 대상 게시글이 없거나 이미 삭제된 게시글이면 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 대상 댓글이 없거나 요청 게시글에 속하지 않으면 `404 Not Found`, `code=COMMENT_NOT_FOUND`를 반환한다.
- 삭제 권한이 없으면 `403 Forbidden`, `code=COMMENT_DELETE_FORBIDDEN`을 반환한다.

## 비범위

- 댓글 소프트 삭제
- 댓글 복구
- 댓글 삭제 이력 조회
- 관리자 권한 삭제
- 댓글 삭제 사유 입력
- 대댓글 삭제 정책

## 도메인 규칙

- 게시글 id, 댓글 id, 요청자 회원 id는 양수여야 한다.
- 댓글 삭제 요청자는 인증된 회원이어야 한다.
- 삭제 대상 게시글은 존재하며 삭제되지 않은 게시글이어야 한다.
- 삭제 대상 댓글은 요청한 게시글에 속해야 한다.
- 댓글 작성자 회원 id와 삭제 요청자 회원 id가 같아야 삭제할 수 있다.

## API

```http
DELETE /posts/{postId}/comments/{commentId}
Authorization: Bearer access.jwt.token
```

성공 응답:

```http
204 No Content
```

댓글 없음 응답:

```http
404 Not Found
```

```json
{
  "success": false,
  "code": "COMMENT_NOT_FOUND",
  "message": "댓글을 찾을 수 없습니다.",
  "data": null,
  "path": "/posts/1/comments/999",
  "timestamp": "2026-05-22T00:00:00Z",
  "errors": []
}
```

삭제 권한 없음 응답:

```http
403 Forbidden
```

```json
{
  "success": false,
  "code": "COMMENT_DELETE_FORBIDDEN",
  "message": "댓글 삭제 권한이 없습니다.",
  "data": null,
  "path": "/posts/1/comments/1",
  "timestamp": "2026-05-22T00:00:00Z",
  "errors": []
}
```
