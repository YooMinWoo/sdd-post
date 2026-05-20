# 009 게시글 상세 조회 명세

## 목표

클라이언트가 게시글 식별자로 저장된 게시글 1건을 조회할 수 있게 한다.

## 사용자 이야기

게시판 사용자는 게시글 목록이나 링크에서 특정 게시글을 선택해 제목, 본문, 작성자, 작성 시각을 확인하고 싶다.

## 범위

- 게시글 id로 게시글 1건을 조회한다.
- 조회 응답에는 게시글 id, 제목, 본문, 작성자 회원 id, 작성자 현재 닉네임, 생성 시각을 포함한다.
- 작성자 닉네임은 게시글에 저장된 `authorMemberId`로 회원 정보를 조회해 제공한다.
- 조회 대상 게시글이 없으면 `404 Not Found`와 한글 에러 메시지를 반환한다.
- 실패 응답은 기존 정책에 따라 영문 `code`와 한글 `message`를 함께 제공한다.

## 비범위

- 게시글 목록 조회
- 게시글 검색, 정렬, 페이징
- 게시글 수정, 삭제
- 조회수 증가
- 댓글, 첨부파일, 좋아요 포함 조회
- 작성 당시 닉네임 스냅샷 제공
- 권한/역할 기반 비공개 게시글 조회

## 도메인 규칙

- 게시글 id는 양수여야 한다.
- 게시글 작성자는 저장된 `authorMemberId`로 식별한다.
- 응답의 작성자 닉네임은 조회 시점의 회원 현재 닉네임을 사용한다.
- 게시글이 존재하더라도 작성자 회원을 찾을 수 없으면 데이터 무결성 오류로 보고 서버 오류가 아니라 명시적 실패로 처리한다.

## API

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
    "createdAt": "2026-05-20T00:00:00Z"
  },
  "path": null,
  "timestamp": "2026-05-20T00:00:00Z",
  "errors": []
}
```

게시글 없음 응답:

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
