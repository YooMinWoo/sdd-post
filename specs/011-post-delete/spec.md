# 011 게시글 삭제 명세

## 목표

로그인한 게시글 작성자가 자신의 게시글을 삭제할 수 있게 한다.

## 사용자 이야기

게시판 사용자는 자신이 작성한 게시글이 더 이상 노출되지 않도록 삭제하고 싶다.

## 범위

- 게시글 id로 게시글 1건을 삭제한다.
- 삭제 API는 유효한 accessToken이 필요한 인증 API로 둔다.
- 로그인한 회원이 게시글 작성자인 경우에만 삭제를 허용한다.
- 게시글은 물리적으로 제거하지 않고 `deletedAt`을 기록하는 소프트 삭제로 처리한다.
- 삭제된 게시글은 상세 조회와 목록 조회에서 노출하지 않는다.
- 삭제 대상 게시글이 없거나 이미 삭제된 게시글이면 `404 Not Found`와 한글 에러 메시지를 반환한다.
- 삭제 권한이 없으면 `403 Forbidden`과 한글 에러 메시지를 반환한다.
- 삭제 성공 시 응답 본문 없이 `204 No Content`를 반환한다.

## 비범위

- 게시글 복구
- 관리자 권한 삭제
- 역할/권한 기반 인가 모델 추가
- 삭제 이력 조회
- 댓글, 첨부파일 등 연관 리소스 삭제 정책
- 삭제 사유 입력
- 물리 삭제 또는 배치 정리

## 도메인 규칙

- 게시글 id는 양수여야 한다.
- 게시글 삭제 요청자는 인증된 회원이어야 한다.
- 게시글 작성자 회원 id와 삭제 요청자 회원 id가 같아야 삭제할 수 있다.
- 삭제된 게시글은 `deletedAt` 값을 가진다.
- 이미 삭제된 게시글은 삭제 가능한 게시글로 보지 않는다.
- 삭제된 게시글은 상세 조회와 목록 조회에서 제외한다.
- 삭제 시 작성자 닉네임 조회는 수행하지 않는다.

## API

```http
DELETE /posts/{postId}
Authorization: Bearer access.jwt.token
```

성공 응답:

```http
204 No Content
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
  "path": "/posts/1",
  "timestamp": "2026-05-20T00:00:00Z",
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
  "path": "/posts/1",
  "timestamp": "2026-05-20T00:00:00Z",
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
  "code": "POST_DELETE_FORBIDDEN",
  "message": "게시글 삭제 권한이 없습니다.",
  "data": null,
  "path": "/posts/1",
  "timestamp": "2026-05-20T00:00:00Z",
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
  "path": "/posts/999",
  "timestamp": "2026-05-20T00:00:00Z",
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
  "path": "/posts/0",
  "timestamp": "2026-05-20T00:00:00Z",
  "errors": []
}
```
