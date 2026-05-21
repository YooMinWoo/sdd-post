# 016 게시글 수정 명세

## 목표

로그인한 게시글 작성자가 자신의 게시글 제목과 본문을 수정할 수 있게 한다.

## 사용자 이야기

게시판 사용자는 자신이 작성한 게시글의 제목이나 본문에 오류가 있을 때 내용을 수정하고 싶다.

## 범위

- 게시글 id로 게시글 1건의 제목과 본문을 수정한다.
- 수정 API는 유효한 accessToken이 필요한 인증 API로 둔다.
- 로그인한 회원이 게시글 작성자인 경우에만 수정을 허용한다.
- 요청 본문은 `title`, `content`를 모두 필수로 받는다.
- 제목과 본문 검증은 게시글 작성과 같은 규칙을 사용한다.
- 삭제된 게시글은 수정할 수 없으며 `404 Not Found`, `code=POST_NOT_FOUND`를 반환한다.
- 수정 권한이 없으면 `403 Forbidden`, `code=POST_UPDATE_FORBIDDEN`을 반환한다.
- 수정 성공 시 수정된 게시글 정보를 반환한다.

## 비범위

- 제목 또는 본문 부분 수정
- 게시글 작성자 변경
- 수정 이력 조회
- 수정 시각 컬럼 추가
- 관리자 권한 수정
- 첨부파일 수정

## 도메인 규칙

- 게시글 id와 요청자 회원 id는 양수여야 한다.
- 게시글 수정 요청자는 인증된 회원이어야 한다.
- 게시글 작성자 회원 id와 수정 요청자 회원 id가 같아야 수정할 수 있다.
- 제목은 공백 제거 후 비어 있으면 안 되고 최대 100자까지 허용한다.
- 본문은 공백 제거 후 비어 있으면 안 되고 최대 5,000자까지 허용한다.
- 삭제된 게시글은 수정 가능한 게시글로 보지 않는다.

## API

```http
PATCH /posts/{postId}
Authorization: Bearer access.jwt.token
Content-Type: application/json
```

```json
{
  "title": "수정된 제목",
  "content": "수정된 본문"
}
```

성공 응답:

```http
200 OK
```

```json
{
  "success": true,
  "message": "게시글이 수정되었습니다.",
  "data": {
    "id": 1,
    "title": "수정된 제목",
    "content": "수정된 본문",
    "authorMemberId": 1,
    "author": "minu",
    "createdAt": "2026-05-20T00:00:00Z",
    "commentCount": 2
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
  "code": "POST_UPDATE_FORBIDDEN",
  "message": "게시글 수정 권한이 없습니다.",
  "data": null,
  "path": "/posts/1",
  "timestamp": "2026-05-22T00:00:00Z",
  "errors": []
}
```
