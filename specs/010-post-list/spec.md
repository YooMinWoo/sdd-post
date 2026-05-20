# 010 게시글 목록 페이징 조회 명세

## 목표

클라이언트가 게시글 목록을 최신순으로 페이징 조회할 수 있게 한다.

## 사용자 이야기

게시판 사용자는 최신 게시글부터 목록을 확인하고, 필요한 게시글을 선택해 상세 화면으로 이동하고 싶다.

## 범위

- 게시글 목록을 페이지 단위로 조회한다.
- 목록은 게시글 생성 시각 기준 최신순으로 정렬한다.
- 목록 항목 응답은 상세 조회 응답과 유사하지만 게시글 본문 `content`는 포함하지 않는다.
- 목록 항목에는 게시글 id, 제목, 작성자 회원 id, 작성자 현재 닉네임, 생성 시각을 포함한다.
- 응답에는 현재 페이지, 페이지 크기, 전체 게시글 수, 전체 페이지 수, 첫 페이지 여부, 마지막 페이지 여부를 포함한다.
- 조회 가능한 게시글이 없으면 빈 목록과 페이징 메타데이터를 반환한다.

## 비범위

- 게시글 검색
- 게시글 카테고리/태그 필터링
- 인기순, 조회순 등 최신순 외 정렬
- 게시글 본문 미리보기 제공
- 조회수 증가
- 비공개 게시글 권한 정책

## 도메인 규칙

- 페이지 번호는 0 이상이어야 한다.
- 페이지 크기는 1 이상 100 이하여야 한다.
- 작성자 닉네임은 조회 시점의 회원 현재 닉네임을 사용한다.
- 목록 응답의 게시글 항목은 본문을 노출하지 않는다.
- 목록 조회는 작성자 닉네임을 게시글별로 개별 조회하지 않고 배치 조회해 N+1 조회를 방지한다.

## API

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
        "createdAt": "2026-05-20T01:00:00Z"
      },
      {
        "id": 1,
        "title": "첫 번째 게시글",
        "authorMemberId": 1,
        "author": "minu",
        "createdAt": "2026-05-20T00:00:00Z"
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
  "timestamp": "2026-05-20T00:00:00Z",
  "errors": []
}
```

빈 목록 응답:

```json
{
  "success": true,
  "message": "게시글 목록을 조회했습니다.",
  "data": {
    "posts": [],
    "page": 0,
    "size": 10,
    "totalElements": 0,
    "totalPages": 0,
    "first": true,
    "last": true
  },
  "path": null,
  "timestamp": "2026-05-20T00:00:00Z",
  "errors": []
}
```

잘못된 페이징 요청:

```http
400 Bad Request
```

```json
{
  "success": false,
  "code": "INVALID_REQUEST",
  "message": "요청 값이 올바르지 않습니다.",
  "data": null,
  "path": "/posts",
  "timestamp": "2026-05-20T00:00:00Z",
  "errors": []
}
```
